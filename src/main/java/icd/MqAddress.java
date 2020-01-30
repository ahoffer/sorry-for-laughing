package icd;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import org.jsoup.nodes.Element;

public class MqAddress {

    String routingType;
    String name;
    Collection<MqDocumentationComment> documentationComments;

    // TODO: How do we turn these objects into documentation? Templated Asciidoctor?
    File asciiDoctorTemplate;

    MqAddress(Element xmlAddress) {
        name = xmlAddress.attr("name");
        documentationComments = new MqDocumentationCommentFactory(xmlAddress)
            .getAllDocumentation();

        if (xmlAddress.getElementsByTag("multicast").size() == 1) {
            routingType = "multicast";
        } else if (xmlAddress.getElementsByTag("anycast").size() == 1) {
            routingType = "anycast";
        } else {
            throw new IllegalArgumentException(String
                .format("Expected MQ address to be either 'multicast' or 'anycast', but %s is not",
                    getName()));
        }
    }


    public String getRoutingType() {
        return routingType;
    }

    public String getName() {
        return name;
    }

    public Collection<MqDocumentationComment> getDocumentationComments() {
        return documentationComments;
    }


}

