package shkalev.model.image;

public class Color {
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;


    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public static Color colorFromIntValue(final int argb, final boolean hasAlpha) {
        if (hasAlpha) {
            return new Color((argb >> 16) & 0xff,
                    (argb >> 8) & 0xff,
                    argb & 0xff,
                    (argb >> 24) & 0xff);
        } else {
            return new Color((argb >> 16) & 0xff,
                    (argb >> 8) & 0xff,
                    argb & 0xff);
        }
    }

    public Color subtraction(final Color color) {
        return new Color(Math.abs(red - color.getRed()),
                Math.abs(green - color.getGreen()),
                Math.abs(blue - color.getBlue()),
                Math.abs(alpha - color.getAlpha()));
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public int colorToIntValue() {
        int argb = alpha << 24; // alpha
        argb += blue; // blue
        argb += green << 8; // green
        argb += red << 16; // red
        return argb;
    }
}
