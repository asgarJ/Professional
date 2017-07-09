package asgarj.gfk.gameoflife.model;

import org.junit.Test;

import java.util.List;

import static asgarj.gfk.gameoflife.common.GameUtils.readAllLines;
import static org.junit.Assert.*;

/**
 * Created by asgar on 7/8/17.
 */
public class BoardTest {
    @Test
    public void resizeByOffset1() {
        int offset = 1;
        Board state = createBoard(offset, readAllLines("resize-offset-1.in"));
        Board expected = createBoard(offset, readAllLines("resize-offset-1.out"));

        state.resize();
        assertTrue(expected.equals(state));
    }

    @Test
    public void resizeByOffset5() {
        int offset = 5;
        Board state = createBoard(offset, readAllLines("resize-offset-5.in"));
        Board expected = createBoard(offset, readAllLines("resize-offset-5.out"));

        state.resize();
        assertTrue(expected.equals(state));
    }

    @Test
    public void slideBoardByOffset3() {
        int offset = 3;
        Board state = createBoard(offset, readAllLines("resize-offset-3-slide.in"));
        Board expected = createBoard(offset, readAllLines("resize-offset-3-slide.out"));

        System.out.println(state);
        state.resize();
        System.out.println(state);
        System.out.println(expected);
        assertTrue(expected.equals(state));
    }

    private Board createBoard(int offset, List<String> lines) {
        int rowCount = lines.size();
        int columnCount = lines.stream().mapToInt(s -> s.length()).max().getAsInt();
        Board board = new Board(rowCount, columnCount, offset);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                board.setSingleCell(row, col, lines.get(row).charAt(col) == '*');
            }
        }
        return board;
    }
}