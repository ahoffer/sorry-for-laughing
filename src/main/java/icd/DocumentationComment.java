package icd;

import java.util.Arrays;
import java.util.Optional;

public class DocumentationComment {

  ;

  public static Optional<DocumentationComment> attemptToCreate(String comment) {
    return Optional.ofNullable(
        Arrays.stream(MagicToken.values())
            .filter(token -> comment.startsWith(token.toString()))
            .findAny()
            .map(token -> new DocumentationComment(token, extractText(token, comment)))
            .orElse(null));
  }

  static String extractText(MagicToken token, String comment) {
    return comment.substring(token.toString().length() + 1).trim();
  }

  MagicToken token;
  String text;

  public DocumentationComment(MagicToken token, String text) {
    this.token = token;
    this.text = text;
  }
}
