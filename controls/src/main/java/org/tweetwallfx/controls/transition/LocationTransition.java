/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.transition;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author Jörg
 */
public final class LocationTransition extends Transition {
    private final Node node;
    private double startX;
    private double startY;
    private double targetY;
    private double targetX;

    public LocationTransition(Duration duration, Node node) {
        setCycleDuration(duration);
        this.node = node;
    }

    public void setFromX(double startX) {
        this.startX = startX;
    }

    public void setFromY(double startY) {
        this.startY = startY;
    }

    public void setToX(double targetX) {
        this.targetX = targetX;
    }

    public void setToY(double targetY) {
        this.targetY = targetY;
    }

    @Override
    protected void interpolate(double frac) {
        if (!Double.isNaN(startX)) {
            node.setLayoutX(startX + frac * (targetX - startX));
        }
        if (!Double.isNaN(startY)) {
            node.setLayoutY(startY + frac * (targetY - startY));
        }
    }
    
}
