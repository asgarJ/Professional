package asgarj.gfk.gameoflife.model;

import asgarj.gfk.gameoflife.common.GameUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by asgar on 7/6/17.
 */
public class GameTest {
    private static final int MAX_STEPS = 100;

    @Test
    public void block() throws Exception {
        stillLifes("block.in");
    }

    @Test
    public void beehive() throws Exception {
        stillLifes("beehive.in");
    }

    @Test
    public void loaf() throws Exception {
        stillLifes("loaf.in");
    }

    @Test
    public void boat() throws Exception {
        stillLifes("boat.in");
    }

    @Test
    public void tub() throws Exception {
        stillLifes("tub.in");
    }

    @Test
    public void toad() {
        oscillatorsPeriod2("toad.in", "toad_state-2.in");
    }

    @Test
    public void beacon() {
        oscillatorsPeriod2("beacon.in", "beacon_state-2.in");
    }

    @Test
    public void blinker() {
        oscillatorsPeriod2("blinker.in", "blinker_state-2.in");
    }

    @Test
    public void glider() {
        spaceships("glider_state-1.in", "glider_state-2.in", "glider_state-3.in", "glider_state-4.in");
    }

    /**
     * This method tests such games whose state never changes
     * @param filename
     */
    private void stillLifes(String filename) {
        Board expectedState = GameUtils.createBoardFromFile(filename);
        Board initialState = expectedState.clone();
        Game game = new Game(initialState);

        for (int step = 0; step < MAX_STEPS; ++step, game.next()) {
            assertEquals(step, game.getStep());
            assertTrue(expectedState.equals(game.getBoard()));   // state remains same
        }

    }

    /**
     * This method tests such cases where the game state alternates between two states.
     * @param fileState1 name of file contains first state
     * @param fileState2 name of file contains second state
     */
    private void oscillatorsPeriod2 (String fileState1, String fileState2) {
        Board firstState = GameUtils.createBoardFromFile(fileState1);
        Board secondState = GameUtils.createBoardFromFile(fileState2);
        Game game = new Game(firstState.clone());

        for (int step = 0; step < MAX_STEPS; ++step, game.next()) {
            assertEquals(step, game.getStep());
            if (step % 2 == 0)
                assertTrue(firstState.equals(game.getBoard()));
            else
                assertTrue(secondState.equals(game.getBoard()));
        }
    }

    /**
     * Tests whether the given array of states match up with the states of game at each step, periodically.
     * @param fileStates an array of file names contains states.
     */
    private void spaceships(String... fileStates) {
        Board[] state = new Board[fileStates.length];
        for (int i = 0; i < fileStates.length; ++i) {
            state[i] = GameUtils.createBoardFromFile(fileStates[i]);
        }
        Game game = new Game(state[0].clone());
        for (int step = 0; step < MAX_STEPS; ++step, game.next()) {
            assertTrue(state[step % state.length].equals(game.getBoard()));
        }
    }

}