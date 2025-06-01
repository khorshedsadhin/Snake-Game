# Java Snake Game

A classic implementation of the Snake game built using Java Swing and AWT. Guide the snake to eat food, grow longer, and try to achieve the highest score without colliding with walls or its own tail!

## Features

* **Classic Snake Gameplay:** Control a growing snake to eat food and score points.
* **Score & High Score:** Tracks your current score and saves the all-time high score locally in `snake_highscore.txt`.
* **Keyboard Controls:** Intuitive arrow key controls for snake movement.
* **Smooth Graphics:** Uses Java Swing for the graphical user interface with anti-aliasing for smoother visuals.
* **Collision Detection:** Game ends if the snake hits the screen boundaries or its own body.
* **Dynamic Food Spawning:** Food appears randomly on the game board, avoiding the snake's current position.
* **Game Over & Restart:** Clear "Game Over" screen with options to restart the game by pressing Enter.
* **Customizable Game Speed:** Game speed can be adjusted via the `GAME_SPEED` constant in the code.
* **Well-Structured Code:** Organized into logical sections and classes for better readability and maintenance.

## Requirements

* Java Development Kit (JDK) installed (version 8 or higher should be fine).

## How to Compile and Run

1.  **Download or Clone:**
    * Download the `SnakeGame.java` file (and `snake_highscore.txt` if you want to start with an existing high score, though the game will create it if it's missing).
    * Or, if this is a Git repository, clone it: `git clone <repository_url>`

2.  **Navigate to the Directory:**
    Open your terminal or command prompt and navigate to the directory where you saved `SnakeGame.java`.

3.  **Compile:**
    Run the Java compiler:
    ```bash
    javac SnakeGame.java
    ```
    This will create a `SnakeGame.class` file (and other inner class files).

4.  **Run:**
    Execute the compiled game:
    ```bash
    java SnakeGame
    ```
    The game window should appear.

## How to Play

* **Objective:** Guide the snake to eat the red food pellets. Each pellet eaten increases your score and the snake's length.
* **Controls:**
    * **Arrow Up:** Move snake up
    * **Arrow Down:** Move snake down
    * **Arrow Left:** Move snake left
    * **Arrow Right:** Move snake right
    * *Note: You cannot immediately reverse the snake's direction (e.g., from Right to Left).*
* **Game Over:** The game ends if the snake:
    * Collides with any of the four screen boundaries.
    * Collides with its own body.
* **Restart:** After a "Game Over," press the **ENTER** key to start a new game.

## File Structure

* `SnakeGame.java`: The main Java source code file containing all game logic and classes.
* `snake_highscore.txt`: A plain text file that stores the highest score achieved. It will be created automatically in the same directory as the game if it doesn't exist. [cite: 1]

## Code Structure Overview

The `SnakeGame.java` file is organized into several key classes and interfaces:

* **`SnakeGame` (main class):** Contains the `main` method to launch the game.
* **`GameConstants` (interface):** Defines constants like screen dimensions, tile size, game speed, and file names.
* **`DrawableEntity` (interface):** An interface for game objects that can be drawn on the screen.
* **`HighScoreManager` (class):** Handles loading and saving the high score from/to `snake_highscore.txt`.
* **`Direction` (enum):** Represents the possible movement directions of the snake.
* **`GameLogicException` (class):** Custom exception for handling game-specific logical errors.
* **`GameFrame` (class extends `JFrame`):** The main window frame for the game.
* **`GamePanel` (class extends `JPanel`):** The core component where the game logic, drawing, and event handling (actions, key presses) take place.
* **`Snake` (class):** Manages the snake's body (a `LinkedList` of `Point`s), movement, growth, collision detection, and drawing.
* **`Food` (class):** Manages the food's position, spawning logic, and drawing.

The code is also internally commented with `✅ SECTION:` markers to delineate these parts.

---

Enjoy the game! Feel free to explore the code and modify it.
