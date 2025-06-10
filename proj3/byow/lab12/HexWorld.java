package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.TreeMap;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static int SIZE = 3;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] Tiles = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                Tiles[x][y] = Tileset.NOTHING;
            }
        }

        DrawAll(Tiles);

        ter.renderFrame(Tiles);
    }


    public static void addHexagon(TETile[][] tiles, int x, int y) {
        TETile chosen = randomTile();
        int length = SIZE;
        int xcor = x;
        int ycor = y;
        int i = 0;
        while (i < SIZE) {
            addLine(tiles, length, xcor, ycor, chosen);
            xcor = xcor - 1;
            ycor = ycor + 1;
            length = length + 2;
            i++;
        }
        int j = 0;
        length = length - 2;
        xcor = xcor + 1;
        while (j < SIZE) {
            addLine(tiles, length, xcor, ycor, chosen);
            xcor = xcor + 1;
            ycor = ycor + 1;
            length = length - 2;
            j++;
        }
    }

    public static void DrawAll(TETile[][] tiles) {
        int startX = SIZE + 1 + 5;
        int startY = 6 * SIZE + 1 + 5;
        DrawBelow(tiles, 3, startX, startY);
        DrawBelow(tiles, 3, startX + 8 * SIZE - 4, startY);
        int X2 = TileRightUp(startX, startY).get("x");
        int Y2 = TileRightUp(startX, startY).get("y");
        DrawBelow(tiles, 4, X2, Y2);
        DrawBelow(tiles, 4, X2 + 4 * SIZE -2, Y2);
        int X3 = TileRightUp(X2, Y2).get("x");
        int Y3 = TileRightUp(X2, Y2).get("y");
        DrawBelow(tiles, 5, X3, Y3);
    }

    public static void DrawBelow(TETile[][] tiles, int n, int x, int y) {
        for (int i = 0; i < n; i++) {
            addHexagon(tiles, x, y);
            x = TileBelow(x, y).get("x");
            y = TileBelow(x, y).get("y");
        }
    }

    private static void addLine(TETile[][] tiles, int length, int x, int y, TETile t) {
        for (int i = 0; i < length; i++) {
            tiles[x + i][y] = t;
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.SAND;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }

    private static TreeMap<String, Integer> TileBelow(int x, int y) {
        TreeMap<String, Integer> newCor = new TreeMap<>();
        newCor.put("x", x);
        newCor.put("y", y - 2 * SIZE);
        return newCor;
    }

    private static TreeMap<String, Integer> TileRightUp(int x, int y) {
        TreeMap<String, Integer> newCor = new TreeMap<>();
        newCor.put("x", x + 2 * SIZE - 1);
        newCor.put("y", y + SIZE);
        return newCor;
    }
}
