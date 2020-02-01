package icd;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * This class is part of the Interface Control Document (ICD) generation component. It attempts to
 * create documenation objects from XML comments in the MQ configuration file. The comment has a
 * special tag that identifies its use in the documentation generation process.
 * <p>
 * The format of a documentation comment is
 * <p>
 * <!-- MAGIC_WORD: The text to capture -->
 * <p>
 * For example:
 * <p>
 * <!--DESCRIPTION: I am a teapot -->
 */
public class MqDocumentationCommentFactory {

    Element parentXml;

    MqDocumentationCommentFactory(Element parentXml) {
        this.parentXml = parentXml;
    }

    /**
     * If the input parameter is a DOM comment node, attempt to create an object. Otherwise return
     * null.
     *
     * @param node
     * @return MqDocumentationComment or null
     */
    public static MqDocumentationComment create(Node node) {
        if (node instanceof Comment) {
            String comment = ((Comment) node).getData();
            return create(comment);
        }
        return null;
    }

    /**
     * If the input parameter represents a valid documentation comment, create an object. Otherwise
     * return null.
     *
     * @param comment
     * @return
     */
    static MqDocumentationComment create(String comment) {
        comment = comment.trim();
        for (MagicToken token : MagicToken.values()) {
            if (comment.startsWith(token.toString())) {
                return new MqDocumentationComment(token, extractDocumentation(token, comment));
            }
        }
        return null;
    }

    static String extractDocumentation(MagicToken token, String comment) {
        return comment.substring(token.toString().length() + 1).trim();
    }

    public List<MqDocumentationComment> getAllDocumentation() {
        return parentXml.childNodes().stream().map(MqDocumentationCommentFactory::create)
            .filter(Objects::nonNull).collect(
                Collectors.toList());
    }
}
