package shkalev.model.image;

public enum ImageType {
    PNG,
    JPEG;

    public static ImageType parseType(final String type) {
        switch (type) {
            case "image/png":
                return PNG;
            case "image/jpeg":
                return JPEG;
            default:
                throw new IllegalArgumentException("Unsupported type " + type);
        }
    }


}
