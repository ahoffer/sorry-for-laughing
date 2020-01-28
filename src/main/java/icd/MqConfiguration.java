package icd;

import org.jsoup.nodes.Document;

import java.util.*;
import java.util.stream.Collectors;

import static icd.MqSecuritySetting.*;
import static java.util.Comparator.comparing;

public class MqConfiguration {

    protected Set<MqEndpoint> endpoints = new HashSet<>();
    protected Map<String, String> roleMappings = new HashMap<>();

    public MqConfiguration(Document doc) {
        createEndpoints(createAddresses(doc), createSecuritySettings(doc));
    }

    public List<MqEndpoint> getEndpoints() {
        return endpoints.stream().sorted(comparing(MqEndpoint::getName)).collect(Collectors.toList());
    }

    public int size() {
        return endpoints.size();
    }

    protected   void createEndpoints(Set<MqAddress> addresses, Set<MqSecuritySetting> security) {
        for (MqAddress address : addresses) {
            //TODO: Test where there is not catch-all ("#") permission
            MqSecuritySetting securitySetting = security.stream().reduce(noMatch(), (acc, next) ->
                    acc.returnHigherScore(address.name, next));
            endpoints.add(new MqEndpoint(address, securitySetting));
        }
    }

  protected    Set<MqSecuritySetting> createSecuritySettings(Document doc) {
        return doc.getElementsByTag("security-setting").stream()
                .map(MqSecuritySetting::new)
                .collect(Collectors.toSet());
    }

    protected Set<MqAddress> createAddresses(Document doc) {
        return doc.getElementsByTag("address").stream().map(MqAddress::new).collect(Collectors.toSet());
    }
}
