package icd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;

public class MqAddress {

    String routingType;
    String name;
    File asciiDoctorTemplate;
    List<DocumentationComment> documentationComments = new ArrayList<>();

    MqAddress(Element xmlAddress) {
        name = xmlAddress.attr("name");
        documentationComments = xmlAddress.childNodes().stream().map(DocumentationComment::attemptToCreate).filter(Objects::nonNull).collect(Collectors.toList());

        if (xmlAddress.getElementsByTag("multicast").size() == 1) {
            routingType = "multicast";
        } else if (xmlAddress.getElementsByTag("anycast").size() == 1) {
            routingType = "anycast";
        } else {
            throw new IllegalArgumentException(String.format("Expected MQ address to be either 'multicast' or 'anycast', but %s is not", name));
        }
    }
}
