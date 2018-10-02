package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    public static final double GRID_HEIGHT = 474.0;
    public static final double GRID_WIDTH = 400.0;
    @FXML
    private GridPane gridPane;

    @FXML
    private TextField rowTextField;

    @FXML
    private TextField columnTextField;

    private int columnCount;
    private int rowCount;
    private boolean[][] initialBoard;
    private boolean[][] destinationBoard;

    private ForkJoinPool forkJoinPool = new ForkJoinPool();
    private Cell[][] rectangles;
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

    public void start() throws InterruptedException {
        while (keepRunning.get()) {
            calculateNeighbors();
            updateView();
            resetBoard();
            sleep();
        }
    }

    private void calculateNeighbors() {
        CellGenerationAction cellGenerationAction = new CellGenerationAction(initialBoard,
                                                                             destinationBoard, 0, initialBoard.length, 0, initialBoard[0].length);
        forkJoinPool.invoke(cellGenerationAction);
    }

    private void resetBoard() {
        initialBoard = destinationBoard;
        destinationBoard = new boolean[rowCount][columnCount];
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(500);
    }

    private void updateView() {
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
        columnCount = Integer.valueOf(this.columnTextField.getText());

        double cellHeight = GRID_HEIGHT / rowCount;
        double cellWidth = GRID_WIDTH / columnCount;

        Dimensions dimensions = new Dimensions(cellHeight, cellWidth);

        rectangles = new Cell[rowCount][columnCount];
        initialBoard = new boolean[rowCount][columnCount];
        destinationBoard = new boolean[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                Cell cell = new Cell(dimensions, i, j);
                cell.addOnMouseClickedEvent(event -> updateInitialBoard(cell, event));
                rectangles[i][j] = cell;
                gridPane.add(cell.getRectangle(), j, i);
            }
        }
    }

    private void updateInitialBoard(Cell cell, MouseEvent event) {
        int rowIndex = GridPane.getRowIndex((Node) event.getSource());
        int columnIndex = GridPane.getColumnIndex(((Node) event.getSource()));
        initialBoard[rowIndex][columnIndex] = cell.isCellBlue();
    }

    public void stopApplication(MouseEvent mouseEvent) {
        this.keepRunning.set(false);
    }

    public void resetGrid(MouseEvent mouseEvent) {
        gridPane.getChildren().clear();
    }
}
