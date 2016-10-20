/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.tweetwallfx.controls.Word;
import org.tweetwallfx.tweet.api.Tweet;

/**
 *
 * @author Jörg
 */
public final class Support {

    private static final Random rand = new Random();
    private static final int dDeg = 10;
    private static final double dRadius = 5.0;

    private static final Pattern pattern = Pattern.compile("\\s+");
    
    private static Support INSTANCE = new Support();
    public static Support getDefault() {
        return INSTANCE;
    }
    
    private Support() {
    }
    
    static List<TweetWord> recalcTweetLayout(Tweet info) {
        TextFlow flow = new TextFlow();
        flow.setMaxWidth(300);
        pattern.splitAsStream(info.getText())
                .forEach(w -> {
                    Text textWord = new Text(w.concat(" "));
                    textWord.getStyleClass().setAll("tag");
//                    String color = "#292F33";
//                    textWord.setStyle("-fx-fill: " + color + ";");
                    textWord.setFont(Font.font(DEFAULT_FONT.getFamily(), TWEET_FONT_SIZE));
                    flow.getChildren().add(textWord);
                });
        flow.requestLayout();
        return flow.getChildren().stream().map(node -> new TweetWord(node.getBoundsInParent(), ((Text) node).getText())).collect(Collectors.toList());
    }
    
    static Map<Word, Bounds> recalcTagLayout(List<Word> words, Pane pane, Node ... respectNodes) {
        Node logo = respectNodes[0];    //please tune algorithm if you have more than the logo as respect area with no allowed intersection.
        boolean doFinish = false;
        Bounds layoutBounds = pane.getLayoutBounds();
        Bounds logoLayout = logo.getBoundsInParent();
        Bounds logoBounds = new BoundingBox(logoLayout.getMinX() - layoutBounds.getWidth() / 2d,
                logoLayout.getMinY() - layoutBounds.getHeight() / 2d,
                logoLayout.getWidth(),
                logoLayout.getHeight());

        List<Bounds> boundsList = new ArrayList<>();
        Text firstNode = createTextNode(words.get(0));
        double firstWidth = firstNode.getLayoutBounds().getWidth();
        double firstHeight = firstNode.getLayoutBounds().getHeight();

        boundsList.add(new BoundingBox(-firstWidth / 2d,
                -firstHeight / 2d, firstWidth, firstHeight));

        for (int i = 1; i < words.size(); ++i) {
            Word word = words.get(i);
            Text textNode = createTextNode(word);
            double width = textNode.getLayoutBounds().getWidth();
            double height = textNode.getLayoutBounds().getHeight();

            Point2D center = new Point2D(0, 0);
            double totalWeight = 0.0;
            for (int prev = 0; prev < i; ++prev) {
                Bounds prevBounds = boundsList.get(prev);
                double weight = words.get(prev).getWeight();
                center = center.add((prevBounds.getWidth() / 2d) * weight, (prevBounds.getHeight() / 2d) * weight);
                totalWeight += weight;
            }
            center = center.multiply(1d / totalWeight);
            boolean done = false;
            double radius = 0.5 * Math.min(boundsList.get(0).getWidth(), boundsList.get(0).getHeight());
            while (!done) {
                if (radius > Math.max(layoutBounds.getHeight(), layoutBounds.getWidth())) {
                    doFinish = true;
                }
                int startDeg = rand.nextInt(360);
                double prev_x = -1;
                double prev_y = -1;
                for (int deg = startDeg; deg < startDeg + 360; deg += dDeg) {
                    double rad = ((double) deg / Math.PI) * 180.0;
                    center = center.add(radius * Math.cos(rad), radius * Math.sin(rad));
                    if (prev_x == center.getX() && prev_y == center.getY()) {
                        continue;
                    }
                    prev_x = center.getX();
                    prev_y = center.getY();
                    Bounds mayBe = new BoundingBox(center.getX() - width / 2d,
                            center.getY() - height / 2d, width, height);
                    boolean useable = true;
                    //check if bounds are full on screen:
                    if (layoutBounds.getWidth() > 0 && layoutBounds.getHeight() > 0 && (mayBe.getMinX() + layoutBounds.getWidth() / 2d < 0
                            || mayBe.getMinY() + layoutBounds.getHeight() / 2d < 0
                            || mayBe.getMaxX() + layoutBounds.getWidth() / 2d > layoutBounds.getMaxX()
                            || mayBe.getMaxY() + layoutBounds.getHeight() / 2d > layoutBounds.getMaxY())) {
                        useable = false;
                    }
                    if (useable) {
                        useable = (null != logo && !mayBe.intersects(logoBounds));
                    }
                    if (useable) {
                        for (int prev = 0; prev < i; ++prev) {
                            if (mayBe.intersects(boundsList.get(prev))) {
                                useable = false;
                                break;
                            }
                        }
                    }
                    if (useable || doFinish) {
                        done = true;
                        boundsList.add(new BoundingBox(center.getX() - width / 2d,
                                center.getY() - height / 2d, width, height));
                        break;
                    }
                }
                radius += dRadius;
            }
        }

        Map<Word, Bounds> boundsMap = new HashMap<>();

        for (int k = 0; k < words.size(); k++) {
            boundsMap.put(words.get(k), boundsList.get(k));
        }
        return boundsMap;
    }

    private static final int TWEET_FONT_SIZE = 54;
    private static final int MINIMUM_FONT_SIZE = 36;
    private static final int MAX_FONT_SIZE = 72;
    
    private static final Font DEFAULT_FONT = Font.font("Calibri", FontWeight.BOLD, MINIMUM_FONT_SIZE);

    private double max;
    private double min;

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }
    
    static double getFontSize(double weight) {
        // maxFont = 48
        // minFont = 18

        double size;
        if (weight == -1) {
            size = TWEET_FONT_SIZE;
        } else if (weight == -2) {
            size = MINIMUM_FONT_SIZE - 10;
        } else {
            // linear
            //y = a+bx
//        double size = defaultFont.getSize() + ((48-defaultFont.getSize())/(max-min)) * word.weight;
            // logarithmic
            // y = a * Math.ln(x) + b
            double a = (DEFAULT_FONT.getSize() - MAX_FONT_SIZE) / (Math.log(min / max));
            double b = DEFAULT_FONT.getSize() - a * Math.log(min);
            size = a * Math.log(weight) + b;
        }
        return size;
    }

    static void fontSizeAdaption(Text text, double weight) {
        text.setFont(Font.font(DEFAULT_FONT.getFamily(), getFontSize(weight)));
    }

    static Text createTextNode(Word word) {
        Text textNode = new Text(word.getText());
        textNode.getStyleClass().setAll("tag");
        textNode.setStyle("-fx-padding: 5px");
        fontSizeAdaption(textNode, word.getWeight());
        return textNode;
    }
}
