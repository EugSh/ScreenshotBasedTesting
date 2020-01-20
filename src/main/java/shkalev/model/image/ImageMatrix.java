package shkalev.model.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageMatrix {
    private final int[][] pixelMatrix;
    private final int cols;
    private final int rows;
    private final boolean hasAlpha;
    private final int pixelLength;
    private final int countOfNotNullPixels;

    public ImageMatrix(final BufferedImage bufferedImage) {
        this(bufferedImage.getHeight(),
                bufferedImage.getWidth(),
                bufferedImage.getAlphaRaster() != null,
                ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData());
    }

    public ImageMatrix(final int rows,
                       final int cols,
                       final boolean hasAlpha,
                       final byte[] pixels) {
        this.cols = cols;
        this.rows = rows;
        this.hasAlpha = hasAlpha;
        this.pixelLength = hasAlpha ? 4 : 3;
        this.pixelMatrix = new int[rows][cols];
        int countOfNotNullPixels = 0;
        for (int pixel = 0, row = 0, col = 0; pixel + pixelLength - 1 < pixels.length; ) {
            int argb = -16777216; // 255 alpha
            if (hasAlpha) {
                argb += (((int) pixels[pixel++] & 0xff) << 24); // alpha
            }
            argb += ((int) pixels[pixel++] & 0xff); // blue
            argb += (((int) pixels[pixel++] & 0xff) << 8); // green
            argb += (((int) pixels[pixel++] & 0xff) << 16); // red
            this.pixelMatrix[row][col] = argb;
            countOfNotNullPixels = argb == 0 ? countOfNotNullPixels : countOfNotNullPixels + 1;
            col++;
            if (col == cols) {
                col = 0;
                row++;
            }
        }
        this.countOfNotNullPixels = countOfNotNullPixels;
    }

    private ImageMatrix(final int[][] pixelMatrix,
                        final int rows,
                        final int cols,
                        final boolean hasAlpha,
                        final int pixelLength,
                        final int countOfNotNullPixels) {
        this.pixelMatrix = pixelMatrix;
        this.cols = cols;
        this.rows = rows;
        this.hasAlpha = hasAlpha;
        this.pixelLength = pixelLength;
        this.countOfNotNullPixels = countOfNotNullPixels;
    }

    public boolean hasAlpha() {
        return hasAlpha;
    }

    public boolean isNull(final int row, final int col) {
        return pixelMatrix[row][col] == 0;
    }


    public byte[] getPixelsAsByteArray() {
        final byte[] result = new byte[rows * cols * pixelLength];
        for (int pixel = 0, row = 0, col = 0; pixel + pixelLength - 1 < result.length; ) {
            final Color color = getColor(row, col);
            if (hasAlpha) {
                result[pixel++] = (byte) color.getAlpha();
            }
            result[pixel++] = (byte) color.getBlue();
            result[pixel++] = (byte) color.getGreen();
            result[pixel++] = (byte) color.getRed();
            col++;
            if (col == cols) {
                col = 0;
                row++;
            }
        }
        return result;
    }

    public Color getColor(final int row, final int col) {
        if (isRowAndColCorrect(row, col)) {
            return Color.colorFromIntValue(pixelMatrix[row][col], hasAlpha);
        }
        throw new IllegalArgumentException("Argument must be - 0 <= row < " + rows + " and 0 <= col < " + cols);
    }

    private boolean isRowAndColCorrect(final int row, final int col) {
        return row < rows && row >= 0 && col < cols && col >= 0;
    }

    public int getCountOfNotNullPixels() {
        return countOfNotNullPixels;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getRed(final int row, final int col) {
        return getColor(row, col).getRed();
    }

    public int getGreen(final int row, final int col) {
        return getColor(row, col).getGreen();
    }

    public int getBlue(final int row, final int col) {
        return getColor(row, col).getBlue();
    }

    public int getAlpha(final int row, final int col) {
        return getColor(row, col).getAlpha();
    }

    public void setColor(final int row, final int col, final Color color) {
        pixelMatrix[row][col] = color.colorToIntValue();
    }

    public ImageMatrix subtraction(final ImageMatrix matrix) throws ImageSizeMismatchException {
        if (matrix.getCols() == cols && matrix.getRows() == rows) {
            int countOfNotNullPixels = 0;
            final int[][] pixels = new int[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    pixels[row][col] = getColor(row, col)
                            .subtraction(matrix.getColor(row, col))
                            .colorToIntValue();
                    countOfNotNullPixels = pixels[row][col] == 0 ? countOfNotNullPixels : countOfNotNullPixels + 1;
                }
            }
            return new ImageMatrix(pixels, rows, cols, hasAlpha, pixelLength, countOfNotNullPixels);
        }
        throw new ImageSizeMismatchException("size not equals");
    }

    public void fillRectangle(final int row, final int col, final int height, final int width, final Color color) {
        final boolean correctArgs = isRowAndColCorrect(row, col) &&
                isRowAndColCorrect(row + height, col + height) &&
                height > 0 &&
                width > 0;
        if (correctArgs) {
            for (int i = 0; i < height + 1; i++) {
                pixelMatrix[row + i][col] = color.colorToIntValue();
                pixelMatrix[row + i][col + width] = color.colorToIntValue();
            }

            for (int j = 1; j < width; j++) {
                pixelMatrix[row][j + col] = color.colorToIntValue();
                pixelMatrix[row + height][j + col] = color.colorToIntValue();
            }
            return;
        }
        throw new IllegalArgumentException("Argument must be - 0 <= row < row + height < " + rows + " and 0 <= col < col + width < " + cols);
    }

    public void fillLiteRectangle(final int row, final int col, final int height, final int width, final Color color) {
        for (int i = 0; i < height; i++) {
            if (row + i >= 0 && row + i < rows && col >= 0 && col < cols) {
                pixelMatrix[row + i][col] = color.colorToIntValue();
            }
            if (row + i >= 0 && row + i < rows && col + width >= 0 && col + width - 1 < cols) {
                pixelMatrix[row + i][col + width - 1] = color.colorToIntValue();
            }
        }

        for (int j = 1; j < width; j++) {
            if (row >= 0 && row < rows && col + j >= 0 && col + j < cols) {
                pixelMatrix[row][j + col] = color.colorToIntValue();
            }
            if (row + height - 1 >= 0 && row + height - 1 < rows && col + j >= 0 && col + j < cols) {
                pixelMatrix[row + height - 1][j + col] = color.colorToIntValue();
            }
        }
    }
}