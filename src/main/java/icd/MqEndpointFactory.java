package icd;

import org.jsoup.nodes.Document;
import java.util.*;

import static icd.MqSecuritySetting.*;
import static java.util.stream.Collectors.*;

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
                    .reduce(noMatch(), (acc, next) -> acc.returnHigherScore(address.name, next));
            endpoints.add(new MqEndpoint(address, securitySetting));
        }
        return endpoints;
    }

    protected List<MqAddress> addressFactory(Document doc) {
        return doc.getElementsByTag("address").stream().sorted().map(MqAddress::new)
            .collect(toList());
    }
}
