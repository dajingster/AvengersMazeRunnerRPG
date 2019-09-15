package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.List;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();

    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

    private TETile[][] visible = initworld(WIDTH, HEIGHT);
    private TETile[][] world = initworld(WIDTH, HEIGHT);

    private Random random;
    private Player player;
    private String playerName = "Player1";
    private boolean startGame = false;
    private boolean inputString = true;

    private String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private int countRooms = 0;
    private Room[] tempRooms = new Room[50];

    private int THANOS = 0;

    private TETile[] thanosTimeline = {Tileset.THANOS, Tileset.THANOS,
        Tileset.THANOS, Tileset.THANOS, Tileset.MULTIPLY,
        Tileset.DEATH, Tileset.LAG, Tileset.VISION};

    List<TETile> TT = Arrays.asList(thanosTimeline);
    private final ArrayList<TETile> thanosTimelineet = new ArrayList<>(TT);

    private static void loadGame() {
        File f = new File("./game.txt");
        if (f.exists()) {
            Main.load(f);
        }
    }

    private void saveGame() {
        File f = new File("./game.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fis = new FileOutputStream(f);
            ObjectOutputStream ois = new ObjectOutputStream(fis);
            ois.writeObject(this);
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }


    private void startGame() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);

        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);

        startGame = true;
    }


    private void menu() {
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.white);
        StdDraw.text((float) WIDTH / 2, (float) HEIGHT / 2 + 6.25, "Endgame");
        StdDraw.text((float) WIDTH / 2, (float) HEIGHT / 2 - 6.25,
                "New(N)         Load(L)         Quit(Q)         Set Player Name(P)");
        StdDraw.text((float) WIDTH / 2, (float) HEIGHT / 2, "Defeat all THANOS to win!");
        StdDraw.show();
    }

    public void interactWithKeyboard() {
        inputString = false;
        if (!startGame) {
            startGame();
        }
        menu();
        char input;
        input = getInput("qlnp");
        if (input == 'n') {
            random = new Random(getSeed());
            drawBoard();
        } else if (input == 'l') {
            loadGame();
        } else if (input == 'q') {
            System.exit(0);
        } else if (input == 'p') {
            changeName();
        }
        playGame();
    }

    public void playGame() {
        TETile cursor;
        ter.renderFrame(visible);
        writeText(WIDTH - 10, HEIGHT - 3, 2, 2,
                "THANOS warriors left: " + THANOS);
        while (THANOS > 0) {
            while (!StdDraw.hasNextKeyTyped()) {
                cursor = mouse();
                writeText(40, 4, 4, 3, playerName);
                if (cursor == null) {
                    rewrite(10, HEIGHT - 3, 4, 3);
                } else if (cursor.equals(Tileset.FLOOR)) {
                    writeText(10, HEIGHT - 3, 4, 3, "FLOOR");
                } else if (cursor.equals(Tileset.WALL)) {
                    writeText(10, HEIGHT - 3, 4, 3, "BRICK");
                } else if (cursor.equals(Tileset.PLAYER)) {
                    writeText(10, HEIGHT - 3, 4, 3, "Player");
                } else if (thanosTimelineet.contains(cursor)) {
                    writeText(10, HEIGHT - 3, 4, 3, "THANOS");
                } else if (cursor.equals(Tileset.NOTHING)) {
                    writeText(10, HEIGHT - 3, 4, 3, "CAN'T SEE");
                }
            }
            if (helperKeyboard(nextKeyTyped())) {
                ter.renderFrame(visible);
                writeText(WIDTH - 10, HEIGHT - 3, 2, 2,
                        "THANOS warriors left: " + THANOS);
                decayEffect();
            }
        }
        gameWon();
    }

    private void decayEffect() {
        if (player.sight < 8) {
            if (random.nextInt(10) == 0) {
                player.sight++;
            }
        }
    }

    private boolean helperKeyboard(char input) {
        if (input == 'a') {
            player.moveLeft();
            return true;
        } else if (input == 's') {
            player.moveDown();
            return true;
        } else if (input == 'd') {
            player.moveRight();
            return true;
        } else if (input == 'w') {
            player.moveUp();
            return true;
        } else if (input == ':') {
            writeText(5, HEIGHT - 3, 4, 3, "PRESS Q TO QUIT");
            input = buffer();
            if (input == 'q') {
                saveGame();
                System.exit(0);
            }
            return helperKeyboard(input);
        }
        return false;
    }

    private void rewrite(double x, double y, double halfWidth, double halfHeight) {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(x, y, halfWidth, halfHeight);
        StdDraw.setPenColor(Color.WHITE);
    }


    private char getInput(String chars) {
        char input = 0;
        while (chars.indexOf(input) == -1) {
            if (StdDraw.hasNextKeyTyped()) {
                input = nextKeyTyped();
            }
        }
        return input;
    }

    private char nextKeyTyped() {
        char input = StdDraw.nextKeyTyped();
        return Character.toLowerCase(input);
    }

    private long getSeed() {
        long result = 0;
        writeText(WIDTH / 2, HEIGHT / 2, WIDTH / 2, 5,
                "Enter RNG seed, then press s to start");
        char input = getInput("0123456789");
        while (input != 's') {
            result = 10 * result + Character.getNumericValue(input);
            writeText(WIDTH / 2, HEIGHT / 2, WIDTH, HEIGHT / 20, Long.toString(result));
            input = getInput("0123456789s");
        }
        return result;
    }

    private void changeName() {
        writeText(WIDTH / 2, HEIGHT / 2, WIDTH / 2, 5,
                "Enter Player Name: Must be alphanumeric, press / when done");
        char input = getInput(CHARACTERS);
        String result = "";
        while (input != '/') {
            result += input;
            writeText(WIDTH / 2, HEIGHT / 2, WIDTH, HEIGHT / 20, result);
            input = getInput(CHARACTERS + '/');
        }
        playerName = result;
        interactWithKeyboard();
    }

    private void writeText(double x, double y, double halfWidth, double halfHeight, String msg) {
        rewrite(x, y, halfWidth, halfHeight);
        StdDraw.text(x, y, msg);
        StdDraw.show();
    }


    private char buffer() {
        ///waits for a keyboard input
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return nextKeyTyped();
            }
        }
    }

    private TETile mouse() {
        try {
            return visible[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }


    private void youLose() {
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH * 16);
        StdDraw.setYscale(0, HEIGHT * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH * 16 / 2, HEIGHT * 16 / 2 + 100, "THANOS had all the infinity stones");
        StdDraw.text(WIDTH * 16 / 2, HEIGHT * 16 / 2 - 45 + 100, ":( Press any key to quit");
        StdDraw.show();

        buffer();
        System.exit(0);
    }

    private void gameWon() {
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH * 16);
        StdDraw.setYscale(0, HEIGHT * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH * 16 / 2, HEIGHT * 16 / 2 + 100, "You Win! Press any key to quit");
        StdDraw.show();

        buffer();
        System.exit(0);
    }


    public TETile[][] interactWithInputString(String input) {

        input = input.toLowerCase().replace(":q", "q");

        char nxt = input.charAt(0);
        input = input.substring(1);

        if (nxt == 'q') {

            return visible;
        } else if (nxt == 'l') {
            File f = new File("./game.txt");
            if (f.exists()) {
                try {
                    FileInputStream fs = new FileInputStream(f);
                    ObjectInputStream os = new ObjectInputStream(fs);
                    Engine loadedGame = (Engine) os.readObject();
                    os.close();
                    return loadedGame.playGameforInputString(input);
                } catch (FileNotFoundException e) {
                    System.out.println("file not found");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e);
                    System.exit(0);
                } catch (ClassNotFoundException e) {
                    System.out.println("class not found");
                    System.exit(0);
                }
            }
        } else if (nxt == 'n') {
            int seedLen = input.indexOf('s');
            random = new Random(Long.parseLong(input.substring(0, seedLen)));
            input = input.substring(seedLen + 1);
            drawBoard();
        }
        return playGameforInputString(input);
    }

    private TETile[][] playGameforInputString(String input) {
        char nxt;
        while (THANOS > 0 && input.length() > 0) {
            nxt = input.charAt(0);
            input = input.substring(1);
            if (nxt == 'a') {
                player.moveLeft();
            } else if (nxt == 's') {
                player.moveDown();
            } else if (nxt == 'd') {
                player.moveRight();
            } else if (nxt == 'w') {
                player.moveUp();
            } else if (nxt == 'q') {
                saveGame();
                return visible;
            }
        }
        return visible;
    }


    private TETile[][] initworld(int w, int h) {
        TETile[][] ans = new TETile[w][h];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                ans[x][y] = Tileset.NOTHING;
            }
        }
        return ans;
    }

    private void placeTHANOSs(int total) {
        TETile thanos;
        int num = 0;
        while (num < total) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int n = random.nextInt(thanosTimelineet.size());
            if (world[x][y].equals(Tileset.FLOOR)) {
                thanos = thanosTimelineet.get(n);
                world[x][y] = thanos;
                if (thanos.equals(Tileset.THANOS)) {
                    num++;
                    THANOS++;
                }
            }
        }
    }

    private void drawBoard() {
        int[] origin = {random.nextInt(4) + 6, random.nextInt(4) + 6
        };
        for (int i = -1; i <= 1; i++) {
            world[origin[0] - 1][origin[1] + i] = Tileset.WALL;
        }

        int direction = 0;
        while (origin[0] < WIDTH - 8) {
            isRoom(origin);
            int width = random.nextInt(3) + 3;
            if (origin[0] + width > WIDTH - 8) {
                break;
            }

            direction = random.nextInt(2) * 2 - 1;
            int length = random.nextInt(10) + 6;
            int possibleIndex = direction * length + origin[1];
            if (possibleIndex > HEIGHT - 5 || possibleIndex < 5) {
                direction = -direction;
            }

            horiHallway(width, origin);

            if (random.nextInt(2) == 0) {
                if (origin[0] < WIDTH - 12 && origin[1] > 8 && origin[1] < HEIGHT - 8) {
                    drawDeadEnds(origin, -direction, true);
                }
            }
            origin[0] = origin[0] + width;
            isRoom(origin);
            wallify(origin[0] + 1, origin[1] - direction);
            verHallway(length, origin, direction);
            origin[1] = origin[1] + direction * (length - 1);
            wallify(origin[0] - 1, origin[1] + direction);
            if (random.nextInt(3) == 0) {
                if (origin[0] < WIDTH - 12 && origin[1] > 8 && origin[1] < HEIGHT - 8) {
                    drawDeadEnds(origin, direction, false);
                }
            }
        }


        world[origin[0] - 1][origin[1] + direction] = Tileset.NOTHING;
        world[origin[0]][origin[1] + direction] = Tileset.NOTHING;
        world[origin[0] - 1][origin[1]] = Tileset.WALL;
        world[origin[0] + 1][origin[1]] = Tileset.WALL;
        world[origin[0]][origin[1]] = Tileset.WALL;


        drawRooms(countRooms);
        placeTHANOSs(8);
        player = new Player();
    }

    private void horiHallway(int width, int[] start) {
        for (int x = 0; x <= width; x++) {
            drawHallway(start[0] + x, start[1], 0);
        }
    }

    private void verHallway(int length, int[] origin, int dir) {
        int start = Math.min(0, dir * (length - 1));
        int end = Math.max(0, dir * (length - 1));
        for (int y = start; y <= end; y++) {
            drawHallway(origin[0], origin[1] + y, 1);
        }
    }


    private void drawRooms(int num) {
        Room[] rooms = new Room[num];
        System.arraycopy(tempRooms, 0, rooms, 0, num);
        for (Room room : rooms) {
            for (int x = room.xr[0]; x <= room.xr[1]; x++) {
                wallify(x, room.yr[0]);
                wallify(x, room.yr[1]);
            }
            for (int y = room.yr[0]; y <= room.yr[1]; y++) {
                wallify(room.xr[0], y);
                wallify(room.xr[1], y);

            }
        }

        for (Room room : rooms) {
            for (int x = room.xr[0] + 1; x < room.xr[1]; x++) {
                for (int y = room.yr[0] + 1; y < room.yr[1]; y++) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    private boolean isRoom(int[] origin) {
        int isRoom = random.nextInt(3);
        if (isRoom == 0 || isRoom == 1) {
            {
                tempRooms[countRooms] = new Room(origin);
                countRooms++;
                return true;
            }
        }
        return false;
    }

    private void drawDeadEnds(int[] origin, int direction, boolean before) {
        int way = random.nextInt(2);
        int length = random.nextInt(3) + 5;
        if (way == 0) {
            verHallway(length, origin, direction);
            wallify(origin[0], origin[1] + length * direction);
            wallify(origin[0] - 1, origin[1] + length * direction);
            wallify(origin[0] + 1, origin[1] + length * direction);
        } else {
            int[] newCoord = new int[]{origin[0] - length, origin[1]};
            if (before) {
                newCoord = origin;
                horiHallway(length, newCoord);
                wallify(newCoord[0] + length + 1, newCoord[1] + 1);
                wallify(newCoord[0] + length + 1, newCoord[1]);
                wallify(newCoord[0] + length + 1, newCoord[1] - 1);
            } else {
                horiHallway(length, newCoord);
                wallify(newCoord[0] - 1, newCoord[1] - 1);
                wallify(newCoord[0] - 1, newCoord[1]);
                wallify(newCoord[0] - 1, newCoord[1] + 1);
            }
        }
    }


    private void wallify(int x, int y) {
        if (!world[x][y].equals(Tileset.FLOOR)) {
            world[x][y] = Tileset.WALL;
        }
    }


    private void drawHallway(int x, int y, int orientation) {
        world[x][y] = Tileset.FLOOR;
        if (orientation == 0) {
            wallify(x, y + 1);
            wallify(x, y - 1);
        } else {
            wallify(x + 1, y);
            wallify(x - 1, y);
        }
    }


    private class Player implements Serializable {
        int sight, x, y;
        HashSet<Coords> playervisible = new HashSet<>();

        Player() {
            sight = 8;
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
            while (!world[x][y].equals(Tileset.FLOOR)) {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            }
            world[x][y] = Tileset.PLAYER;
            update();
        }

        void update() {
            playervisible.clear();
            playervisible.addAll(update(1, 1));
            playervisible.addAll(update(1, -1));
            playervisible.addAll(update(-1, 1));
            playervisible.addAll(update(-1, -1));
            playervisible.add(new Coords(x, y));
            updatevisible();
        }

        HashSet<Coords> update(int up, int right) {
            int iterations = 0;
            HashSet<Coords> result = new HashSet<>();
            HashSet<Coords> curr = new HashSet<>();
            HashSet<Coords> nxt = new HashSet<>();

            Coords upOne = new Coords(x, y + up);
            Coords rightOne = new Coords(x + right, y);
            nxt.add(upOne);
            nxt.add(rightOne);
            while (nxt.size() > 0 && iterations < sight) {
                curr.clear();
                curr.addAll(nxt);
                nxt.clear();
                for (Coords c : curr) {
                    if (c.notWall()) {
                        nxt.add(new Coords(c.x + right, c.y));
                        nxt.add(new Coords(c.x, c.y + up));
                    }
                }
                result.addAll(curr);
                iterations++;
            }
            return result;
        }

        void updatevisible() {
            visible = initworld(WIDTH, HEIGHT);
            for (Coords c : playervisible) {
                visible[c.x][c.y] = world[c.x][c.y];
            }
        }

        void move(int dx, int dy) {
            int newx = x + dx;
            int newy = y + dy;
            TETile target = world[newx][newy];
            if (target.equals(Tileset.WALL)) {
                return;
            }
            world[x][y] = Tileset.FLOOR;
            world[newx][newy] = Tileset.PLAYER;
            if (target.equals(Tileset.THANOS)) {
                incrementTHANOSs();
            } else if (target.equals(Tileset.DEATH)) {
                youLose();
            } else if (target.equals(Tileset.MULTIPLY)) {
                placeTHANOSs(5);
            } else if (target.equals(Tileset.VISION)) {
                sight = 1;
            }
            x = newx;
            y = newy;
            update();
        }

        void moveLeft() {
            move(-1, 0);
        }

        void incrementTHANOSs() {
            THANOS--;
        }

        void moveRight() {
            move(1, 0);
        }

        void moveDown() {
            move(0, -1);
        }

        void moveUp() {
            move(0, 1);
        }
    }

    private class Room implements Serializable {
        int[] center;
        int[] xr = new int[2];
        int[] yr = new int[2];

        Room(int[] middle) {
            center = middle;
            xr[0] = center[0] - (random.nextInt(2) + 2);
            yr[0] = center[1] - (random.nextInt(2) + 2);
            xr[1] = center[0] + random.nextInt(2) + 2;
            yr[1] = center[1] + random.nextInt(2) + 2;
        }
    }

    private class Coords implements Serializable {
        int x, y;
        TETile tile;

        Coords(int x, int y) {
            this.x = x;
            this.y = y;
            tile = world[x][y];
        }

        boolean notWall() {
            return !tile.equals(Tileset.WALL);
        }
    }
}
