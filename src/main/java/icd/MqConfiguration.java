package icd;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static icd.MqSecuritySetting.*;

public class MqConfiguration {

    List<MqEndpoint> endpoints = new ArrayList<>();

    public MqConfiguration(Document doc) {
        parse(doc);
    }

    public MqEndpoint get(int i) {
        return endpoints.get(i);
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

        for (MqAddress addr : addresses) {
            String name = addr.name;
            MqSecuritySetting securitySetting = security.stream().reduce(noMatch(), (acc, next) ->
                    acc.returnHigherScore(name, next));

            endpoints.add(new MqEndpoint(addr, securitySetting));
        }
    }
}
