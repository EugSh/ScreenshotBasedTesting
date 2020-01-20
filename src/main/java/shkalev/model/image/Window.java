package shkalev.model.image;

public class Window {
    private int row;
    private int col;
    private int height;
    private int width;

    public Window(int row, int col, int height, int width) {
        this.row = row;
        this.col = col;
        this.height = height;
        this.width = width;
    }

    public boolean isIntersection(final Window window) {
        return getIntersectionArea(window) != 0;
    }

    public int getIntersectionArea(final Window window) {
        final int left = Math.max(window.getCol(), col);
        final int right = Math.min(window.getCol() + window.getWidth(), col + width);
        final int top = Math.max(window.getRow(), row);
        final int bottom = Math.min(window.getRow() + window.getHeight(), row + height);
        final int width = right - left;
        final int height = bottom - top;
        if (width < 0 || height < 0) {
            return 0;
        }
        return width * height;
    }

    public int gerArea() {
        return height * width;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getCol() {
        return col;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "Window{" +
                "row=" + row +
                ", col=" + col +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
