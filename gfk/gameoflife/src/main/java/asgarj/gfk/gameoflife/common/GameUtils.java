package asgarj.gfk.gameoflife.common;

import asgarj.gfk.gameoflife.model.Board;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by asgar on 7/9/17.
 */
public class GameUtils {
    public static Board createBoardFromFile(String filename) {
        List<String> lines = readAllLines(filename);
        Board board = Board.newBoard(lines);
        return board;
    }

    public static Board createBoardFromFile(String filename, int offset) {
        List<String> lines = readAllLines(filename);
        Board board = Board.newBoard(lines, offset);
        return board;
    }

    public static List<String> readAllLines(String filename) {
        List<String> lines = null;
        try {
            Path filepath = Paths.get(GameUtils.class.getResource("/" + filename).toURI());
            lines = Files.readAllLines(filepath, StandardCharsets.US_ASCII);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
