/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetwallfx.controls.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.scene.image.Image;

/**
 *
 * @author Jörg
 */
public class ImageCache {
    private final int maxSize;
    private final Map<String, Reference<Image>> cache = new HashMap<>();
    private final LinkedList<String> lru = new LinkedList<>();
    private final ImageCreator creator;

    public ImageCache(final ImageCreator creator) {
        this.creator = creator;
        maxSize = 10;
    }

    public Image get(final String url) {
        Image image;
        Reference<Image> imageRef = cache.get(url);
        if (null == imageRef || (null == (image = imageRef.get()))) {
            image = creator.create(url);
            cache.put(url, new SoftReference<>(image));
            lru.addFirst(url);
        } else {
            if (!url.equals(lru.peekFirst())) {
                lru.remove(url);
                lru.addFirst(url);
            }
        }
        if (lru.size() > maxSize) {
            String oldest = lru.removeLast();
            cache.remove(oldest);
        }
        return image;
    }

    public static interface ImageCreator {

        Image create(String url);
    }

    public static class DefaultImageCreator implements ImageCreator {

        @Override
        public Image create(final String url) {
            return new Image(url);
        }
    }

    public static class ProfileImageCreator implements ImageCreator {

        @Override
        public Image create(final String url) {
            return new Image(url, 64, 64, true, false);
        }
    }
    
}
