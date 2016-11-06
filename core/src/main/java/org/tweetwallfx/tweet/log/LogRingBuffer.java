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
package org.tweetwallfx.tweet.log;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.core.LogEvent;

/**
 * LogRingBuffer for log events.
 * @author Jörg
 */
public final class LogRingBuffer {

    private LogRingBuffer() {
    }
    
    private static final LogRingBuffer INSTANCE = new LogRingBuffer();
    
    public static LogRingBuffer getDefault() {
        return INSTANCE;
    }
    
    public static interface LogEventListener {
        void log(LogEvent event);
    }
    
    private Deque<LogEvent> ring = new ConcurrentLinkedDeque<>();
    
    private List<LogEventListener> listeners = new CopyOnWriteArrayList<>();
    
    void append(LogEvent event) {
        ring.add(event);
        //deliver event to all listeners on separate thread.
        deliverLogEventExecutor.submit(() -> {
            listeners.stream().forEach(listener -> {listener.log(event);});
        });
        if (ring.size() > 1000) {
            ring.removeFirst();
        }
    }
    
    public void addListener(LogEventListener listener) {
        addListener(listener, true);
    }

    public void addListener(LogEventListener listener, boolean forcefeedback) {
        if (forcefeedback) {
            deliverLogEventExecutor.submit(() -> {
                ring.stream().forEach(event -> {listener.log(event);});
            });
        }
        listeners.add(listener);
    }

    private final ExecutorService deliverLogEventExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("DeliverLogEvent");
            t.setDaemon(true);
            return t;
        });
    
}
