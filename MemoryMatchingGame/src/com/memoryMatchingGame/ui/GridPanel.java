package com.memoryMatchingGame.ui;

import com.memoryMatchingGame.lib.WordLibrary;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class GridPanel extends JPanel {

    // public
    public GridPanel() {
        words = WordLibrary.getDefault();
        addMouseListener(new MouseHandler());
        clock = new java.util.Timer();
        counter = new java.util.Timer();
    }

    public void Init(int cellCount, int paringInterval) {
        gameOver = false;
        isParing = false;
        paredCount = 0;
        this.cellCount = cellCount;
        this.paringInterval = paringInterval;
        this.elapsedTime = 0;
        mainTimer = new MainTimer();
        clock.scheduleAtFixedRate(mainTimer, 1, 1);
        countDown = new CountDown(this.paringInterval);
        numWords = cellCount * cellCount / 2;
        cells = new Cell[cellCount][cellCount];
        paringTube = new ArrayList<>();
        int width = 2 * PAD + cellCount * CELL_WIDTH;
        int height = 2 * PAD + cellCount * CELL_HEIGHT;
        setPreferredSize(new Dimension(width, height));
        setBackground(new Color(220, 230, 255));

        ArrayList<String> wordArray, currWordSet;
        wordArray = new ArrayList<>();
        for (int i = 0; i < words.getSize(); i++) {
            wordArray.add(words.getWord(i));
        }
        Collections.shuffle(wordArray);

        currWordSet = new ArrayList<>();
        for (int i = 0; i < numWords; i++) {
            currWordSet.add(wordArray.get(i));
            currWordSet.add(wordArray.get(i));
        }
        Collections.shuffle(currWordSet);

        for (int row = 0; row < cellCount; row++) {
            for (int col = 0; col < cellCount; col++) {
                int x = PAD + col * CELL_WIDTH;
                int y = PAD + row * CELL_HEIGHT;
                cells[row][col] = new Cell(x, y, currWordSet.get(row * cellCount + col), "*******");
            }
        }

        this.revalidate();
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
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
        g.setColor(Color.darkGray);
        g.drawString("Total time elapsed: " + formatInterval(elapsedTime), PAD + gw / 2 - sw / 2, PAD + gh + sh);
        if (countDown != null) {
            if (((CountDown) countDown).GetCurrentTime() < 5) {
                g.setColor(Color.MAGENTA);
                int timeLeft = ((CountDown) countDown).GetCurrentTime();
                int len = metrics.stringWidth("Peek Time Left: " + timeLeft);
                g.drawString("Peek Time Left: " + timeLeft, PAD + gw / 2 - len / 2, PAD + gh + sh * 2);
            }
        }
        if (gameOver) {
            g.setColor(Color.red);
            int len = metrics.stringWidth("You Win!");
            g.drawString("You Win!", PAD + gw / 2 - len / 2, PAD + gh + sh * 2);
        }
    }

    // private
    private final int PAD = 70;
    private final int CELL_WIDTH = 100;
    private final int CELL_HEIGHT = 30;
    private final Font font = new Font("Arial", Font.BOLD, 18);

    private final WordLibrary words;
    private Cell[][] cells;
    private ArrayList<Integer> paringTube;
    private int cellCount;
    private int paringInterval;
    private int paredCount;
    private long elapsedTime;
    private final java.util.Timer clock;
    private java.util.Timer counter;
    private TimerTask mainTimer;
    private TimerTask countDown;

    private int numWords;
    private boolean isParing;
    private boolean gameOver;

    private class Cell {

        private final int x, y;
        private final String frontWord, backWord;
        private boolean drawFront, selectable;
        private final Font font = new Font("Arial", Font.PLAIN, 14);

        public Cell(int x, int y, String frontWord, String backWord) {
            this.x = x;
            this.y = y;
            this.frontWord = frontWord;
            this.backWord = backWord;
            this.drawFront = false;
            this.selectable = true;
        }

        public boolean equals(Cell lhs) {
            return this.frontWord.equals(lhs.frontWord);
        }

        public void TurnToFront(boolean _b) {
            drawFront = _b;
        }

        public void SetSelectable(boolean _b) {
            selectable = _b;
        }

        public boolean GetSelectable() {
            return selectable;
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
            if (gameOver || isParing) {
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
            if (paringTube.size() < 4 && cells[mRow][mCol].GetSelectable()) {
                paringTube.add(mRow);
                paringTube.add(mCol);
                if (paringTube.size() >= 4) {
                    if (cells[paringTube.get(0)][paringTube.get(1)].equals(cells[paringTube.get(2)][paringTube.get(3)])) {
                        cells[paringTube.get(0)][paringTube.get(1)].SetSelectable(false);
                        cells[paringTube.get(2)][paringTube.get(3)].SetSelectable(false);
                        paringTube.clear();
                        paredCount++;
                        if (paredCount == numWords) {
                            gameOver = true;
                        }
                    } else {
                        isParing = true;
                        countDown = new CountDown(paringInterval);
                        counter = new java.util.Timer();
                        counter.scheduleAtFixedRate(countDown, 1000, 1000);
                    }
                }
            }
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

    private class MainTimer extends TimerTask {

        @Override
        public void run() {
            if (!gameOver) {
                elapsedTime++;
            }
            if (isParing && ((CountDown) countDown).GetCurrentTime() < 1) {
                isParing = false;
                cells[paringTube.get(0)][paringTube.get(1)].TurnToFront(false);
                cells[paringTube.get(2)][paringTube.get(3)].TurnToFront(false);
                paringTube.clear();
                counter.cancel();
                counter.purge();
                countDown = null;
                System.gc();
            }
            repaint();
        }
    }

    private class CountDown extends TimerTask {

        private int currentTime;

        public CountDown(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void run() {
            if (currentTime > 0) {
                currentTime--;
                System.out.println("CountDown: " + currentTime);
            }
        }

        public int GetCurrentTime() {
            return currentTime;
        }
    }

    private static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }
}
