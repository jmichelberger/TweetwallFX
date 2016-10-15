/*
 * The MIT License
 *
 * Copyright 2014-2015 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwallfx.controls.stepengine.test.steps;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.util.Duration;
import org.tweetwallfx.controls.stepengine.AbstractStep;
import org.tweetwallfx.controls.stepengine.StepEngine.MachineContext;
import org.tweetwallfx.controls.transition.LocationTransition;

/**
 *
 * @author Jörg Michelberger
 */
public class ToRightLowerStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ToRightLowerStep.class.getName());

    @Override
    public int preferredStepDuration(MachineContext context) {
        return 3000;
    }

    @Override
    public void doStep(MachineContext context) {
        LOG.log(Level.INFO, "Enter ToRightLowerStep.doStep()");
        Node n = (Node)context.get("Button");
        LocationTransition t = new LocationTransition(Duration.seconds(2), n);
        t.setFromX(n.getLayoutX());
        t.setFromY(n.getLayoutY());
        t.setToX(200);
        t.setToY(200);
        
        t.setOnFinished(e -> context.proceed());
        Platform.runLater(() -> t.play());
        LOG.log(Level.INFO, "Exit ToRightLowerStep.doStep()");
    }
}
