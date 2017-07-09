package asgarj.gfk.gameoflife.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by asgar on 7/5/17.
 */
public class Game {
    public static final int DEFAULT_OFFSET = 1;
    private static final long DELAY_MilliSeconds = 500L;

    private int step;
    private Board board;
    private Thread thread;
    private volatile boolean running = true;

    public Game(Board board) {
        this.step = 0;
        this.board = board;
        Runnable r = getRunnable();
        thread = new Thread(r, "Game of Life");
    }

    public Game(Path inputPath) {
       this(inputPath, DEFAULT_OFFSET);
    }

    public Game(Path inputPath, int offset) {
        this.step = 0;
        try {
            List<String> lines = Files.readAllLines(inputPath);
            this.board = Board.newBoard(lines, offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runnable r = getRunnable();
        thread = new Thread(r, "Game of Life");
    }

    private Runnable getRunnable() {
        return () -> {
            while (running) {
                System.out.printf("------ State %d ------\n", step);
                System.out.println(board);
                next();
                try {
                    TimeUnit.MILLISECONDS.sleep(DELAY_MilliSeconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     *
     */
    public void next() {
        boolean[][] temp = new boolean[board.getRowCount()][board.getColumnCount()];
        for (int row = 0; row < board.getRowCount(); ++row)
            temp[row] = Arrays.copyOf(board.getCells()[row], board.getColumnCount());
        this.board.clear();
        for (int row = 0; row < board.getRowCount(); ++row) {
            for (int col = 0; col < board.getColumnCount(); ++col) {
                boolean status = findStatus(row, col, temp);
                this.board.setSingleCell(row, col, status);
            }
        }
        this.board.resize();
        ++this.step;
    }

    /**
     * finds the life status of the cell for the next step
     * @param row
     * @param col
     * @param cells
     * @return
     */
    private boolean findStatus(int row, int col, boolean[][] cells) {
        int count = 0;
        for (int i = Math.max(0, row - 1); i <= Math.min(cells.length - 1, row + 1); ++i) {
            for (int j = Math.max(0, col - 1); j <= Math.min(cells[0].length - 1, col + 1); ++j) {
                if (i == row && j == col) continue;
                if (cells[i][j])
                    count++;
            }
        }
        return (!cells[row][col] && count == 3) || (cells[row][col] && (count==2 || count==3));
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        this.running = false;
    }

    public int getStep() {
        return step;
    }

    public Board getBoard() {
        return board;
    }
}
