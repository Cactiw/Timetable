package Timetable.model.Properties;

import org.springframework.lang.NonNull;

public class BorderProperties {
    private final boolean rightBorder;
    private final boolean leftBorder;

    public BorderProperties(final boolean rightBorder, final boolean leftBorder) {
        this.rightBorder = rightBorder;
        this.leftBorder = leftBorder;
    }

    public BorderProperties() {
        this.rightBorder = true;
        this.leftBorder = true;
    }

    public boolean getRightBorder() {
        return rightBorder;
    }

    @NonNull
    public BorderProperties setRightBorder(final boolean newRightBorder) {
        return new BorderProperties(
                newRightBorder,
                this.leftBorder
        );
    }

    public boolean getLeftBorder() {
        return leftBorder;
    }

    @NonNull
    public BorderProperties setLeftBorder(@NonNull final Boolean newLeftBorder) {
        return new BorderProperties(
                this.rightBorder,
                newLeftBorder
        );
    }
}
