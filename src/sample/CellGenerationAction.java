package sample;

import java.util.concurrent.RecursiveAction;

public class CellGenerationAction extends RecursiveAction {

    private boolean[][] initialBoard;
    private boolean[][] destinationBoard;
    private int startRow;
    private int endRow;
    private int startColumn;
    private int endColumn;

    public CellGenerationAction(boolean[][] initialBoard, boolean[][] destinationBoard, int startRow, int endRow, int startColumn, int endColumn) {
        this.initialBoard = initialBoard;
        this.destinationBoard = destinationBoard;
        this.startRow = startRow;
        this.endRow = endRow;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    @Override
    protected void compute() {
        if (isGridSmallEnoughToComputeDirectly()) {
            computeDirectly();
            return;
        }

        int halfRows = (endRow - startRow) / 2;
        int halfColumn = (endColumn - startColumn) / 2;

//        System.out.println("End Row: " + endRow + ", End Column: " + endColumn + ", Half Rows: " + (endRow -));

        if (gridHasMoreRowsThanColumns(halfRows, halfColumn)) {
            splitGridUpByRows(halfRows);
        } else {
            splitGridUpByColumns(halfColumn);
        }
    }

    private boolean isGridSmallEnoughToComputeDirectly() {
        return getAreaOfGrid() < 20;
    }

    private int getAreaOfGrid() {
        return (endRow - startRow) * (endColumn - startColumn);
    }

    private boolean gridHasMoreRowsThanColumns(int halfRows, int halfColumn) {
        return halfRows > halfColumn;
    }

    private void splitGridUpByRows(int halfRows) {
        invokeAll(new CellGenerationAction(initialBoard, destinationBoard, startRow,
                                           startRow + halfRows, startColumn, endColumn),
                new CellGenerationAction(initialBoard,
                                         destinationBoard, startRow + halfRows, endRow, startColumn, endColumn));
    }

    private void splitGridUpByColumns(int halfColumn) {
        invokeAll(new CellGenerationAction(initialBoard, destinationBoard, startRow,
                                           endRow, startColumn , startColumn + halfColumn),
                  new CellGenerationAction(initialBoard, destinationBoard, startRow, endRow,
                                           startColumn + halfColumn, endColumn));
    }

    void computeDirectly() {
        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                int numberOfNeighbors = getNumberOfNeighbors(row, column);

                if (initialBoard[row][column]) {
                    destinationBoard[row][column] = numberOfNeighbors >= 2;

                    if (numberOfNeighbors > 3) {
                        destinationBoard[row][column] = false;
                    }
                } else {
                    destinationBoard[row][column] = numberOfNeighbors == 3;
                }
            }
        }
    }

    private int getNumberOfNeighbors(int row, int col) {
        int neighborCount = 0;
        for (int leftIndex = -1; leftIndex < 2; leftIndex++) {
            for (int topIndex = -1; topIndex < 2; topIndex++) {
                int neighbourRowIndex = row + leftIndex;
                int neighbourColIndex = col + topIndex;

                if (isCenterCellOrOffGrid(leftIndex, topIndex, neighbourRowIndex, neighbourColIndex)) {
                    continue;
                }

                boolean neighbour = initialBoard[neighbourRowIndex][neighbourColIndex];

                if (neighbour) {
                    neighborCount++;
                }
            }
        }

        return neighborCount;
    }

    private boolean isCenterCellOrOffGrid(int leftIndex, int topIndex, int neighbourRowIndex, int neighbourColIndex) {
        return isCenterCell(leftIndex, topIndex) || isOffGrid(neighbourRowIndex, neighbourColIndex);
    }

    private boolean isOffGrid(int neighbourRowIndex, int neighbourColIndex) {
        return (neighbourRowIndex < 0 || neighbourColIndex < 0) || (neighbourRowIndex >= initialBoard.length || neighbourColIndex >= initialBoard[0].length);
    }

    private boolean isCenterCell(int leftIndex, int topIndex) {
        return (leftIndex == 0) && (topIndex == 0);
    }
}
