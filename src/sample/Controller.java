package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

import static sample.Cell.BLUE;
import static sample.Cell.WHITE;

public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField rowTextField;

    @FXML
    private TextField columnTextField;

    @FXML
    private Button stopButton;

    @FXML
    private Button resetButton;

    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    private Integer columnCount;
    private Integer rowCount;
    private AtomicBoolean keepRunning = new AtomicBoolean(true);

    public void runApplication() {
        this.keepRunning.set(true);
        new Thread(createTask()).start();
    }

    private Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                start();
                return true;
            }
        };
    }

    private  boolean[][] initialBoard;
    private boolean[][] destinationBoard;
    private Cell[][] rectangles;


    public void start() {


        while (keepRunning.get()) {

            GameOfLifeAdvancer gameOfLifeAdvancer = new GameOfLifeAdvancer(initialBoard,
                    destinationBoard, 0, initialBoard.length-1, 0, initialBoard[0].length - 1);

            forkJoinPool.invoke(gameOfLifeAdvancer);

            printBoard();
            initialBoard = destinationBoard;
            destinationBoard = new boolean[rowCount][columnCount];

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printBoard() {
        for (int i = 0; i < destinationBoard.length; i++) {
            for (int j = 0; j < destinationBoard.length; j++) {
                if (destinationBoard[i][j]) {
                    rectangles[i][j].killCell();
                } else {
                    rectangles[i][j].resuscitateCell();
                }
            }
        }
    }

    public void createGrid(MouseEvent mouseEvent) {
        rowCount = Integer.valueOf(this.rowTextField.getText());
        double cellHeight = 474.0 / rowCount;

        columnCount = Integer.valueOf(this.columnTextField.getText());
        double cellWidth = 400.0 / columnCount;

        rectangles = new Cell[rowCount][columnCount];
        initialBoard = new boolean[rowCount][columnCount];
        destinationBoard = new boolean[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Rectangle node = new Rectangle();
                node.setArcHeight(5.0);
                node.setArcWidth(5.0);
                node.setHeight(cellHeight);
                node.setWidth(cellWidth);
                node.setFill(WHITE);
                node.setStroke(Color.BLACK);
                node.setStrokeType(StrokeType.INSIDE);
                node.setOnMouseClicked(event -> {
                    int rowIndex = GridPane.getRowIndex((Node) event.getSource());
                    int columnIndex = GridPane.getColumnIndex(((Node) event.getSource()));

                    if (node.getFill().equals(BLUE)) {
                        initialBoard[rowIndex][columnIndex] = false;
                        node.setFill(WHITE);
                    } else {
                        initialBoard[rowIndex][columnIndex] = true;
                        node.setFill(BLUE);
                    }
                });

                rectangles[i][j] = new Cell(node, i, j);
                gridPane.add(node, j, i);
            }
        }

        System.out.println(gridPane.getHeight());
        System.out.println(gridPane.getWidth());
    }

    public void stopApplication(MouseEvent mouseEvent) {
        this.keepRunning.set(false);
    }

    public void resetGrid(MouseEvent mouseEvent) {
        gridPane.getChildren().clear();
    }
}
