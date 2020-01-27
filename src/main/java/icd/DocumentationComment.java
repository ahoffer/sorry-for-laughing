package icd;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Node;

public class DocumentationComment {

    public static DocumentationComment attemptToCreate(Node node) {
        if (node instanceof Comment) {
            String comment = ((Comment) node).getData();
            return attemptToCreate(comment);
        }
        return null;
    }

    static DocumentationComment attemptToCreate(String comment) {
        for (MagicToken token : MagicToken.values()) {
            if (comment.startsWith(token.toString())) {
                return new DocumentationComment(token, extractDocumentation(token, comment));
            }
        }
        return null;
    }

    static String extractDocumentation(MagicToken token, String comment) {
        return comment.substring(token.toString().length() + 1).trim();
    }

    final MagicToken token;
    final String text;

    public DocumentationComment(MagicToken token, String text) {
        this.token = token;
        this.text = text;
    }
}
