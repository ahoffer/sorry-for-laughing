package icd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MqSecuritySettingFactory {


    private final Document document;
    // This field is derived from the "role-mapping" elements in the MQ configuration file. However,
    // the file maps LDAP roles to internal roles, but this field reverse the keys and values.
    // NOTE: If this field is not populated, the permission-to-ldap-roles map will always be empty.
    MultiValuedMap<String, String> internalRoleToLdapRoles = new ArrayListValuedHashMap<>();

    public MqSecuritySettingFactory(Document document) {
        this.document = document;
        populateRoleMappings();

    }

    void populateRoleMappings() {
        // This method reverses the keys and values in the XML
        document.getElementsByTag("role-mapping").stream().forEach(rolemapXml ->
            internalRoleToLdapRoles.put(rolemapXml.attr("to"), rolemapXml.attr("from")));

        if (internalRoleToLdapRoles.isEmpty()) {
            //TODO Log a warning that there are no role mappings
        }
    }

    public List<MqSecuritySetting> getAllSecuritySettings() {
        return document.getElementsByTag("security-setting").stream()
            .map(this::create)
            .collect(Collectors.toList());
    }

    MqSecuritySetting create(Element securitySettingXml) {
        Map<String, List<String>> permissionToInternalRoles = new HashMap<>();
        Map<String, List<String>> permissionToLdapRoles = new HashMap<>();

        securitySettingXml.children().stream().forEach(permissionXml -> {

            String permission = getPermissionName(permissionXml);
            List<String> internalRoles = getRoles(permissionXml);
            List<String> ldapRoles = getLdapRoles(internalRoles);

            if (internalRoles.isEmpty()) {
                //TODO Log a message that no roles are assigned to this permission
            } else {
                permissionToInternalRoles.put(permission, internalRoles);

            }

            if (ldapRoles.isEmpty()) {
                //TODO Log a message that no LDAP roles are assigned to this permission
            } else {
                permissionToLdapRoles.put(permission, ldapRoles);

            }
        });

        return new MqSecuritySetting(getNameMatchingString(securitySettingXml),
            permissionToInternalRoles,
            permissionToLdapRoles);
    }


    private String getPermissionName(Element permissionXml) {
        return permissionXml.attr("type");
    }

    private String getNameMatchingString(Element securitySettingXml) {
        return securitySettingXml.attr("match");
    }

    private List<String> getRoles(Element permissionXml) {
        return getRolesFromString(permissionXml.attr("roles"));
    }


    List<String> getRolesFromString(String roles) {
        return (Arrays.stream(roles.split(",")).sorted().collect(Collectors.toList()));
    }

    List<String> getLdapRoles(List<String> internalRoles) {
        //TODO: This can probably be replaced by a stream that uses flatMap
        List<String> ldapRoles = new ArrayList<>();
        internalRoles.forEach(role -> ldapRoles.addAll(internalRoleToLdapRoles.get(role)));

        Collections.sort(ldapRoles);
        return ldapRoles;

    }


}
