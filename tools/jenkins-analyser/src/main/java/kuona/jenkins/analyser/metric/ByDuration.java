package kuona.jenkins.analyser.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by graham on 10/12/14.
 */
public class ByDuration {
    public static final int MILLISECONDS = 1000;
    private static long thresholds[] = {
            10 * MILLISECONDS,
            30 * MILLISECONDS,
            60 * MILLISECONDS,
            5 * 60 * MILLISECONDS,
            30 * 60 * MILLISECONDS,
            60 * 60 * MILLISECONDS,
            5 * 60 * 60 * MILLISECONDS
    };
    private static String names[] = {
            "< 10 seconds",
            "10 - 30 seconds",
            "< minute",
            "< 5 minutes",
            "< 30 minutes",
            "< hour",
            "< 5 hours",
            "way too long"
    };
    private int counts[] = {0, 0, 0, 0, 0, 0, 0, 0};

    public void collect(long duration) {
        for (int i = 0; i < thresholds.length; i++) {
            if (duration < thresholds[i]) {
                counts[i] += 1;
                return;
            }
        }

        counts[counts.length - 1] += 1;
    }

    public List<Band> getBands() {
        List<Band> result = new ArrayList<>();

        for (int i = 0; i < thresholds.length; i++) {
            result.add(new Band(names[i], counts[i]));
        }
        result.add(new Band(names[names.length - 1], counts[counts.length - 1]));

        return result;
    }

    class Band {
        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        private final String name;
        private final int value;

        Band(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
