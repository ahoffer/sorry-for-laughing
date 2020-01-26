package icd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class MqAddress {

  String routingType;
  String name;
  File asciiDoctorTemplate;
  List<DocumentationComment> doclets = new ArrayList<>();

  MqAddress(Element element) {
    parse(element);
  }

  MqAddress parse(Element e) {
    name = e.attr("name");
    e.childNodes()
        .forEach(
            endpointChild -> {
              processComment(endpointChild);
              processRoutingType(endpointChild);
            });
    return this;
  }

  void processRoutingType(Node node) {
    if (!(node instanceof org.jsoup.nodes.Element)) return;
    String tagName = ((org.jsoup.nodes.Element) node).tagName();
    if (tagName.equals("anycast") || tagName.equals("multicast")) {
      routingType = tagName;
    }
  }

  void processComment(Node node) {
    if (!(node instanceof Comment)) return;
    String comment = ((Comment) node).getData();
    DocumentationComment.attemptToCreate(comment).map(doclets::add);
  }
}
