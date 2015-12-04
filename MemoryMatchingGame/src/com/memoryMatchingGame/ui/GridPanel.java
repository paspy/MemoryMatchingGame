package com.memoryMatchingGame.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GridPanel extends JPanel {

    // private
    private class Cell {

        private int x, y, size;
        private String frontWord;
        private String backWord;

        private final Font font = new Font("Arial", Font.BOLD, 16);

        public Cell(int x, int y, int size, String frontWord, String backWord) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.frontWord = frontWord;
            this.backWord = backWord;
        }

        public boolean equals(Cell lhs) {
            return this.backWord.equals(lhs.backWord);
        }

        public void SetWorld(String _word) {
            frontWord = _word;
        }

        @Override
        public String toString() {
            return frontWord;
        }

        
        public void draw(Graphics g, boolean drawFront) {

            g.setFont(font);
            g.setColor(Color.WHITE);
            g.fillRect(x, y, size, size); // fill cell with fillColor
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size); // draw black outline
            if (drawFront) {
                FontMetrics metrics = g.getFontMetrics(font);
                int dx = (size - metrics.stringWidth(frontWord)) / 2;
                int dy = ((size - metrics.getHeight()) / 2) - metrics.getAscent();
                g.setColor(Color.BLUE);
                g.drawString(frontWord, dx, dy);
            } else {
                FontMetrics metrics = g.getFontMetrics(font);
                int dx = (size - metrics.stringWidth(backWord)) / 2;
                int dy = ((size - metrics.getHeight()) / 2) - metrics.getAscent();
                g.setColor(Color.RED);
                g.drawString(backWord, dx, dy);
            }
            g.dispose();
        }

    }

    // public
    public GridPanel(int gridSzie) {

    }
}
