package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Cell {

    public static final Paint BLUE = Paint.valueOf("4680e3");
    public static final Paint WHITE = Paint.valueOf("fafbfc");

    private final Rectangle rectangle;

    private final List<Consumer<? super MouseEvent>> mouseClickEvents = new ArrayList<>();

    private final int row;
    private final int column;

    public Cell(Dimensions dimensions, int row, int column) {
        this.rectangle = createRectangle(dimensions.getHeight(), dimensions.getWidth());
        this.row = row;
        this.column = column;
    }

    private Rectangle createRectangle(double cellHeight, double cellWidth) {
        Rectangle node = new Rectangle();
        node.setArcHeight(5.0);
        node.setArcWidth(5.0);
        node.setHeight(cellHeight);
        node.setWidth(cellWidth);
        node.setFill(WHITE);
        node.setStroke(Color.BLACK);
        node.setStrokeType(StrokeType.INSIDE);
        node.setOnMouseClicked(this::onMouseClick);
        return node;
    }

    private void onMouseClick(MouseEvent mouseEvent) {
        if (this.rectangle.getFill().equals(BLUE)) {
            this.rectangle.setFill(WHITE);
        } else {
            this.rectangle.setFill(BLUE);
        }

        mouseClickEvents.forEach(consumer -> consumer.accept(mouseEvent));
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

    public boolean isCellBlue() {
        return this.rectangle.getFill().equals(BLUE);
    }

    public void addOnMouseClickedEvent(EventHandler<? super MouseEvent> mouseEvent) {
        this.mouseClickEvents.add(mouseEvent::handle);
    }
}
