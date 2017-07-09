package asgarj.gfk.gameoflife.controller;

import asgarj.gfk.gameoflife.common.GameUtils;
import asgarj.gfk.gameoflife.model.Board;
import asgarj.gfk.gameoflife.model.Game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by asgar on 7/5/17.
 */
public class DriverProgram {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream inputStream = DriverProgram.class.getResourceAsStream("/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) { e.printStackTrace(); }

        long duration = Long.parseLong(properties.getProperty("duration"));
        int offset = Integer.parseInt(properties.getProperty("offset"));
        String filename = properties.getProperty("inputfile");

        Board board = GameUtils.createBoardFromFile(filename, offset);
        Game game = new Game(board);
        try {
            game.start();
            TimeUnit.SECONDS.sleep(duration);
            game.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
