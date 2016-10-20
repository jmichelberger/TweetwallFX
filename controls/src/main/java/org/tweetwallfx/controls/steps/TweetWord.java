/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.steps;

import javafx.geometry.Bounds;

/**
 *
 * @author Jörg
 */
class TweetWord {

    private final Bounds bounds;
    private final String text;

    public TweetWord(Bounds bounds, String text) {
        this.bounds = bounds;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return "TweetWord{" + "text=" + text + ", bounds=" + bounds + '}';
    }
}
