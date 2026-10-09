
/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy for getTimespan(tweets):
     *   number of tweets: 1, more than 1
     *   order: chronological, not chronological
     *   timestamps: identical, different
     *   empty list: not tested because the specification does not define its result
     *
     * Testing strategy for getMentionedUsers(tweets):
     *   number of tweets: 1, more than 1
     *   mentions: none, one, multiple
     *   repeated usernames: yes, no
     *   capitalization: same case, different case
     *   mention position: beginning, middle, end
     *   email address: present, absent
     *   punctuation after mention: present, absent
     *   username characters: letters, digits, underscores, hyphens
     */

    private static final Instant d1 =
            Instant.parse("2016-02-17T10:00:00Z");

    private static final Instant d2 =
            Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(
            1, "alyssa",
            "is it reasonable to talk about rivest so much?", d1);

    private static final Tweet tweet2 = new Tweet(
            2, "bbitdiddle",
            "rivest talk in 30 minutes #hype", d2);

    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        });
    }

    // Tests for getTimespan

    @Test
    public void testGetTimespanOneTweet() {
        Instant time = Instant.parse("2024-01-02T10:00:00Z");

        Tweet tweet = new Tweet(3, "alice", "hello", time);

        Timespan result =
                Extract.getTimespan(Arrays.asList(tweet));

        assertEquals(time, result.getStart());
        assertEquals(time, result.getEnd());
    }

    @Test
    public void testGetTimespanUnorderedTweets() {
        Instant early = Instant.parse("2024-01-01T10:00:00Z");
        Instant middle = Instant.parse("2024-01-02T10:00:00Z");
        Instant late = Instant.parse("2024-01-03T10:00:00Z");

        List<Tweet> tweets = Arrays.asList(
                new Tweet(4, "alice", "third", late),
                new Tweet(5, "bob", "first", early),
                new Tweet(6, "carol", "second", middle)
        );

        Timespan result = Extract.getTimespan(tweets);

        assertEquals(early, result.getStart());
        assertEquals(late, result.getEnd());
    }

    @Test
    public void testGetTimespanIdenticalTimestamps() {
        Instant time = Instant.parse("2024-01-02T10:00:00Z");

        List<Tweet> tweets = Arrays.asList(
                new Tweet(7, "alice", "one", time),
                new Tweet(8, "bob", "two", time)
        );

        Timespan result = Extract.getTimespan(tweets);

        assertEquals(time, result.getStart());
        assertEquals(time, result.getEnd());
    }

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan =
                Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    // Tests for getMentionedUsers

    @Test
    public void testGetMentionedUsersBasic() {
        Instant time = Instant.parse("2024-01-02T10:00:00Z");

        List<Tweet> tweets = Arrays.asList(
                new Tweet(9, "alice", "@bob hello @carol", time),
                new Tweet(10, "dave", "hello @bob!", time)
        );

        Set<String> result = Extract.getMentionedUsers(tweets);

        Set<String> expected = new HashSet<>(
                Arrays.asList("bob", "carol"));

        assertEquals(expected, normalizeUsers(result));
    }

    @Test
    public void testGetMentionedUsersIgnoresEmailAddress() {
        Instant time = Instant.parse("2024-01-02T10:00:00Z");

        List<Tweet> tweets = Arrays.asList(
                new Tweet(11, "alice",
                        "email bitdiddle@mit.edu and mention @mit", time)
        );

        Set<String> result = Extract.getMentionedUsers(tweets);

        assertEquals(
                new HashSet<>(Arrays.asList("mit")),
                normalizeUsers(result));
    }

    @Test
    public void testGetMentionedUsersUsernameCharacters() {
        Instant time = Instant.parse("2024-01-02T10:00:00Z");

        List<Tweet> tweets = Arrays.asList(
                new Tweet(12, "alice",
                        "@bob_2 @user-name @BOB_2", time)
        );

        Set<String> result = Extract.getMentionedUsers(tweets);

        Set<String> expected = new HashSet<>(
                Arrays.asList("bob_2", "user-name"));

        assertEquals(expected, normalizeUsers(result));
    }

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers =
                Extract.getMentionedUsers(Arrays.asList(tweet1));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    private Set<String> normalizeUsers(Set<String> users) {
        Set<String> normalized = new HashSet<>();

        for (String user : users) {
            normalized.add(user.toLowerCase(Locale.ROOT));
        }

        return normalized;
    }
}