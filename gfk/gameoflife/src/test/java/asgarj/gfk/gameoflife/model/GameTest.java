package asgarj.gfk.gameoflife.model;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static asgarj.gfk.gameoflife.model.GameHelper.readAllLines;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by asgar on 7/6/17.
 */
public class GameTest {
    private static final int MAX_STEPS = 10;

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

    private void stillLifes(String filename) {
        Path filepath = null;
        Board initialState = null;
        try {
            filepath = Paths.get(GameTest.class.getResource("/" + filename).toURI());
            List<String> lines = Files.readAllLines(filepath, StandardCharsets.US_ASCII);
            initialState = Board.newBoard(lines, Game.DEFAULT_OFFSET);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Game game = new Game(filepath);
        assertEquals(0, game.getStep());
        assertTrue(initialState.equals(game.getBoard()));

        for (int step = 1; step < 10; ++step) {
            game.next();
            assertEquals(step, game.getStep());
            assertTrue(initialState.equals(game.getBoard()));   // state remains same
        }

    }

    private void oscillatorsPeriod2 (String fileState1, String fileState2) {
        Path filepath = null;
        Board firstState = null, secondState = null;
        try {
            filepath = Paths.get(GameTest.class.getResource("/" + fileState1).toURI());
            List<String> lines = Files.readAllLines(filepath, StandardCharsets.US_ASCII);
            firstState = Board.newBoard(lines, Game.DEFAULT_OFFSET);

            Path filepath2 = Paths.get(GameTest.class.getResource("/" + fileState2).toURI());
            lines = Files.readAllLines(filepath2, StandardCharsets.US_ASCII);
            secondState = Board.newBoard(lines, Game.DEFAULT_OFFSET);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Game game = new Game(filepath);
        for (int step = 0; step < 10; ++step, game.next()) {
            assertEquals(step, game.getStep());
            if (step % 2 == 0)
                assertTrue(firstState.equals(game.getBoard()));
            else
                assertTrue(secondState.equals(game.getBoard()));
        }
    }

    private void spaceships(String... fileStates) {
        Path filepath = null;
        try {
            filepath = Paths.get(GameTest.class.getResource("/" + fileStates[0]).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Board[] state = new Board[fileStates.length];
        for (int i = 0; i < fileStates.length; ++i) {
            state[i] = Board.newBoard(readAllLines(fileStates[i]), Game.DEFAULT_OFFSET);
        }
        Game game = new Game(filepath);

        for (int step = 0; step < MAX_STEPS; ++step, game.next()) {
            assertTrue(state[step % state.length].equals(game.getBoard()));
        }
    }

}