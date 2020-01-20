package shkalev.model.image;

public class ImageSizeMismatchException extends Exception {
    private static final long serialVersionUID = -3896927895221904843L;

    public ImageSizeMismatchException(String message) {
        super(message);
    }
}
