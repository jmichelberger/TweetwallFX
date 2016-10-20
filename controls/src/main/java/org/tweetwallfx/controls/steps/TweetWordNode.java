/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.steps;

import javafx.scene.text.Text;
import org.tweetwallfx.controls.WordleSkin;

/**
 *
 * @author Jörg
 */
public class TweetWordNode {

    private final TweetWord tweetWord;
    private final Text textNode;

    public TweetWordNode(TweetWord tweetWord, Text textNode) {
        this.tweetWord = tweetWord;
        this.textNode = textNode;
    }

    public Text getTextNode() {
        return textNode;
    }

    public TweetWord getTweetWord() {
        return tweetWord;
    }

}
