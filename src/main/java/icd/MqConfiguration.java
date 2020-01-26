package icd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.nodes.Document;

public class MqConfiguration {

  List<MqEndpoint> endpoints = new ArrayList<>();

  public MqEndpoint get(int i) {
    return endpoints.get(i);
  }

  public int size() {
    return endpoints.size();
  }

  public MqConfiguration parse(org.jsoup.nodes.Document doc) {
    Set<MqAddress> addresses =
        doc.getElementsByTag("address").stream().map(MqAddress::new).collect(Collectors.toSet());

    Set<MqSecuritySetting> security =
        doc.getElementsByTag("security-setting").stream()
            .map(MqSecuritySetting::new)
            .collect(Collectors.toSet());

   Object mergedSecurity = security.stream()
        .reduce(
            (mqSecuritySetting, mqSecuritySetting2) -> mqSecuritySetting.merge(mqSecuritySetting2));
    return this;
  }
}
