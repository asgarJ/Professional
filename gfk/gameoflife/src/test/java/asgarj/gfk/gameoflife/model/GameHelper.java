package asgarj.gfk.gameoflife.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by asgar on 7/8/17.
 */
public class GameHelper {

    public static List<String> readAllLines(String filename) {
        List<String> lines = null;
        try {
            Path filepath = Paths.get(GameTest.class.getResource("/" + filename).toURI());
            lines = Files.readAllLines(filepath, StandardCharsets.US_ASCII);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static Board createBoard(int offset, List<String> lines) {
        int rows = lines.size();
        int cols = lines.stream().mapToInt(s -> s.length()).max().getAsInt();
        Board board = new Board(rows, cols, offset);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                board.setSingleCell(row, col, lines.get(row).charAt(col) == '*');
            }
        }
        return board;
    }
}
