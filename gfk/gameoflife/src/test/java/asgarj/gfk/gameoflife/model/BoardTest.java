package asgarj.gfk.gameoflife.model;

import org.junit.Test;

import static asgarj.gfk.gameoflife.model.GameHelper.readAllLines;
import static org.junit.Assert.*;

/**
 * Created by asgar on 7/8/17.
 */
public class BoardTest {
    @Test
    public void resizeByOffset1() {
        int offset = 1;
        Board state = GameHelper.createBoard(offset, readAllLines("resize-offset-1.in"));
        state.resize();

        Board expected = GameHelper.createBoard(offset, readAllLines("resize-offset-1.out"));
        assertTrue(expected.equals(state));
    }

    @Test
    public void resizeByOffset5() {
        int offset = 5;
        Board state = GameHelper.createBoard(offset, readAllLines("resize-offset-5.in"));
        state.resize();

        Board expected = GameHelper.createBoard(offset, readAllLines("resize-offset-5.out"));
        assertTrue(expected.equals(state));
    }

    @Test
    public void slideBoardByOffset3() {
        int offset = 3;
        Board state = GameHelper.createBoard(offset, readAllLines("resize-offset-3-slide.in"));
        state.resize();

        Board expected = GameHelper.createBoard(offset, readAllLines("resize-offset-3-slide.out"));
        assertTrue(expected.equals(state));
    }
}