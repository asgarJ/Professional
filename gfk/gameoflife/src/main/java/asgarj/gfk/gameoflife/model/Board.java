package asgarj.gfk.gameoflife.model;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by asgar on 7/5/17.
 */
public class Board {
    public static final char LIVE_CELL = '\u2588';
    public static final char DEAD_CELL = '.';

    private int rows, cols, offset;
    private boolean[][] cell;
    private int topOffset, bottomOffset, leftOffset, rightOffset;

    public Board(int rows, int cols, int offset) {
        this.rows = rows;
        this.cols = cols;
        this.offset = offset;
        cell = new boolean[rows][cols];
        this.topOffset = rows;
        this.bottomOffset = rows;
        this.leftOffset = cols;
        this.rightOffset = cols;
    }

    public static Board newBoard(List<String> lines, int offset) {
        int rows = lines.size();
        int cols = lines.stream().mapToInt(s -> s.length()).max().getAsInt();
        Board board = new Board(rows, cols, offset);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                board.setSingleCell(row, col, lines.get(row).charAt(col) == '*');
            }
        }
        board.resize();
        return board;
    }

    public void setSingleCell(int row, int col, boolean alive) {
        if (row < 0 || row >= rows)
            throw new IllegalArgumentException(format("row [%d] is out of range", row));
        if (col < 0 || col >= cols)
            throw new IllegalArgumentException(format("col [%d] is out of range", col));
        this.cell[row][col] = alive;
        if (alive) {
            topOffset = Math.min(topOffset, row);
            bottomOffset = Math.min(bottomOffset, rows - row - 1);
            leftOffset = Math.min(leftOffset, col);
            rightOffset = Math.min(rightOffset, cols - col - 1);
        }
    }

    public void resize() {
        boolean resizeRows = topOffset == 0 || bottomOffset == 0;
        boolean resizeCols = leftOffset == 0 || rightOffset == 0;

        if (!resizeRows && !resizeCols) return;

        int newTopOffset = offset - topOffset;
        int newRows = rows + (offset - topOffset) + (offset - bottomOffset);

        int newLeftOffset = offset - leftOffset;
        int newCols = cols + (offset - leftOffset) + (offset - rightOffset);

        boolean[][] newboard = new boolean[newRows][newCols];
        for (int row = 0; row < rows; ++row) {
            if (newTopOffset + row < 0 || newTopOffset + row >= newRows) continue;
            for (int col = 0; col < cols; ++col) {
                if (newLeftOffset + col < 0 || newLeftOffset + col >= newCols) continue;
                newboard[newTopOffset + row][newLeftOffset + col] = cell[row][col];
            }
        }
        cell = newboard;
        rows = newRows;
        cols = newCols;
        topOffset = bottomOffset = leftOffset = rightOffset = offset;
    }

    public void clear() {
        this.topOffset = rows;
        this.bottomOffset = rows;
        this.leftOffset = cols;
        this.rightOffset = cols;
    }

    public boolean[][] getCell() {
        return cell;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
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

        if (rows != board.rows) return false;
        if (cols != board.cols) return false;
        return Arrays.deepEquals(cell, board.cell);
    }

    @Override
    public int hashCode() {
        int result = rows;
        result = 31 * result + cols;
        result = 31 * result + Arrays.deepHashCode(cell);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] rows : this.cell) {
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
}
