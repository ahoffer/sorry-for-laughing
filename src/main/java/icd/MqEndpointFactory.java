package icd;

import static icd.MqSecuritySetting.noMatch;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class is part of the Interface Control Document (ICD) generation component. It models the an
 * all the relevant information in the MQ configuration file. It is composed of a list of
 * endpoints.
 */
public class MqEndpointFactory {

    Document document;
    MqSecuritySettingFactory securityFactory;
    List<MqAddress> addresses;
    Collection<MqSecuritySetting> securitySettings;

    public MqEndpointFactory(Document document) {
        this.document = document;
    }

    public MqEndpointFactory(File file) throws IOException {
        this(Jsoup.parse(file, "UTF-8"));
    }


    /**
     * Return a list of lexically sorted MQ endpoints.
     */
    public List<MqEndpoint> getAllEndpoints() {
        securityFactory = new MqSecuritySettingFactory(document);
        securitySettings = securityFactory.getAllSecuritySettings();
        addresses = addressFactory(document);
        return createEndpoints();
    }

    protected List<MqEndpoint> createEndpoints() {
        List<MqEndpoint> endpoints = new ArrayList<>();
        for (MqAddress address : addresses) {
            // TODO: Test where there is not catch-all ("#") permission
            MqSecuritySetting securitySetting =
                securitySettings.stream()
                    .reduce(noMatch(),
                        (acc, next) -> acc.returnHigherScore(address.getName(), next));
            endpoints.add(new MqEndpoint(address, securitySetting));
        }
        return endpoints;
    }

    protected List<MqAddress> addressFactory(Document doc) {
        return doc.getElementsByTag("address").stream().map(MqAddress::new)
            .sorted(Comparator.comparing(MqAddress::getName))
            .collect(toList());
    }
}
