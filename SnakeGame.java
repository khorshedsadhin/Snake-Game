import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.*;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}

interface GameConstants {
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;
    int TILE_SIZE = 25;
    int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (TILE_SIZE * TILE_SIZE);
    int GAME_SPEED = 90;
    String HIGH_SCORE_FILE = "snake_highscore.txt";
    String GAME_TITLE = "Snake Game";
}

interface DrawableEntity {
    void draw(Graphics g);
}

class HighScoreManager implements GameConstants {
    public static int loadHighScore() {
        File highScoreFile = new File(HIGH_SCORE_FILE);
        if (!highScoreFile.exists()) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading high score: " + e.getMessage());
            return 0;
        }
        return 0;
    }

    public static void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(Integer.toString(score));
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }
}

class GameLogicException extends RuntimeException {
    public GameLogicException(String message) {
        super(message);
    }
}

class GameFrame extends JFrame implements GameConstants {
    public GameFrame() {
        this.add(new GamePanel());
        this.setTitle(GAME_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener, GameConstants {
    private Timer timer;
    private Snake snake;
    private Food food;
    private boolean running = false;
    private boolean gameOver = false;
    private int score;
    private int highScore;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 20));
        this.setFocusable(true);
        this.addKeyListener(this);
        timer = new Timer(GAME_SPEED, this);
        startGame();
    }

    public void startGame() {
        snake = new Snake();
        food = new Food();
        food.spawn(snake.getBody());
        score = 0;
        highScore = HighScoreManager.loadHighScore();
        gameOver = false;
        running = true;
        timer.start();
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw(g2d);
    }

    private void draw(Graphics2D g2d) {
        if (running && !gameOver) {
            food.draw(g2d);
            snake.draw(g2d);
            drawScore(g2d);
        } else if (gameOver) {
            displayGameOver(g2d);
        }
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Consolas", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g2d.getFont());
        String scoreText = "Score: " + score;
        g2d.drawString(scoreText, SCREEN_WIDTH - metrics.stringWidth(scoreText) - 15, g2d.getFont().getSize() + 5);
        String highScoreText = "High Score: " + highScore;
        g2d.drawString(highScoreText, 15, g2d.getFont().getSize() + 5);
    }

    private void displayGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(200, 0, 0));
        g2d.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g2d.getFont());
        g2d.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 70);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Consolas", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g2d.getFont());
        String finalScoreText = "Final Score: " + score;
        g2d.drawString(finalScoreText, (SCREEN_WIDTH - metrics2.stringWidth(finalScoreText)) / 2, SCREEN_HEIGHT / 2);
        String highScoreText = "High Score: " + highScore;
        g2d.drawString(highScoreText, (SCREEN_WIDTH - metrics2.stringWidth(highScoreText)) / 2, SCREEN_HEIGHT / 2 + 40);

        g2d.setFont(new Font("Consolas", Font.PLAIN, 20));
        FontMetrics metrics3 = getFontMetrics(g2d.getFont());
        g2d.drawString("Press ENTER to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press ENTER to Restart")) / 2, SCREEN_HEIGHT / 2 + 90);
    }

    private void moveSnake() {
        snake.move();
    }

    private void checkFoodConsumption() {
        if (snake.getHead().equals(new Point(food.getX(), food.getY()))) {
            snake.grow();
            score++;
            if (score > highScore) {
                highScore = score;
                HighScoreManager.saveHighScore(highScore);
            }
            food.spawn(snake.getBody());
        }
    }

    private void checkCollisions() {
        Point head = snake.getHead();
        List<Point> snakeBody = snake.getBody(); // Get the body once for efficiency

        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                gameOver = true;
                break;
            }
        }

        if (head.x < 0 || head.x >= SCREEN_WIDTH || head.y < 0 || head.y >= SCREEN_HEIGHT) {
            gameOver = true;
        }

        if (gameOver) {
            running = false;
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !gameOver) {
            moveSnake();
            checkFoodConsumption();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && running) {
            snake.setDirection(e.getKeyCode());
        } else if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

class Snake implements DrawableEntity, GameConstants {
    private LinkedList<Point> body; // Changed to LinkedList
    private Direction direction;
    private final Color headColor = new Color(0, 220, 0);
    private final Color bodyColor1 = new Color(0, 180, 0);
    private final Color bodyColor2 = new Color(0, 150, 0);

    public Snake() {
        this.body = new LinkedList<>(); // Changed to LinkedList
        this.direction = Direction.RIGHT;
        initializeSnake();
    }

    private void initializeSnake() {
        body.clear();
        int startX = 5 * TILE_SIZE;
        int startY = SCREEN_HEIGHT / (2 * TILE_SIZE) * TILE_SIZE;
        body.add(new Point(startX, startY));
        body.add(new Point(startX - TILE_SIZE, startY));
        body.add(new Point(startX - 2 * TILE_SIZE, startY));
    }

