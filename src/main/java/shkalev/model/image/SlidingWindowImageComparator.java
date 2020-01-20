package shkalev.model.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowImageComparator {
    private static final Logger LOG = LoggerFactory.getLogger(SlidingWindowImageComparator.class);
    private final ImageMatrix sub;
    private final float startWindowSizeCoeff;
    private final float windowStepCoeff;

    public SlidingWindowImageComparator(final ImageMatrix expected, final ImageMatrix actual, final float startWindowSizeCoeff, final float windowStepCoeff) throws ImageSizeMismatchException {
        if (startWindowSizeCoeff < 2 || startWindowSizeCoeff > 100) {
            throw new IllegalArgumentException("startWindowSizeCoeff must be - 2 <= startWindowSizeCoeff <= 100, but startWindowSizeCoeff = " + startWindowSizeCoeff);
        }
        if (windowStepCoeff < 2 || windowStepCoeff > 100) {
            throw new IllegalArgumentException("startWindowSizeCoeff must be - 2 <= startWindowSizeCoeff <= 100, but startWindowSizeCoeff = " + startWindowSizeCoeff);
        }
        this.startWindowSizeCoeff = startWindowSizeCoeff;
        this.sub = expected.subtraction(actual);
        this.windowStepCoeff = windowStepCoeff;
    }

    public SlidingWindowImageComparator(final ImageMatrix expected, final ImageMatrix actual) throws ImageSizeMismatchException {
        this(expected, actual, 10f, 2f);
    }

    public SlidingWindowImageComparator(final ImageMatrix expected, final ImageMatrix actual, final float startWindowSizeCoeff) throws ImageSizeMismatchException {
        this(expected, actual, startWindowSizeCoeff, 2f);
    }

    public CompletableFuture<List<SlidingWindow>> compareImage(final int startRow,
                                                               final int startCol,
                                                               final int height,
                                                               final int width,
                                                               final int deep) {
        if (sub.getCountOfNotNullPixels() == sub.getCols() * sub.getRows()) {
            final List<SlidingWindow> result = new LinkedList<>();
            result.add(new SlidingWindow(startRow, startCol, height, width, sub.getCols() * sub.getRows()));
            CompletableFuture<List<SlidingWindow>> future = new CompletableFuture<>();
            future.complete(result);
            return future;
        }
        if (sub.getCountOfNotNullPixels() == 0) {
            final List<SlidingWindow> result = new LinkedList<>();
            CompletableFuture<List<SlidingWindow>> future = new CompletableFuture<>();
            future.complete(result);
            return future;
        }
        final int maxSide = Math.max(sub.getRows(), sub.getCols());
        int windowSize = getStartWindowSize(maxSide / startWindowSizeCoeff);
        int windowStep = (int) (windowSize / windowStepCoeff);
        final List<CompletableFuture<List<SlidingWindow>>> futures = new LinkedList<>();
        final Window imageBorder = new Window(startRow, startCol, height, width);
        int countOfList = 0;
        for (int i = 0; i < deep; i++) {
            for (int j = 0; j < deep; j++) {
                final int slideRowStep = windowStep / (1 << i);
                final int slideColStep = windowStep / (1 << j);
                final int slideRowHeight = windowSize / (1 << i);
                final int slideColWidth = windowSize / (1 << j);
                if (slideRowStep < 1 || slideColStep < 1 || slideRowHeight < 2 || slideColWidth < 2) {
                    continue;
                }
                countOfList++;
                final Window slideTemplate = new Window(startRow, startCol, slideRowHeight, slideColWidth);
                futures.add(CompletableFuture.supplyAsync(() ->
                        slideOnImage(imageBorder, slideRowStep, slideColStep, slideTemplate)
                ));
            }
        }
        return filteringSlidingWindows(merge(futures, countOfList));
    }

    private CompletableFuture<List<SlidingWindow>> filteringSlidingWindows(final CompletableFuture<List<SlidingWindow>> future) {
        return future.thenApply(list -> {
            final List<SlidingWindow> result = new LinkedList<>();
            int size = list.size();
            list.sort(Comparator.comparing(SlidingWindow::getPixelsCount)
                    .reversed()
                    .thenComparing(SlidingWindow::gerArea));
            while (size > 0) {
                final ListIterator<SlidingWindow> iterator = list.listIterator();
                if (!iterator.hasNext()) {
                    break;
                }
                final SlidingWindow currentWindow = iterator.next();
                result.add(currentWindow);
                iterator.remove();
                SlidingWindow window;
                while (iterator.hasNext()) {
                    window = iterator.next();
                    if (currentWindow.isIntersection(window)) {
                        iterator.remove();
                    }
                }
                size = list.size();
            }
            return result;
        });
    }

    private CompletableFuture<List<SlidingWindow>> merge(final List<CompletableFuture<List<SlidingWindow>>> futures, final int countOfList) {
        final CompletableFuture<List<SlidingWindow>> future = new CompletableFuture<>();
        final List<SlidingWindow> result = new LinkedList<>();
        final Lock lock = new ReentrantLock();
        final AtomicInteger count = new AtomicInteger(0);
        futures.forEach(f -> f.whenCompleteAsync((list, error) -> {
            if (error != null) {
                future.completeExceptionally(error);
            }
            lock.lock();
            try {
                result.addAll(list);
                if (count.incrementAndGet() == countOfList) {
                    future.complete(result);
                }
            } finally {
                lock.unlock();
            }
        }).exceptionally(e -> {
            LOG.error("An error occurred during the merge ", e);
            return null;
        }));
        return future;
    }

    private List<SlidingWindow> slideOnImage(final Window imageBorder,
                                             final int stepRow,
                                             final int stepCol,
                                             final Window slidingWindowTemplate) {
        final List<SlidingWindow> result = new LinkedList<>();
        SlidingWindow slidingWindow = new SlidingWindow(slidingWindowTemplate);
        for (int row = imageBorder.getRow(); row < imageBorder.getRow() + imageBorder.getHeight(); row += stepRow) {
            for (int col = imageBorder.getCol(); col < imageBorder.getRow() + imageBorder.getWidth(); col += stepCol) {
                slidingWindow.setRow(row);
                slidingWindow.setCol(col);
                int notNullPixels = getNotNullPixelsIn(slidingWindow, imageBorder);
                if (notNullPixels != 0) {
                    slidingWindow.setPixelsCount(notNullPixels);
                    result.add(slidingWindow);
                    slidingWindow = new SlidingWindow(slidingWindowTemplate);

                }
            }
        }
        return result;
    }

    private int getNotNullPixelsIn(final SlidingWindow window, final Window imageBorder) {
        int result = 0;
        for (int row = window.getRow(); row < window.getRow() + window.getHeight(); row++) {
            for (int col = window.getCol(); col < window.getCol() + window.getWidth(); col++) {
                if (col < imageBorder.getCol() + imageBorder.getWidth() && row < imageBorder.getRow() + imageBorder.getHeight()) {
                    if (!sub.isNull(row, col)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    private int getStartWindowSize(final float side) {
        int pow = 0;
        float n = side;
        while (n > 1) {
            pow++;
            n = n / 2;
        }
        return 1 << pow - 1;
    }

    public static float getSideCoef(final int side) {
        if (side < 100) {
            return 2f;
        }
        if (side < 600) {
            return 5f;
        }
        return 10f;
    }
}
