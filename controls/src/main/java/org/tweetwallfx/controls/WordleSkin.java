/*
 * The MIT License
 *
 * Copyright 2014-2016 TweetWallFX
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
package org.tweetwallfx.controls;

import com.sun.javafx.stage.StageHelper;
import java.io.Serializable;
import org.tweetwallfx.controls.util.ImageCache;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.tweetwallfx.controls.stepengine.StepEngine;
import org.tweetwallfx.controls.stepengine.StepIterator;
import org.tweetwallfx.controls.steps.CloudToTweetStep;
import org.tweetwallfx.controls.steps.TweetToCloudStep;
import org.tweetwallfx.controls.steps.UpdateCloudStep;

/**
 * @author sven
 */
public class WordleSkin extends SkinBase<Wordle> {
    private static final Logger startupLogger = LogManager.getLogger("org.tweetwallfx.startup");
    public static final int TWEET_FONT_SIZE = 54;
    public static final int MINIMUM_FONT_SIZE = 36;
    public static final int MAX_FONT_SIZE = 72;
    
    public final Map<Word, Text> word2TextMap = new HashMap<>();
    // used for Tweet Display
    public  final List<TweetLayout.TweetWordNode> tweetWordList = new ArrayList<>();
    private final Pane pane;
    private final Pane stackPane;
    private HBox infoBox;
    private HBox mediaBox;

    private int displayCloudTags = 25;

    private ImageView logo;
    private ImageView backgroundImage;
    private Font font;
    private final Boolean favIconsVisible;
    private final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    private final ImageCache mediaImageCache = new ImageCache(new ImageCache.DefaultImageCreator());
    private final ImageCache profileImageCache = new ImageCache(new ImageCache.ProfileImageCreator());
    public ImageView getLogo() {
        return logo;
    }

    public ImageCache getMediaImageCache() {
        return mediaImageCache;
    }

    public ImageCache getProfileImageCache() {
        return profileImageCache;
    }

    public Pane getPane() {
        return pane;
    }

    public int getDisplayCloudTags() {
        return displayCloudTags;
    }

    public HBox getMediaBox() {
        return mediaBox;
    }

    public void setMediaBox(HBox mediaBox) {
        this.mediaBox = mediaBox;
    }

    public HBox getInfoBox() {
        return infoBox;
    }

    public void setInfoBox(HBox infoBox) {
        this.infoBox = infoBox;
    }

    public Boolean getFavIconsVisible() {
        return favIconsVisible;
    }

    public DateFormat getDf() {
        return df;
    }
    
    public Font getFont() {
        return font;
    }
    
    public WordleSkin(Wordle wordle) {
        super(wordle);
        //create panes
        stackPane = new StackPane();
        pane = new Pane();
        pane.prefWidthProperty().bind(stackPane.widthProperty());
        pane.prefHeightProperty().bind(stackPane.heightProperty());
        //assemble panes
        stackPane.getChildren().addAll(pane);
        this.getChildren().add(stackPane);
        //assign style
        stackPane.getStylesheets().add(this.getClass().getResource("wordle.css").toExternalForm());
        
        final TextArea loggingTextArea = new TextArea();

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        loggingTextArea.setPrefRowCount(10);
        loggingTextArea.setPrefColumnCount(100);
        loggingTextArea.setWrapText(true);
        loggingTextArea.setPrefWidth(600);
        GridPane.setHalignment(loggingTextArea, HPos.CENTER);
        gridpane.add(loggingTextArea, 0, 1);
        loggingTextArea.setText("The logging area!\n");
        loggingTextArea.setOpacity(0.5);
        loggingTextArea.setId("loggingTextArea");   //for accessibility via log4j

//        stackPane.setOnKeyTyped((KeyEvent event) -> {
//            if (event.isAltDown() && event.getCharacter().equals("d")) {
                if (null == gridpane.getParent()) {
                    stackPane.getChildren().add(gridpane);
                }
                else {
                    stackPane.getChildren().remove(gridpane);
                }
//            }
//        });
//        stackPane.getChildren().add(gridpane);
        
        getSkinnable().logoProperty().addListener((obs, oldValue, newValue) -> {
            updateLogo(newValue);
        });

        getSkinnable().backgroundGraphicProperty().addListener((obs, oldValue, newValue) -> {
            updateBackgroundGraphic(newValue);
        });

        updateBackgroundGraphic(getSkinnable().backgroundGraphicProperty().getValue());
        updateLogo(getSkinnable().logoProperty().getValue());

        favIconsVisible = wordle.favIconsVisibleProperty().get();
        displayCloudTags = wordle.displayedNumberOfTagsProperty().get();
        font = wordle.fontProperty().get();
        prepareStepMachine();
    }

