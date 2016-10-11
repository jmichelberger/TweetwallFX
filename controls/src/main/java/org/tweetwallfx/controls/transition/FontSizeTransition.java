/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.transition;

import javafx.animation.Transition;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Jörg
 */
public final class FontSizeTransition extends Transition {
    private final Text node;
    private double startSize;
    private double toSize;

    public FontSizeTransition(Duration duration, Text node) {
        setCycleDuration(duration);
        this.node = node;
    }

    public void setFromSize(double startSize) {
        this.startSize = startSize;
    }

    public void setToSize(double toSize) {
        this.toSize = toSize;
    }

    @Override
    protected void interpolate(double frac) {
        if (!Double.isNaN(startSize)) {
            node.setFont(Font.font(node.getFont().getFamily(), startSize + frac * (toSize - startSize)));
        }
    }
}
