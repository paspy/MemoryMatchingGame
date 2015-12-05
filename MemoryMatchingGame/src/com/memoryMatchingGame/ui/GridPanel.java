package com.memoryMatchingGame.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.memoryMatchingGame.lib.WordLibrary;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.Timer;

public final class GridPanel extends JPanel {

    // public
    public GridPanel(int cellCount) {
        words = WordLibrary.getDefault();
        addMouseListener(new MouseHandler());
    }

    public void Init(int cellCount, int paringInterval) {
        gameOver = false;
        this.cellCount = cellCount;
        this.paringInterval = paringInterval;
        numWords = cellCount * cellCount / 2;
        cells = new Cell[cellCount][cellCount];
        randomIndices = new int[numWords];
        int width = 2 * PAD + cellCount * CELL_WIDTH;
        int height = 2 * PAD + cellCount * CELL_HEIGHT;
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(220, 230, 255));

        ArrayList<String> wordArray, currWordset;
        wordArray = new ArrayList<>();
        for (int i = 0; i < words.getSize(); i++) {
            wordArray.add(words.getWord(i));
        }
        Collections.shuffle(wordArray);

        currWordset = new ArrayList<>();
        for (int i = 0; i < numWords; i++) {
            currWordset.add(wordArray.get(i));
            currWordset.add(wordArray.get(i));
        }
        Collections.shuffle(currWordset);

        for (int row = 0; row < cellCount; row++) {
            for (int col = 0; col < cellCount; col++) {
                int x = PAD + col * CELL_WIDTH;
                int y = PAD + row * CELL_HEIGHT;
                cells[row][col] = new Cell(x, y, currWordset.get(row * cellCount + col), "*******");
            }
        }
        this.revalidate();
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // clear panel (draw background)
        for (int row = 0; row < cellCount; row++) {
            for (int col = 0; col < cellCount; col++) {
                cells[row][col].draw(g);
            }
        }
        g.setFont(font);
        FontMetrics metrics = getFontMetrics(font); // Needed for finding width of string
        int gw = cellCount * CELL_WIDTH; // grid width
        int gh = cellCount * CELL_HEIGHT; // grid height
        int sh = metrics.getHeight(); // string height
        g.setColor(Color.black);
        int sw = metrics.stringWidth("Periodic Table Memory Matching");
        g.drawString("Periodic Table Memory Matching", PAD + gw / 2 - sw / 2, (PAD + sh) / 2); // center the string
    }

    // private
    private final int PAD = 70;
    private final int CELL_WIDTH = 100;
    private final int CELL_HEIGHT = 30;
    private final Font font = new Font("Arial", Font.BOLD, 18);

    private final WordLibrary words;
    private Cell[][] cells;
    private int[] randomIndices;
    private int cellCount;
    private int paringInterval;
    private int numWords;
    private boolean gameOver;

    private class Cell {

        private final int x, y;
        private String frontWord, backWord;
        private boolean drawFront;
        private final Font font = new Font("Arial", Font.PLAIN, 14);

        public Cell(int x, int y, String frontWord, String backWord) {
            this.x = x;
            this.y = y;
            this.frontWord = frontWord;
            this.backWord = backWord;
            this.drawFront = false;
        }

        public boolean equals(Cell lhs) {
            return this.frontWord.equals(lhs.frontWord);
        }

        public void SetWorld(String _word) {
            frontWord = _word;
        }

        public void TurnToFront(boolean _b) {
            drawFront = _b;
        }

        @Override
        public String toString() {
            return frontWord;
        }

        public void draw(Graphics g) {

            g.setFont(font);
            g.setColor(Color.WHITE);
            g.fillRect(x, y, CELL_WIDTH, CELL_HEIGHT); // fill cell with fillColor
            g.setColor(Color.BLACK);
            g.drawRect(x, y, CELL_WIDTH, CELL_HEIGHT); // draw black outline
            if (drawFront) {
                FontMetrics metrics = g.getFontMetrics(font);
                int dx = x + (CELL_WIDTH - metrics.stringWidth(frontWord)) / 2;
                int dy = y + ((CELL_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
                g.setColor(Color.BLUE);
                g.drawString(frontWord, dx, dy);
            } else {
                FontMetrics metrics = g.getFontMetrics(font);
                int dx = x + (CELL_WIDTH - metrics.stringWidth(backWord)) / 2;
                int dy = y + ((CELL_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
                g.setColor(Color.RED);
                g.drawString(backWord, dx, dy);
            }
        }

    }

    private class MouseHandler implements MouseListener {

        @Override
        public void mouseEntered(MouseEvent event) {
        }

        @Override
        public void mouseExited(MouseEvent event) {
        }

        @Override
        public void mousePressed(MouseEvent event) {
            if (gameOver) {
                return;
            }
            int mX = event.getX();
            int mY = event.getY();
            int mCol = (mX - PAD) / CELL_WIDTH; // keep integer part
            int mRow = (mY - PAD) / CELL_HEIGHT;
            if (mRow >= cellCount || mCol >= cellCount || mRow < 0 || mCol < 0) {
                return;
            }
            
            cells[mRow][mCol].TurnToFront(true);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            if (gameOver) {
                return;
            }
            int mX = event.getX();
            int mY = event.getY();
            if (mX < PAD || mX >= PAD + cellCount * CELL_WIDTH) {
                return;
            }
            if (mY < PAD || mY >= PAD + cellCount * CELL_HEIGHT) {
                return;
            }
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent event) {

        }
    }

    static void shuffleArray(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
