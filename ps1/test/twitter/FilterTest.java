
/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     Testing strategy for writtenBy(tweets, username):
     *   number of tweets: 0, 1, more than 1
     *   number of matching tweets: 0, 1, more than 1
     *   username capitalization: same case, different case
     *
     * Testing strategy for inTimespan(tweets, timespan):
     *   number of tweets: 0, more than 1
     *   tweets inside timespan: none, some, all
     *   tweets at start or end boundary: yes, no
     *
     * Testing strategy for containing(tweets, words):
     *   number of search words: 1, more than 1
     *   matching tweets: none, some, all
     *   capitalization: same case, different case
     *   word matching: complete word, part of a bigger word
     *
     * All methods: check that the input order is preserved.
     */

    private static final Instant d1 =
            Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 =
            Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 =
            Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(
            1, "alyssa",
            "is it reasonable to talk about rivest so much?", d1);

    private static final Tweet tweet2 = new Tweet(
            2, "bbitdiddle",
            "rivest talk in 30 minutes #hype", d2);

    private static final Tweet tweet3 = new Tweet(
            3, "Alyssa", "Talking about the pset tonight", d3);

    // Tests for writtenBy

    @Test
    public void testWrittenByDifferentCaseMultipleResults() {
        List<Tweet> result = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2, tweet3), "ALYSSA");

        assertEquals(
                Arrays.asList(tweet1, tweet3), result);
    }

    @Test
    public void testWrittenByNoMatches() {
        List<Tweet> result = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2, tweet3), "carol");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testWrittenByOneMatch() {
        List<Tweet> result = Filter.writtenBy(
                Arrays.asList(tweet1, tweet2, tweet3), "bbitdiddle");

        assertEquals(Arrays.asList(tweet2), result);
    }

    // Tests for inTimespan

    @Test
    public void testInTimespanEndpointsIncluded() {
        List<Tweet> result = Filter.inTimespan(
                Arrays.asList(tweet1, tweet2, tweet3),
                new Timespan(d2, d3));

        assertEquals(Arrays.asList(tweet2, tweet3), result);
    }

    @Test
    public void testInTimespanNoMatches() {
        Instant start = Instant.parse("2017-01-01T00:00:00Z");
        Instant end = Instant.parse("2017-01-02T00:00:00Z");

        List<Tweet> result = Filter.inTimespan(
                Arrays.asList(tweet1, tweet2, tweet3),
                new Timespan(start, end));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testInTimespanPreservesInputOrder() {
        List<Tweet> tweets =
                Arrays.asList(tweet3, tweet1, tweet2);

        List<Tweet> result = Filter.inTimespan(
                tweets, new Timespan(d1, d3));

        assertEquals(tweets, result);
    }

    // Tests for containing

    @Test
    public void testContainingCaseAndWholeWords() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("TALK"));

        assertEquals(Arrays.asList(tweet1, tweet2), result);
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("rivest", "pset"));

        assertEquals(Arrays.asList(tweet1, tweet2, tweet3), result);
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("nonexistent"));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testContainingDoesNotMatchPartOfWord() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("talking"));

        assertEquals(Arrays.asList(tweet3), result);
    }

    @Test
    public void testContainingPreservesInputOrder() {
        List<Tweet> tweets =
                Arrays.asList(tweet3, tweet2, tweet1);

        List<Tweet> result = Filter.containing(
                tweets, Arrays.asList("rivest", "pset", "talk"));

        assertEquals(tweets, result);
    }

    @Test
    public void testContainingEmptyWords() {
        List<Tweet> result = Filter.containing(
                Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList());

        assertTrue(result.isEmpty());
    }
}