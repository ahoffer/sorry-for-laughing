package icd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Interface Control Document (ICD) generation component. It models the
 * permissions and roles associated with a particular endpoint. It includes the names of the
 * "internal" roles defined as part of the security-settings in the MQ configuration files, as well
 * as the LDAP roles defined in the role-mapping section of the MQ configuration file.
 * <p>
 * Internal to the class is the logic to match an endpoint name with the most specific
 * security-settings that match the endpoint name.
 */
public class MqSecuritySetting {

    static final char WORD_BREAK = '.';
    static final int NO_MATCH = -1;
    static final String MATCH_REMAINING_WORDS = "#";
    static final int STARTING_SCORE = 0;
    static final String MATCH_ONE_WORD = "*";

    // The "match string" represents the string literals, wild-cards, and word-delimiters
    // associated with the security-settings.
    // Some examples include "pulse.mission.information", "pulse.#", and "#".
    String match = "";

    // This field maps permissions like "createNonDurableQueue" to the internal roles that may
    // perform that action, such as "manager" or "broker-client".
    // Roles are always in lexical order.
    //TODO Replace with multimap?
    Map<String, List<String>> permissionToInternalRoles = new HashMap<>();

    // Maps permission to LDAP roles. LDAP roles are important to the client application-- the
    // internal roles are not important to them. NOTE that a single internal role may be aliased
    // to multiple LDAP roles. For example, both LDAP roles "ent SOA ESB Sender"
    // and "ent SOA ESB Receiver" map to the internal role "manager".
    // Roles are always in lexical order.
    //TODO Replace with multimap?
    Map<String, List<String>> permissionToLdapRoles = new HashMap<>();

    public MqSecuritySetting(String match, Map<String, List<String>> permissionToInternalRoles,
        Map<String, List<String>> permissionToLdapRoles) {
        this.match = match;
        this.permissionToInternalRoles = permissionToInternalRoles;
        this.permissionToLdapRoles = permissionToLdapRoles;
    }

    protected MqSecuritySetting() {
    }

    /**
     * This constructor creates an object guaranteed to never match the name of any endpoint
     *
     * @return A security object that never matches any queue or topic.
     */
    public static MqSecuritySetting noMatch() {
        return new MqSecuritySetting() {
            @Override
            public Integer score(String input) {
                return NO_MATCH;
            }
        };
    }

    // TODO Add unit tests
    // TODO: Add all this lexing/matching stuff to a new class? Move to inner class?
    // The more exact the security setting "match string" matches the input, the higher the score.
    Integer score(String input) {
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
                // TODO: Not worrying about the case of "*.*" being a better match than "*.*.*"

            } else {
                return NO_MATCH;
            }
        }

        // "pluse.msisino.info" matches "pulse.mission.info" with a score of 3
        return score;
    }

    // TODO Add unit tests
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

    /**
     * Return the security object with the higher score -- either this security object or the one in
     * the parameters. Makes sorting easier.
     *
     * @param input
     * @param o
     * @return
     */
    public MqSecuritySetting returnHigherScore(String input, MqSecuritySetting o) {
        return o.score(input) > score(input) ? o : this;
    }

    void debugPrintOn(PrintStream printStream, String parentIndent) {
        printStream.println(parentIndent + "Security: match with security setting '" + match + "'");
        printStream.println(parentIndent + "\tLDAP Roles and Permissions:");
        if (permissionToInternalRoles.isEmpty()) {
            printStream.println(parentIndent + "\t\t" + "NONE");
        } else {
            permissionToLdapRoles.forEach((k, v) -> {
                printStream.println(parentIndent + "\t\t" + k + ":" + String.join(",", v));
            });
        }

//        printStream.println(parentIndent + "\tInternal Roles and Permission:");
//        if (permissionToInternalRoles.isEmpty()) {
//            printStream.println(parentIndent + "\t\t" + "NONE");
//        } else {
//            permissionToInternalRoles.forEach((k, v) -> {
//                printStream.println(parentIndent + "\t\t" + k + ":" + String.join(",", v));
//            });
//        }
    }
}
