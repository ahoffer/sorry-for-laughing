package icd;

import org.jsoup.nodes.Document;

import java.util.*;
import java.util.stream.Collectors;

import static icd.MqSecuritySetting.*;
import static java.util.Comparator.comparing;

public class MqConfiguration {

    protected Set<MqEndpoint> endpoints = new HashSet<>();

    public MqConfiguration(Document doc) {
        parse(doc);
    }

    public List<MqEndpoint> getEndpoints() {
        return endpoints.stream().sorted(comparing(MqEndpoint::getName)).collect(Collectors.toList());
    }

    public int size() {
        return endpoints.size();
    }

    public void parse(org.jsoup.nodes.Document doc) {
        Set<MqAddress> addresses =
                doc.getElementsByTag("address").stream().map(MqAddress::new).collect(Collectors.toSet());

        Set<MqSecuritySetting> security =
                doc.getElementsByTag("security-setting").stream()
                        .map(MqSecuritySetting::new)
                        .collect(Collectors.toSet());

        for (MqAddress address : addresses) {
            String name = address.name;
            MqSecuritySetting securitySetting = security.stream().reduce(noMatch(), (acc, next) ->
                    acc.returnHigherScore(name, next));

            endpoints.add(new MqEndpoint(address, securitySetting));
        }
    }
}