    public void move() {
        Point currentHead = body.getFirst(); // Use getFirst() for LinkedList
        Point newHead = new Point(currentHead);

        switch (direction) {
            case UP:    newHead.y -= TILE_SIZE; break;
            case DOWN:  newHead.y += TILE_SIZE; break;
            case LEFT:  newHead.x -= TILE_SIZE; break;
            case RIGHT: newHead.x += TILE_SIZE; break;
        }
        body.addFirst(newHead); // Use addFirst() for LinkedList
        body.removeLast();      // Use removeLast() for LinkedList
    }

    public void grow() {
        Point tail = body.getLast(); // Use getLast() for LinkedList
        body.addLast(new Point(tail.x, tail.y)); // Add to the end for growth
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int i = 0;
        for (Point segment : body) { // Iterate through LinkedList
            if (i == 0) { // Head
                g2d.setColor(headColor);
                g2d.fillRoundRect(segment.x, segment.y, TILE_SIZE, TILE_SIZE, 15, 15);
                g2d.setColor(Color.BLACK);
                int eyeSize = TILE_SIZE / 5;
                int eyeOffset1 = TILE_SIZE / 4;
                int eyeOffset2 = TILE_SIZE - TILE_SIZE / 4 - eyeSize;

                if (direction == Direction.UP) {
                    g2d.fillOval(segment.x + eyeOffset1, segment.y + eyeOffset1, eyeSize, eyeSize);
                    g2d.fillOval(segment.x + eyeOffset2, segment.y + eyeOffset1, eyeSize, eyeSize);
                } else if (direction == Direction.DOWN) {
                    g2d.fillOval(segment.x + eyeOffset1, segment.y + eyeOffset2, eyeSize, eyeSize);
                    g2d.fillOval(segment.x + eyeOffset2, segment.y + eyeOffset2, eyeSize, eyeSize);
                } else if (direction == Direction.LEFT) {
                    g2d.fillOval(segment.x + eyeOffset1, segment.y + eyeOffset1, eyeSize, eyeSize);
                    g2d.fillOval(segment.x + eyeOffset1, segment.y + eyeOffset2, eyeSize, eyeSize);
                } else { // RIGHT
                     g2d.fillOval(segment.x + eyeOffset2, segment.y + eyeOffset1, eyeSize, eyeSize);
                     g2d.fillOval(segment.x + eyeOffset2, segment.y + eyeOffset2, eyeSize, eyeSize);
                }
            } else { // Body
                if (i % 2 == 0) g2d.setColor(bodyColor2);
                else g2d.setColor(bodyColor1);
                g2d.fillRoundRect(segment.x, segment.y, TILE_SIZE, TILE_SIZE, 10, 10);
                g2d.setColor(headColor.darker());
                g2d.drawRoundRect(segment.x, segment.y, TILE_SIZE, TILE_SIZE, 10, 10);
            }
            i++;
        }
    }

    public List<Point> getBody() { // Return List interface
        return body;
    }

    public Point getHead() {
        if (body.isEmpty()) {
            throw new GameLogicException("Snake body is empty, cannot get head.");
        }
        return body.getFirst(); // Use getFirst()
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(int keyCode) { // Changed to accept keyCode directly
        Direction newDirection = this.direction;
        switch (keyCode) {
            case KeyEvent.VK_LEFT:  if (this.direction != Direction.RIGHT) newDirection = Direction.LEFT;  break;
            case KeyEvent.VK_RIGHT: if (this.direction != Direction.LEFT)  newDirection = Direction.RIGHT; break;
            case KeyEvent.VK_UP:    if (this.direction != Direction.DOWN)  newDirection = Direction.UP;    break;
            case KeyEvent.VK_DOWN:  if (this.direction != Direction.UP)    newDirection = Direction.DOWN;  break;
        }
        this.direction = newDirection;
    }
}

class Food implements DrawableEntity, GameConstants {
    private int x;
    private int y;
    private final Random random;
    private final Color foodColor = new Color(255, 60, 60);
    private final Color foodHighlightColor = new Color(255, 150, 150);

    public Food() {
        this.random = new Random();
    }

    public void spawn(List<Point> snakeBody) { // Accepts List interface
        boolean onSnake;
        int attempts = 0;
        int maxAttempts = GAME_UNITS + 10;

        do {
            onSnake = false;
            this.x = random.nextInt(SCREEN_WIDTH / TILE_SIZE) * TILE_SIZE;
            this.y = random.nextInt(SCREEN_HEIGHT / TILE_SIZE) * TILE_SIZE;
            for (Point segment : snakeBody) {
                if (segment.x == this.x && segment.y == this.y) {
                    onSnake = true;
                    break;
                }
            }
            attempts++;
            if (attempts > maxAttempts) {
                System.err.println("Could not find a valid position for food after " + maxAttempts + " attempts.");
                break;
            }
        } while (onSnake);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(foodColor);
        g2d.fillOval(x, y, TILE_SIZE, TILE_SIZE);
        int highlightSize = TILE_SIZE / 3;
        g2d.setColor(foodHighlightColor);
        g2d.fillOval(x + TILE_SIZE / 5, y + TILE_SIZE / 5, highlightSize, highlightSize);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
