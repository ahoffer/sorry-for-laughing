package icd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class MqSecuritySettingFactory {

    private final Document document;
    // This field is derived from the "role-mapping" elements in the MQ configuration file.
    // The file maps LDAP roles to internal roles. This map reverses the keys and values.
    // NOTE: If this field is not populated, no there will be no LDAP for any endpoint.
    MultiValuedMap<String, String> globalLapMappings = new ArrayListValuedHashMap<>();

    public MqSecuritySettingFactory(Document document) {
        this.document = document;
        populateRoleMappings();

    }

    void populateRoleMappings() {
        // This method reverses the keys and values in the XML
        document.getElementsByTag("role-mapping").stream().forEach(rolemapXml ->
            globalLapMappings.put(rolemapXml.attr("to"), rolemapXml.attr("from")));

        if (globalLapMappings.isEmpty()) {
            //TODO Log a warning that there are no role mappings
        }
    }

    public List<MqSecuritySetting> getAllSecuritySettings() {
        return document.getElementsByTag("security-setting").stream()
            .map(this::create)
            .collect(Collectors.toList());
    }

    MqSecuritySetting create(Element securitySettingXml) {
        MultiValuedMap<String, String> permissionToInternalRoles = new ArrayListValuedHashMap<>();
        MultiValuedMap<String, String> permissionToLdapRoles = new ArrayListValuedHashMap<>();
        securitySettingXml.children().stream().forEach(permissionXml -> {
            String permission = permissionXml.attr("type");
            List<String> internalRoles = Arrays.asList(permissionXml.attr("roles").split(","));
            internalRoles
                .forEach(internalRole -> permissionToInternalRoles.put(permission, internalRole));
            internalRoles.stream().map(globalLapMappings::get)
                .flatMap(Collection::stream)
                .forEach(ldapRoles -> permissionToLdapRoles.put(permission, ldapRoles));

            if (permissionToInternalRoles.isEmpty()) {
                //TODO Log a message that no roles are assigned to this permission
            }

            if (permissionToLdapRoles.isEmpty()) {
                //TODO Log a message that no LDAP roles are assigned to this permission
            }
        });

        String nameMatchingString = securitySettingXml.attr("match");
        return new MqSecuritySetting(nameMatchingString,
            permissionToInternalRoles,
            permissionToLdapRoles);
    }

}
