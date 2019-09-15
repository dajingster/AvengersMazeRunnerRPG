package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 40;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);



    public static void addHexagon(TETile[][] world, int xx, int yy, int s, TETile t) {
        for (int y = yy; y < yy + s; y += 1) {
            for (int x = xx - (y - yy); x < xx + s + (y - yy); x += 1) {
                world[x][y] = t;
            }
        }

        for (int y = yy + s; y < yy + 2*s; y += 1) {
            for (int x = xx - (s - 1) + (y - (yy + s)); x < xx + s + (s - 1) - (y - (yy + s)); x += 1) {
                world[x][y] = t;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }

        for (int i = 0; i < HEIGHT - 6; i+= 6) {
            addHexagon(tiles, 4, i, 3, new TETile('#', new Color(216, 128, 128), Color.darkGray,
                    "wall"));
        }

        for (int i = 3; i < HEIGHT - 9; i+= 8) {
            addHexagon(tiles, 9, i, 4, new TETile('#', new Color(216, 128, 128), Color.darkGray,
                    "wall"));
        }

        ter.renderFrame(tiles);
    }
}
