/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.memoryMatchingGame.lib;

/**
 *
 */
public abstract class WordLibrary {

    protected WordLibrary() {
    }

    public static WordLibrary getDefault() {
        return StaticWordLibrary.DEFAULT;
    }
    public abstract String getWord(int idx);
    public abstract int getSize();
    public abstract String[] getArr();
}
