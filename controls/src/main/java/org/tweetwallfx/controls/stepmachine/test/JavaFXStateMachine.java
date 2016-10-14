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
package org.tweetwallfx.controls.stepmachine.test;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.tweetwallfx.controls.stepmachine.StepIterator;
import org.tweetwallfx.controls.stepmachine.StepMachine;
import org.tweetwallfx.controls.stepmachine.test.steps.ToCenterStep;
import org.tweetwallfx.controls.stepmachine.test.steps.ToLeftLowerStep;
import org.tweetwallfx.controls.stepmachine.test.steps.ToLeftUpperStep;
import org.tweetwallfx.controls.stepmachine.test.steps.ToRightLowerStep;
import org.tweetwallfx.controls.stepmachine.test.steps.ToRightUpperStep;

/**
 *
 * @author Jörg
 */
public class JavaFXStateMachine extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        btn.setLayoutX(150);
        btn.setLayoutY(150);
        
        Pane root = new Pane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 400, 400);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        prepareStateMachine(btn);
        primaryStage.show();
        
        
        //https://github.com/fxexperience/code
    }

    private final ExecutorService tickTockExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("TickTock");
            t.setDaemon(true);
            return t;
        });

    private void prepareStateMachine(Button button) {
        StepIterator steps = new StepIterator(Arrays.asList(
                new ToCenterStep(),
                new ToLeftUpperStep(),
                new ToRightUpperStep(),
                new ToRightLowerStep(),
                new ToLeftLowerStep()
        ));
        
        StepMachine s = new StepMachine(steps);
        s.getContext().put("Button", button);
        
        tickTockExecutor.execute(new Runnable() {
            @Override
            public void run() {
                s.go();
            }
        });
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
