/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.tweet;

import com.sun.javafx.stage.StageHelper;
import java.io.Serializable;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 *
 * @author Jörg
 */
@Plugin(name="StringPropertyAppender", category="Core", elementType="appender", printObject=true)
public class StringPropertyAppender extends AbstractAppender {
    private Text text;

    private final StringProperty s = new SimpleStringProperty();

    protected StringPropertyAppender(String name, Filter filter,
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
        if (null == text) {
            Optional<Node> findFirst = StageHelper.getStages().stream().map(stage-> stage.getScene().lookup(selector)).findFirst();
            if (findFirst.isPresent()) {
                text = (Text)findFirst.get();
                text.textProperty().bind(s);
            } else {
                System.err.println("StringPropertyAppender selector=" + selector +" not found!");
                text = null;
            }
        }
    }
    
    public StringProperty stringProperty() {
        return s;
    }

    // The append method is where the appender does the work.
    // Given a log event, you are free to do with it what you want.
    // This example demonstrates:
    // 1. Concurrency: this method may be called by multiple threads concurrently
    // 2. How to use layouts
    // 3. Error handling
    @Override
    public void append(LogEvent event) {
        Platform.runLater(() -> s.setValue(String.valueOf(event.getMessage().getFormattedMessage())));
        System.out.println(event.getMessage());
    }
    
    // Your custom appender needs to declare a factory method
    // annotated with `@PluginFactory`. Log4j will parse the configuration
    // and call this factory method to construct an appender instance with
    // the configured attributes.
    @PluginFactory
    public static StringPropertyAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("selector") String selector) {
        if (name == null) {
            LOGGER.error("No name provided for StringPropertyAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new StringPropertyAppender(name, filter, layout, true, selector);
    }    
}
