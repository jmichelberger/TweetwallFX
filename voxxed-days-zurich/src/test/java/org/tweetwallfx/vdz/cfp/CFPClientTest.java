/*
 * The MIT License
 *
 * Copyright 2014-2018 TweetWallFX
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
package org.tweetwallfx.vdz.cfp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.tweetwall.devoxx.api.cfp.client.CFPClient;
import org.tweetwall.devoxx.api.cfp.client.Event;
import org.tweetwall.devoxx.api.cfp.client.Events;
import org.tweetwall.devoxx.api.cfp.client.ProposalTypes;
import org.tweetwall.devoxx.api.cfp.client.Rooms;
import org.tweetwall.devoxx.api.cfp.client.Schedule;
import org.tweetwall.devoxx.api.cfp.client.Schedules;
import org.tweetwall.devoxx.api.cfp.client.Speaker;
import org.tweetwall.devoxx.api.cfp.client.SpeakerReference;
import org.tweetwall.devoxx.api.cfp.client.Talk;
import org.tweetwall.devoxx.api.cfp.client.Tracks;
import org.tweetwall.devoxx.api.cfp.client.VotingResults;

public class CFPClientTest {

    private static final boolean CFP_REACHABLE = Response.Status.Family.SUCCESSFUL == ClientBuilder.newClient()
            .target(CFPClientVDZ18.BASE_URI)
            .request(MediaType.APPLICATION_JSON)
            .get()
            .getStatusInfo()
            .getFamily();

    @Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
        System.out.println("#################### START: " + testName.getMethodName() + " ####################");
    }

    @After
    public void after() {
        System.out.println("####################   END: " + testName.getMethodName() + " ####################");
    }

    private static void ignoreIfServerUnreachable() {
        if (!CFP_REACHABLE) {
            System.out.println("CFP Server is unreachable");
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void clientImplIsFound() {
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);
        assertEquals(1, CFPClient.getClientStream().count());
        assertEquals(CFPClientVDZ18.class, client.getClass());
    }

    @Test
    public void eventsAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Events> eventsOptional = client.getEvents();
        System.out.println("eventsOptional: " + eventsOptional);
        assertTrue(eventsOptional.isPresent());

        final Events events = eventsOptional
                .get();
        System.out.println("events: " + events);
        assertNotNull(events);
    }

    @Test
    public void eventIsRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Event> eventOptional = client.getEvent();
        System.out.println("eventOptional: " + eventOptional);
        assertTrue(eventOptional.isPresent());

        final Event event = eventOptional
                .get();
        System.out.println("event: " + event);
        assertNotNull(event);
    }

    @Test
    public void proposalTypesAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<ProposalTypes> proposalTypesOptional = client.getProposalTypes();
        System.out.println("proposalTypesOptional: " + proposalTypesOptional);
        assertTrue(proposalTypesOptional.isPresent());

        final ProposalTypes proposalTypes = proposalTypesOptional
                .get();
        System.out.println("proposalTypes: " + proposalTypes);
        assertNotNull(proposalTypes);
    }

    @Test
    public void roomsAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Rooms> roomsOptional = client.getRooms();
        System.out.println("roomsOptional: " + roomsOptional);
        assertTrue(roomsOptional.isPresent());

        final Rooms rooms = roomsOptional
                .get();
        System.out.println("rooms: " + rooms);
        assertNotNull(rooms);
    }

    @Test
    public void schedulesAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Schedules> schedulesOptional = client.getSchedules();
        System.out.println("schedulesOptional: " + schedulesOptional);
        assertTrue(schedulesOptional.isPresent());

        final Schedules schedules = schedulesOptional
                .get();
        System.out.println("schedules: " + schedules);
        assertNotNull(schedules);
        assertTrue(schedules.getSchedules().count() > 0);
    }

    @Test
    public void scheduleIsRetrievableForADay() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Schedule> scheduleOptional = client.getSchedule("thursday");
        System.out.println("scheduleOptional: " + scheduleOptional);
        assertTrue(scheduleOptional.isPresent());

        final Schedule schedule = scheduleOptional
                .get();
        System.out.println("schedule: " + schedule);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void scheduleIsRetrievableForADayAndRoom() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Schedule> scheduleOptional = client.getSchedule("thursday", "e_room6");
        System.out.println("scheduleOptional: " + scheduleOptional);
        assertTrue(scheduleOptional.isPresent());

        final Schedule schedule = scheduleOptional
                .get();
        System.out.println("schedule: " + schedule);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    public void speakersAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final List<Speaker> speakers = client.getSpeakers();
        System.out.println("speakers: " + convertCollectionForToString(speakers));
        assertNotNull(speakers);
    }

    @Test
    public void speakerInformationIsCompletable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final List<Speaker> speakers = client.getSpeakers();
        assertNotNull(speakers);

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                //                .filter(s -> "@mreinhold".equals(s.getTwitter()))
                .findAny();
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());
        assertFalse(speakerOptional.get().hasCompleteInformation());

        final Optional<Speaker> speakerOptionalReload = speakerOptional
                .flatMap(Speaker::reload);
        System.out.println("speakerOptionalReload: " + speakerOptionalReload);
        assertTrue(speakerOptionalReload.isPresent());
        assertTrue(speakerOptionalReload.get().hasCompleteInformation());

        Speaker speaker = speakerOptionalReload.get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void speakerIsRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Speaker> speakerOptional = client.getSpeaker("a2488b47888290c8c123ce5e81ad0a860d57e1e2");
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());

        final Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertSame(speaker, speaker.reload().get());
    }

    @Test
    public void talkInformationIsCompletable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Speaker> speakerOptional = client.getSpeaker("a2488b47888290c8c123ce5e81ad0a860d57e1e2");
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());

        final Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
        assertTrue(speaker.hasCompleteInformation());
        assertNotNull(speaker.getAcceptedTalks());
        assertFalse(speaker.getAcceptedTalks().isEmpty());

        final Talk incompleteTalk = speaker.getAcceptedTalks().get(0);
        assertNotNull(incompleteTalk);
        assertFalse(incompleteTalk.hasCompleteInformation());
        System.out.println("incompleteTalk: " + incompleteTalk);

        final Optional<Talk> incompleteTalkOptionalReload = incompleteTalk.reload();
        System.out.println("incompleteTalkOptionalReload: " + incompleteTalkOptionalReload);
        assertTrue(incompleteTalkOptionalReload.isPresent());
        assertTrue(incompleteTalkOptionalReload.get().hasCompleteInformation());

        final Talk completeTalk = incompleteTalkOptionalReload.get();
        assertNotNull(completeTalk);
        assertTrue(completeTalk.hasCompleteInformation());
        System.out.println("completeTalk: " + completeTalk);
    }

    @Test
    public void talkCanGetSpeakers() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Talk> talkOptional = client.getTalk("XBI-0039");
        System.out.println("talkOptional: " + talkOptional);
        assertTrue(talkOptional.isPresent());

        final Talk talk = talkOptional
                .get();
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());

        final Set<Speaker> speakers = talk
                .getSpeakers()
                .stream()
                .map(SpeakerReference::getSpeaker)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        assertSame(talk.getSpeakers().size(), speakers.size());

        final Optional<Speaker> speakerOptional = speakers
                .stream()
                //                .filter(s -> "@mreinhold".equals(s.getTwitter()))
                .findAny();
        System.out.println("speakerOptional: " + speakerOptional);
        assertTrue(speakerOptional.isPresent());
        assertTrue(speakerOptional.get().hasCompleteInformation());

        Speaker speaker = speakerOptional
                .get();
        System.out.println("speaker: " + speaker);
        assertNotNull(speaker);
    }

    @Test
    public void talkIsRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Talk> talkOptional = client.getTalk("XBI-0039");
        System.out.println("talkOptional: " + talkOptional);
        assertTrue(talkOptional.isPresent());

        final Talk talk = talkOptional
                .get();
        System.out.println("talk: " + talk);
        assertNotNull(talk);
        assertTrue(talk.hasCompleteInformation());
        assertSame(talk, talk.reload().get());
    }

    @Test
    public void tracksAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<Tracks> tracksOptional = client.getTracks();
        System.out.println("tracksOptional: " + tracksOptional);
        assertTrue(tracksOptional.isPresent());

        final Tracks tracks = tracksOptional
                .get();
        System.out.println("tracks: " + tracks);
        assertNotNull(tracks);
    }

    @Test
    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsOverallAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<VotingResults> votingResults = client.getVotingResultsOverall();
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
    }

    @Test
    @Ignore // error prone as it is dependant upon the CFP API Server
    public void votingResultsDailyAreRetrievable() {
        ignoreIfServerUnreachable();
        final CFPClient client = CFPClient.getClient();
        System.out.println("client: " + client);
        assertNotNull(client);

        final Optional<VotingResults> votingResults = client.getVotingResultsDaily("monday");
        System.out.println("votingResults: " + votingResults);
        assertNotNull(votingResults);
    }

    private static String convertCollectionForToString(final Collection<?> collection) {
        if (null == collection) {
            return null;
        }

        return collection
                .stream()
                .map(Object::toString)
                .map(s -> s.replaceAll("\n", "\n        "))
                .collect(Collectors.joining(
                        ",\n        ",
                        "[\n        ",
                        "\n    ]"));
    }
}