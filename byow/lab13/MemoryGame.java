package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);

        MemoryGame game = new MemoryGame(80, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String s = "";

        for (int i = 0; i < n; i++) {
            s += CHARACTERS[rand.nextInt(26)];
        }
        return s;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen

        StdDraw.clear(Color.BLACK);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        StdDraw.text(width/2, height/2, s);

        if (!gameOver) {
            StdDraw.textLeft(2, height - 2, "Round: " + round);
            StdDraw.text(width/2, height - 2, "Play!");
            StdDraw.textRight(width - 2, height - 2, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
            StdDraw.line(0, height - 4, width, height - 4);
        }


        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++){
            char c = letters.charAt(i);
            drawFrame(Character.toString(c));
            StdDraw.pause(1000);
        }
        StdDraw.pause(500);
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String s = "";
        int count = 0;
        while (StdDraw.hasNextKeyTyped() && count < n) {
            s += StdDraw.nextKeyTyped();
            count += 1;
        }
        return s;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        playerTurn = false;
        String player = "";

        //TODO: Establish Engine loop
        while (!gameOver) {
            drawFrame("Round: " + round);
            StdDraw.pause(2000);
            String r = generateRandomString(round);
            flashSequence(r);
            playerTurn = true;
            while (playerTurn) {
                player += solicitNCharsInput(1);
                drawFrame(player);
                StdDraw.pause(500);
                if (player.length() == r.length()) {
                    playerTurn = false;
                }
            }
            if (player.equals(r)) {
                round += 1;
            }
            else {
                gameOver = true;
            }

            player = "";
        }

        drawFrame("Game Over! You made it to round: " + round);

    }

}
