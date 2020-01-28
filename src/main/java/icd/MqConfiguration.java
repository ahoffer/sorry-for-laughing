package icd;

import org.jsoup.nodes.Document;

import java.util.*;
import java.util.stream.Collectors;

import static icd.MqSecuritySetting.*;
import static java.util.Comparator.comparing;

public class MqConfiguration {

  protected Set<MqEndpoint> endpoints = new HashSet<>();

  public MqConfiguration(Document document) {

    MqSecuritySetting.internalRoleToLdap = createLdapRoleMappings(document);
    Collection<MqSecuritySetting> securitySettings = createSecuritySettings(document);
    createEndpoints(createAddresses(document), securitySettings);
  }

  Map<String, String> createLdapRoleMappings(Document document) {

    // This method reverses the keys and values in the XML
    return document.getElementsByTag("role-mapping").stream()
        .collect(Collectors.toMap(element -> element.attr("to"), element -> element.attr("from")));
  }

  public List<MqEndpoint> getEndpoints() {
    return endpoints.stream().sorted(comparing(MqEndpoint::getName)).collect(Collectors.toList());
  }

  public int size() {
    return endpoints.size();
  }

  protected void createEndpoints(
      Collection<MqAddress> addresses, Collection<MqSecuritySetting> security) {
    for (MqAddress address : addresses) {
      // TODO: Test where there is not catch-all ("#") permission
      MqSecuritySetting securitySetting =
          security.stream()
              .reduce(noMatch(), (acc, next) -> acc.returnHigherScore(address.name, next));
      endpoints.add(new MqEndpoint(address, securitySetting));
    }
  }

  protected Collection<MqSecuritySetting> createSecuritySettings(Document doc) {
    return doc.getElementsByTag("security-setting").stream()
        .map(MqSecuritySetting::new)
        .collect(Collectors.toSet());
  }

  protected Collection<MqAddress> createAddresses(Document doc) {
    return doc.getElementsByTag("address").stream().map(MqAddress::new).collect(Collectors.toSet());
  }
}
