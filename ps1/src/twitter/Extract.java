/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */

public static Timespan getTimespan(List<Tweet> tweets) {
    if (tweets.isEmpty()) {
        throw new IllegalArgumentException("tweets must not be empty");
    }

    Instant earliest = tweets.get(0).getTimestamp();
    Instant latest = tweets.get(0).getTimestamp();

    for (Tweet tweet : tweets) {
        Instant timestamp = tweet.getTimestamp();

        if (timestamp.isBefore(earliest)) {
            earliest = timestamp;
        }

        if (timestamp.isAfter(latest)) {
            latest = timestamp;
        }
    }

    return new Timespan(earliest, latest);
}

public static Set<String> getMentionedUsers(List<Tweet> tweets) {
    Set<String> mentionedUsers = new HashSet<>();
    Pattern pattern = Pattern.compile(
        "(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)"
    );

    for (Tweet tweet : tweets) {
        Matcher matcher = pattern.matcher(tweet.getText());

        while (matcher.find()) {
            mentionedUsers.add(
                matcher.group(1).toLowerCase(Locale.ROOT)
            );
        }
    }

    return mentionedUsers;
}

}
