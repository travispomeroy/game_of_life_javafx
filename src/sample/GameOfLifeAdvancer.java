package sample;

import java.util.concurrent.RecursiveAction;

public class GameOfLifeAdvancer extends RecursiveAction {

    private boolean[][] initialBoard;
    private boolean[][] destinationBoard;
    private int startRow;
    private int endRow;
    private int startColumn;
    private int endColumn;

    public GameOfLifeAdvancer(boolean[][] initialBoard, boolean[][] destinationBoard, int startRow, int endRow, int startColumn, int endColumn) {
        this.initialBoard = initialBoard;
        this.destinationBoard = destinationBoard;
        this.startRow = startRow;
        this.endRow = endRow;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    @Override
    protected void compute() {
        if (getArea() < 20) {
            computeDirectly();
            return;
        }

        int halfRows = (endRow - startColumn) / 2;
        int halfColumn = (endColumn - startColumn) / 2;

        if (halfRows > halfColumn) {
            invokeAll(new GameOfLifeAdvancer(initialBoard, destinationBoard, startRow,
                    startRow + halfRows, startColumn, endColumn),
                    new GameOfLifeAdvancer(initialBoard,
                    destinationBoard, startRow + halfRows + 1, endRow, startColumn, endColumn));
        } else {
            invokeAll(new GameOfLifeAdvancer(initialBoard, destinationBoard, startRow,
                    endRow, startColumn , startColumn + halfColumn),
                    new GameOfLifeAdvancer(initialBoard, destinationBoard, startRow, endRow,
                     startColumn + halfColumn + 1, endColumn));
        }
    }

    private void computeDirectly() {
        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                int numberOfNeighbors = getNumberOfNeighbors(row, column);

                if (initialBoard[row][column]) {
                    destinationBoard[row][column] = true;

                    if (numberOfNeighbors < 2) {
                        destinationBoard[row][column] = false;
                    }

                    if (numberOfNeighbors > 3) {
                        destinationBoard[row][column] = false;
                    }
                } else {
                    destinationBoard[row][column] = false;

                    if (numberOfNeighbors == 3) {
                        destinationBoard[row][column] = true;
                    }
                }
            }
        }
    }

    private int getNumberOfNeighbors(int row, int col) {
        int neighborCount = 0;
        for (int leftIndex = -1; leftIndex < 2; leftIndex++) {
            for (int topIndex = -1; topIndex < 2; topIndex++) {
                if ((leftIndex == 0) && (topIndex == 0)) {
                    continue;
                }

                int neighbourRowIndex = row + leftIndex;
                int neighbourColIndex = col + topIndex;

                if (neighbourRowIndex < 0) {
                    neighbourRowIndex = initialBoard.length + neighbourRowIndex;
                }

                if (neighbourColIndex < 0) {
                    neighbourColIndex = initialBoard[0].length + neighbourColIndex;
                }

                boolean neighbour =
                        initialBoard[neighbourRowIndex % initialBoard.length][neighbourColIndex % initialBoard[0].length];

                if (neighbour) {
                    neighborCount++;
                }
            }
        }

        return neighborCount;
    }

    private int getArea() {
        return (endRow - startRow) * (endColumn - startColumn);
    }
}
