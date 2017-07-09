package asgarj.gfk.gameoflife.model;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
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
            while (true) {
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
        boolean[][] temp = new boolean[board.getRows()][board.getCols()];
        for (int row = 0; row < board.getRows(); ++row)
            temp[row] = Arrays.copyOf(board.getCell()[row], board.getCols());
        this.board.clear();
        for (int row = 0; row < board.getRows(); ++row) {
            for (int col = 0; col < board.getCols(); ++col) {
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
     * @param cell
     * @return
     */
    private boolean findStatus(int row, int col, boolean[][] cell) {
        int count = 0;
        for (int i = Math.max(0, row - 1); i <= Math.min(cell.length - 1, row + 1); ++i) {
            for (int j = Math.max(0, col - 1); j <= Math.min(cell[0].length - 1, col + 1); ++j) {
                if (i == row && j == col) continue;
                if (cell[i][j])
                    count++;
            }
        }
        return (!cell[row][col] && count == 3) || (cell[row][col] && (count==2 || count==3));
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public int getStep() {
        return step;
    }

    public Board getBoard() {
        return board;
    }
}
