package shkalev.model.image;

public class SlidingWindow extends Window {
    private int pixelsCount;

    public SlidingWindow(final int row, final int col, final int height, final int width) {
        super(row, col, height, width);
    }

    public SlidingWindow(final int row, final int col, final int height, final int width, final int pixelsCount) {
        super(row, col, height, width);
        this.pixelsCount = pixelsCount;
    }

    public SlidingWindow(final Window window) {
        this(window.getRow(), window.getCol(), window.getHeight(), window.getWidth());
    }

    public void setPixelsCount(final int pixelsCount) {
        this.pixelsCount = pixelsCount;
    }

    public int getPixelsCount() {
        return pixelsCount;
    }
}
