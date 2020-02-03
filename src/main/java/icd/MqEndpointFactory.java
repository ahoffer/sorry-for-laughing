package icd;

import static icd.MqSecuritySetting.noMatch;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * This class is part of the Interface Control Document (ICD) generation component. It models the
 * all the relevant information in the MQ configuration file. Given the configuration file, it
 * returns a list of endpoints sorted by name.
 */
class MqEndpointFactory {

    Document document;
    MqSecuritySettingFactory securityFactory;
    List<MqAddress> addresses;
    Collection<MqSecuritySetting> allSecuritySettings;

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
        allSecuritySettings = securityFactory.getAllSecuritySettings();
        addresses = addressFactory(document);
        return createEndpoints();
    }

    List<MqEndpoint> createEndpoints() {
        List<MqEndpoint> endpoints = new ArrayList<>();
        addresses.forEach(address -> {
            MqSecuritySetting securitySetting =
                allSecuritySettings.stream()
                    .reduce(noMatch(),
                        (acc, next) -> acc.returnHigherScore(address.getName(), next));
            endpoints.add(new MqEndpoint(address, securitySetting));
        });

        Collections.sort(endpoints);
        return endpoints;
    }

    List<MqAddress> addressFactory(Document document) {
        Elements addresses = document.getElementsByTag("addresses");
        Validate.isTrue(addresses.size() == 1, "Expected a single <addresses> element");
        Elements endpointAddresses = addresses.first()
            .getElementsByTag("address");
        return endpointAddresses.stream().map(MqAddress::new)
            .collect(toList());
    }
}
