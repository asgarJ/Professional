package asgarj.gfk.gameoflife.model;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by asgar on 7/5/17.
 */
public class Board implements Cloneable {
    public static final int DEFAULT_OFFSET = 1;
    public static final char LIVE_CELL = '\u2588';
    public static final char DEAD_CELL = '.';

    private int rowCount, columnCount, offset;
    private boolean[][] cells;
    private int topOffset, bottomOffset, leftOffset, rightOffset;

    public Board(int rowCount, int columnCount) {
        this(rowCount, columnCount, DEFAULT_OFFSET);
    }

    public Board(int rowCount, int columnCount, int offset) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.offset = offset;
        this.cells = new boolean[rowCount][columnCount];
        this.topOffset = rowCount;
        this.bottomOffset = rowCount;
        this.leftOffset = columnCount;
        this.rightOffset = columnCount;
    }

    /**
     * Static helper method to create an instance of Board.
     * Resizes the board at the end to make sure that there's some padding on the edges.
     * @param lines rows of the grid
     * @param offset desired offset from sides to be maintained after each step.
     * @return new Board instance
     */
    public static Board newBoard(List<String> lines, int offset) {
        int rowCount = lines.size();
        int columnCount = lines.stream().mapToInt(s -> s.length()).max().getAsInt();
        Board board = new Board(rowCount, columnCount, offset);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                board.setSingleCell(row, col, lines.get(row).charAt(col) == '*');
            }
        }
        board.resize();
        return board;
    }

    public static Board newBoard(List<String> lines) {
        return newBoard(lines, DEFAULT_OFFSET);
    }

    public void setSingleCell(int row, int col, boolean alive) {
        if (row < 0 || row >= rowCount)
            throw new IllegalArgumentException(format("row [%d] is out of range", row));
        if (col < 0 || col >= columnCount)
            throw new IllegalArgumentException(format("col [%d] is out of range", col));
        this.cells[row][col] = alive;
        if (alive) {
            topOffset = Math.min(topOffset, row);
            bottomOffset = Math.min(bottomOffset, rowCount - row - 1);
            leftOffset = Math.min(leftOffset, col);
            rightOffset = Math.min(rightOffset, columnCount - col - 1);
        }
    }

    public void resize() {
        boolean resizeRows = topOffset == 0 || bottomOffset == 0;
        boolean resizeCols = leftOffset == 0 || rightOffset == 0;

        if (!resizeRows && !resizeCols) return;

        int newTopOffset = offset - topOffset;
        int newRows = rowCount + (offset - topOffset) + (offset - bottomOffset);

        int newLeftOffset = offset - leftOffset;
        int newCols = columnCount + (offset - leftOffset) + (offset - rightOffset);

        boolean[][] newboard = new boolean[newRows][newCols];
        for (int row = 0; row < rowCount; ++row) {
            if (newTopOffset + row < 0 || newTopOffset + row >= newRows) continue;
            for (int col = 0; col < columnCount; ++col) {
                if (newLeftOffset + col < 0 || newLeftOffset + col >= newCols) continue;
                newboard[newTopOffset + row][newLeftOffset + col] = cells[row][col];
            }
        }
        cells = newboard;
        rowCount = newRows;
        columnCount = newCols;
        topOffset = bottomOffset = leftOffset = rightOffset = offset;
    }

    public void clear() {
        this.topOffset = rowCount;
        this.bottomOffset = rowCount;
        this.leftOffset = columnCount;
        this.rightOffset = columnCount;
    }

    public boolean[][] getCells() {
        return cells;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;

        Board board = (Board) o;

        if (rowCount != board.rowCount) return false;
        if (columnCount != board.columnCount) return false;
        return Arrays.deepEquals(cells, board.cells);
    }

    @Override
    public int hashCode() {
        int result = rowCount;
        result = 31 * result + columnCount;
        result = 31 * result + Arrays.deepHashCode(cells);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] rows : this.cells) {
            for (boolean alive : rows) {
                if (alive) {
                    sb.append(LIVE_CELL);
                } else {
                    sb.append(DEAD_CELL);
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public Board clone() {
        Board clone = new Board(rowCount, columnCount, offset);
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                clone.setSingleCell(i, j, this.cells[i][j]);
        return clone;
    }
}
