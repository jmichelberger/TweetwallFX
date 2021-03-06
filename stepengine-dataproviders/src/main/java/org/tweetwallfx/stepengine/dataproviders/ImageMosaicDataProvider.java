/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 TweetWallFX
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
package org.tweetwallfx.stepengine.dataproviders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tweetwallfx.cache.URLContent;
import org.tweetwallfx.stepengine.api.DataProvider;
import org.tweetwallfx.stepengine.api.config.StepEngineSettings;
import org.tweetwallfx.tweet.api.Tweet;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntry;
import org.tweetwallfx.tweet.api.entry.MediaTweetEntryType;
import static org.tweetwallfx.util.ToString.createToString;
import static org.tweetwallfx.util.ToString.map;

public class ImageMosaicDataProvider implements DataProvider.HistoryAware, DataProvider.NewTweetAware {

    private static final Logger LOG = LogManager.getLogger(ImageMosaicDataProvider.class);
    private final CopyOnWriteArrayList<ImageStore> images = new CopyOnWriteArrayList<>();
    private final Config config;

    private ImageMosaicDataProvider(final Config config) {
        this.config = config;
    }

    @Override
    public void processNewTweet(final Tweet tweet) {
        processHistoryTweet(tweet);
    }

    @Override
    public void processHistoryTweet(final Tweet tweet) {
        LOG.info("new Tweet received: {}", tweet.getId());
        if (null == tweet.getMediaEntries()
                || (tweet.isRetweet() && !config.isIncludeRetweets())) {
            return;
        }
        LOG.debug("processing new Tweet: {}", tweet.getId());
        Arrays.stream(tweet.getMediaEntries())
                .filter(MediaTweetEntryType.photo::isType)
                .forEach(mte -> addImage(mte, tweet.getCreatedAt()));
    }

    public List<ImageStore> getImages() {
        return Collections.<ImageStore>unmodifiableList(images);
    }

    private void addImage(final MediaTweetEntry mte, final Date date) {
        PhotoImageCache.INSTANCE.getCachedOrLoad(mte, urlc -> {
            if (images.addIfAbsent(new ImageStore(urlc, date.toInstant()))) {
                LOG.info("Added ImageStore for mediaID: {}", mte.getId());
            }
            if (config.getMaxCacheSize() < images.size()) {
                images.sort(Comparator.comparing(ImageStore::getInstant));
                images.remove(0);
            }
        });
    }

    public static class FactoryImpl implements DataProvider.Factory {

        @Override
        public ImageMosaicDataProvider create(final StepEngineSettings.DataProviderSetting dataProviderSetting) {
            return new ImageMosaicDataProvider(dataProviderSetting.getConfig(Config.class));
        }

        @Override
        public Class<ImageMosaicDataProvider> getDataProviderClass() {
            return ImageMosaicDataProvider.class;
        }
    }

    public static class Config {

        private boolean includeRetweets = false;
        private int maxCacheSize = 40;

        public boolean isIncludeRetweets() {
            return includeRetweets;
        }

        public void setIncludeRetweets(final boolean includeRetweets) {
            this.includeRetweets = includeRetweets;
        }

        public int getMaxCacheSize() {
            return maxCacheSize;
        }

        public void setMaxCacheSize(final int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
        }

        @Override
        public String toString() {
            return createToString(this, map(
                    "includeRetweets", isIncludeRetweets(),
                    "maxCacheSize", getMaxCacheSize()
            )) + " extends " + super.toString();
        }
    }

    public static final class ImageStore {

        private final Image image;
        private final String digest;
        private final Instant instant;

        public ImageStore(final URLContent urlc, final Instant instant) {
            this.digest = urlc.getDigest();
            this.image = new Image(urlc.getInputStream());
            this.instant = instant;
        }

        public String getDigest() {
            return digest;
        }

        public Instant getInstant() {
            return instant;
        }

        public Image getImage() {
            return image;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode(this.digest);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (null == obj || getClass() != obj.getClass()) {
                return false;
            }

            final ImageStore other = (ImageStore) obj;
            boolean result = true;

            result &= Objects.nonNull(this.digest);
            result &= Objects.nonNull(other.digest);
            result &= Objects.equals(this.digest, other.digest);

            return result;
        }
    }
}
