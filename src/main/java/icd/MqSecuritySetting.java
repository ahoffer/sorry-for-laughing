package icd;

import org.jsoup.nodes.Element;

import java.util.*;

public class MqSecuritySetting {

    public static final char WORD_BREAK = '.';
    public static final int NO_MATCH = -1;
    public static final String MATCH_REMAINING_WORDS = "#";
    public static final int STARTING_SCORE = 0;
    public static final String MATCH_ONE_WORD = "*";
    protected String match;
    protected Map<String, List<String>> typeToPermissions = new HashMap<>();

    public MqSecuritySetting(Element element) {
        parse(element);
    }

    public static MqSecuritySetting noMatch() {
        return new MqSecuritySetting() {
            @Override
            public Integer score(String input) {
                return NO_MATCH;
            }
        };
    }

    protected MqSecuritySetting() {
    }

    public static MqSecuritySetting empty() {
        return new MqSecuritySetting();
    }

    public MqSecuritySetting merge(MqSecuritySetting mqSecuritySetting) {
        return null;
    }

    public MqSecuritySetting parse(Element element) {
        match = element.attr("match");
        element.children().forEach(this::parsePermission);
        return this;
    }

    protected void parsePermission(Element element) {
        if (!element.tagName().equals("permission")) return;
        String type = element.attr("type");
        String roles = element.attr("roles");
        Arrays.asList(roles.split(","));
        typeToPermissions.put(type, Arrays.asList(roles.split(",")));
    }

    //TODO Add unit tests
    public Integer score(String input) {
        String[] inputWords = getWords(input);
        String[] matchStringWords = getWords(match);
        int score = STARTING_SCORE;
        for (int i = 0; i < matchStringWords.length; ++i) {

            // Don't run off the end
            if (i >= inputWords.length) {
                return NO_MATCH;
            }

            String inputWord = inputWords[i];
            String matchWord = matchStringWords[i];

            //  afdcgs.# matches afdcgs.mission.objective
            if (MATCH_REMAINING_WORDS.equals(matchWord)) {
                return score;
            }


            if (inputWord.equals(matchWord)) {
                score++;
            } else if (MATCH_ONE_WORD.equals(matchWord)) {
                // Do nothing. Does not contribute to score.
                //TODO: Not worrying about the case of "*.*" being a better match than "*.*.*"

            } else {
                return NO_MATCH;
            }
        }

        // "pluse.msisino.info" matches "pulse.mission.info" with a score of 3
        return score;

    }

    //TODO Add unit tests
    String[] getWords(String name) {
        List<String> words = new ArrayList<>();

        String currentWord = "";
        for (char ch : name.toCharArray()) {
            switch (ch) {
                case WORD_BREAK:
                    words.add(currentWord);
                    currentWord = "";
                    break;
                default:
                    currentWord += ch;
            }
        }

        if (!currentWord.isEmpty()) {
            words.add(currentWord);
        }
        return words.toArray(new String[words.size()]);
    }

    public MqSecuritySetting returnHigherScore(String input, MqSecuritySetting o) {
        return o.score(input) > score(input) ? o : this;
    }
}
