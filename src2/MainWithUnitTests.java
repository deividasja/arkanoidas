import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainWithUnitTests extends JPanel implements ActionListener, KeyListener, Runnable{

    @Test   // 1
    public void SecondBricksRowTest(){
        int bricksNumber = 10;
        brick = new Rectangle[bricksNumber];
        gameBoard();
        repaint();
        assertEquals(brick[9].x,161);
    }

    @Test   // 2
    public void GameStartTest(){
        gameBoard();
        repaint();
        assertEquals(words,null);
        assertEquals(endGame, false);
        Restart();
        assertEquals(words,null);
        assertEquals(endGame, false);
    }

    @Test   // 3
    public void ResetAfterRestartTest(){
        brick[1]=null;
        count++;
        endGame=true;
        Restart();
        assertEquals(count,0);
        assertEquals(brick[1].width, brickWidth);
        assertEquals(endGame, false);

    }

    // -------------------------------------------------------------------------

    public static final int MAX_BRICK_ROW_LENGTH = 7;
    public static final int MAX_BRICK_ROW_SIZE = 8;
    public static final int NEXT_BRICK_ROW_X = 39;
    public static final int ADDITIONAL_BRICK_SPACE = 2;
    public static final int PADDLE_MOVE_SPEED = 6;
    public static final int MAX_BALL_Y = 500;
    public static final int PADDLE_LOWEST_X = 8;
    public static final int PADDLE_HIGHEST_X = 600;
    public static final int MAX_WIDTH_X_BALL = 686;

    static boolean right = false;
    static boolean left = false;
    int ballx = 330;
    int bally = 450;
    int paddlex = 300;
    int paddley = 480;
    int brickx = 100;
    int bricky = 60;
    int brickWidth = 60;
    int brickHeight = 25;
    String words;
    int movex = -3;
    int movey = -3;
    boolean endGame = false;
    int count = 0;
    int brickRow = 0;
    Rectangle ball = new Rectangle(ballx, bally, 13, 13);
    Rectangle paddle = new Rectangle(paddlex, paddley, 80, 10);
    Rectangle[] brick = new Rectangle[16];


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        MainWithUnitTests game = new MainWithUnitTests();

        frame.setSize(703,540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        game.addKeyListener(game);
        game.setFocusable(true);
        Thread thread = new Thread(game);
        thread.start();
    }

    public void paint(Graphics g){
        // Background
        g.setColor(Color.BLUE);
        g.fillRect(0,0,700,900);

        // Scoreboard
        g.setColor(Color.white);
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.drawString("Your score: " + count, 8, 18);

        // Ball
        g.setColor(Color.white);
        g.fillOval(ball.x, ball.y, ball.width, ball.height);

        // Paddle
        g.setColor(Color.orange);
        g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);

        // Barrier
        g.setColor(Color.cyan);
        g.drawRect(0, 0, 686, 500);

        // Bricks
        for(int i = 0; i < brick.length; i++){
            if(brick[i] != null){
                if(i<=7){
                    g.setColor(Color.white);}
                if(i>7){
                    g.setColor(Color.green);
                }
                g.fill3DRect(brick[i].x, brick[i].y, brick[i].width, brick[i].height, true);
            }
        }

        if(endGame){
            Font font = new Font("Ariel", Font.BOLD, 50);
            g.setFont(font);
            if(words == "You Lost :("){
                g.setColor(Color.red);
            }
            if(words == " You Won!"){
                g.setColor(Color.yellow);
            }
            g.drawString(words, 200, 260);

            Font font2 = new Font("Ariel", Font.PLAIN,20);
            g.setFont(font2);
            g.setColor(Color.white);
            g.drawString("(Press 'R' to play again)",225,300);

            Font font3 = new Font("Ariel", Font.PLAIN,14);
            g.setFont(font3);
            g.setColor(Color.white);
            g.drawString("Press 'E' to exit",550,485);
        }


    }

    @Override
    public void run(){
        gameBoard();

        while(true) {
            for (int i = 0; i < brick.length; i++) {
                if (brick[i] != null) {
                    if (brick[i].intersects(ball)) {
                        brick[i] = null;
                        movey = -movey;
                        count++;
                    }
                }
            }

            repaint();
            ball.x += movex;
            ball.y += movey;

            if (count == brick.length) {
                endGame = true;
                words = " You Won!";
                repaint();
            }

            else if(ball.y >= MAX_BALL_Y) {
                endGame = true;
                words = "You Lost :(";
                repaint();
            }

            else {
                if (right) {
                    paddle.x += PADDLE_MOVE_SPEED;
                    left = false;
                }
                if (left) {
                    paddle.x -= PADDLE_MOVE_SPEED;
                    right = false;
                }
                if (paddle.x <= PADDLE_LOWEST_X) {
                    paddle.x = PADDLE_LOWEST_X;
                } else if (paddle.x >= PADDLE_HIGHEST_X) {
                    paddle.x = PADDLE_HIGHEST_X;
                }
                if (ball.x <= 0 || ball.x + ball.height >= MAX_WIDTH_X_BALL) {
                    movex = -movex;
                }
                if (ball.y <= 0) {
                    movey = -movey;
                }
                if (paddle.intersects(ball)) {
                    movey = -movey;
                }

                try {
                    Thread.sleep(10);
                } catch (Exception x) {
                    System.out.println("Something went wrong.");
                }
            }
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (e.getKeyChar() == 'E' || e.getKeyChar() == 'e') {
            System.exit(0);
        }
        if (e.getKeyChar() == 'R' || e.getKeyChar() == 'r') {
            Restart();
        }
        if(key == KeyEvent.VK_RIGHT){
            right = true;
        }
        if(key == KeyEvent.VK_LEFT){
            left = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_RIGHT){
            right = false;
        }
        if(key == KeyEvent.VK_LEFT){
            left = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public void Restart(){
        requestFocus();

        right = false;
        left = false;
        ballx = 330;
        bally = 450;
        paddlex = 300;
        paddley = 480;
        brickx = 100;
        bricky = 60;
        brickWidth = 60;
        brickHeight = 25;
        ball = new Rectangle(ballx, bally, 13, 13);
        paddle = new Rectangle(paddlex, paddley, 80, 10);
        brick = new Rectangle[16];
        movex = -3;
        movey = -3;
        endGame = false;
        count = 0;
        words = null;

        gameBoard();
        repaint();
    }

    public void gameBoard(){
        for(int i = 0; i < brick.length; i++){
            brick[i] = new Rectangle(brickx, bricky, brickWidth, brickHeight);

            if(i == MAX_BRICK_ROW_LENGTH){
                brickx = NEXT_BRICK_ROW_X;
                bricky = bricky + brickHeight + ADDITIONAL_BRICK_SPACE;
                brickRow = MAX_BRICK_ROW_LENGTH;
            }
            if(i - brickRow == MAX_BRICK_ROW_SIZE){
                brickx = NEXT_BRICK_ROW_X;
                bricky = bricky + brickHeight + ADDITIONAL_BRICK_SPACE;
                brickRow = i;
            }
            brickx += brickWidth +1;
        }
    }

}