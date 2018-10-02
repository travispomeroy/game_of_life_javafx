package sample;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Cell {

    public static final Paint BLUE = Paint.valueOf("4680e3");
    public static final Paint WHITE = Paint.valueOf("fafbfc");

    private final Rectangle rectangle;

    private final int row;
    private final int column;

    public Cell(Rectangle rectangle, int row, int column) {
        this.rectangle = rectangle;
        this.row = row;
        this.column = column;
    }

    public void killCell() {
        this.rectangle.setFill(BLUE);
    }

    public void resuscitateCell() {
        this.rectangle.setFill(WHITE);
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }
}
