package com.memoryMatchingGame.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GridPanel extends JPanel {
    
    // private members
    private class Cell {
        private int x, y;
        private String frontWord;
        private String backWord;
        
        private Font font = new Font("Arial", Font.BOLD, 16);
        
        public Cell(int x, int y, String frontWord, String backWord) {
            this.x = x;
            this.y = y;
            this.frontWord = frontWord;
            this.backWord = backWord;
        }
        
        public boolean equals(Cell lhs) {
            return this.backWord.equals(lhs.backWord);
        }
    }
    
    // public members
    public GridPanel(int gridSzie) {
        
    }
}