    private void updateLogo(final String newLogo) {
        if (null != logo) {
            pane.getChildren().remove(logo);
            logo = null;
        }   
        System.out.println("Logo: " + newLogo);
        if (null != newLogo && !newLogo.isEmpty()) {
            logo = new ImageView(newLogo);
            logo.getStyleClass().add("logo");            
            pane.getChildren().add(logo);
            logo.setLayoutX(0);
            logo.setLayoutY(pane.getHeight() - logo.getImage().getHeight());
        }
    }

    private void updateBackgroundGraphic(final String newBackgroundGraphic) {
        if (null != backgroundImage) {
            stackPane.getChildren().remove(backgroundImage);
            backgroundImage = null;
        }
        if (null != newBackgroundGraphic && !newBackgroundGraphic.isEmpty()) {
            backgroundImage = new ImageView(newBackgroundGraphic) {
                @Override
                public double minHeight(double width) {
                    return 10; 
                }

                @Override
                public double minWidth(double height) {
                    return 10;
                }
                
            };

            backgroundImage.getStyleClass().add("bg-image");
            
            backgroundImage.fitWidthProperty().bind(stackPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(stackPane.heightProperty());

            backgroundImage.setPreserveRatio(true);
            backgroundImage.setCache(true);
            backgroundImage.setSmooth(true);
            
            stackPane.getChildren().add(0, backgroundImage);
        }
    }
    private final ExecutorService tickTockExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("TickTock");
            t.setDaemon(true);
            return t;
        });
    
    public void prepareStepMachine() {
        startupLogger.info("Prepare StepMachine");
        StepIterator steps = new StepIterator(Arrays.asList(new UpdateCloudStep(),
                new CloudToTweetStep(),
                new TweetToCloudStep()
        ));
        
        StepEngine s = new StepEngine(steps);
        s.getContext().put("WordleSkin", this);
        
        tickTockExecutor.execute(new Runnable() {
            @Override
            public void run() {
                s.go();
            }
        });
        startupLogger.info("Prepare StepMachine done");        
    }
   
    @Plugin(name = "TextAreaAppender", category = "Core", elementType = "appender", printObject = true)
    public static class TextAreaAppender extends AbstractAppender {
        private TextArea textArea;
        
        protected TextAreaAppender(String name, Filter filter,
                Layout<? extends Serializable> layout, final boolean ignoreExceptions, final String selector) {
            super(name, filter, layout, ignoreExceptions);
            StageHelper.getStages().addListener(new ListChangeListener<Stage>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Stage> c) {
                    bind(selector);
                }
            });
        }
        
        private void bind(final String selector) {
            if (null == textArea) {
                Optional<Node> findFirst = StageHelper.getStages().stream().map(stage-> stage.getScene().lookup(selector)).findFirst();
                if (findFirst.isPresent()) {
                    textArea = (TextArea)findFirst.get();
                } else {
                    System.err.println("TextAreaAppender selector=" + selector +" not found!");
                    textArea = null;
                }
            }
        }

        // The append method is where the appender does the work.
        // Given a log event, you are free to do with it what you want.
        // This example demonstrates:
        // 1. Concurrency: this method may be called by multiple threads concurrently
        // 2. How to use layouts
        // 3. Error handling
        @Override
        public void append(LogEvent event) {
            System.out.println(event.getMessage().getFormattedMessage());
            if (null != textArea) {
                textArea.appendText(event.getMessage().getFormattedMessage());
                textArea.appendText("\n");
            }
        }
        // Your custom appender needs to declare a factory method
        // annotated with `@PluginFactory`. Log4j will parse the configuration
        // and call this factory method to construct an appender instance with
        // the configured attributes.

        @PluginFactory
        public static TextAreaAppender createAppender(
                @PluginAttribute("name") String name,
                @PluginElement("Layout") Layout<? extends Serializable> layout,
                @PluginElement("Filter") final Filter filter,
                @PluginAttribute("selector") String selector) {
            if (name == null) {
                LOGGER.error("No name provided for TextAreaAppender");
                return null;
            }
            if (layout == null) {
                layout = PatternLayout.createDefaultLayout();
            }
            return new TextAreaAppender(name, filter, layout, true, selector);
        }
    }
}