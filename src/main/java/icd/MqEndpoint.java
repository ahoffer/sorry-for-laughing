package icd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.*;


/**
 * This class is part of the Interface Control Document (ICD) generation component. It models the an
 * Artemis endpoint. It includes the endpoint's name, routing type (any/multi cast), permissions,
 * and documentation comments.
 */
public class MqEndpoint {

    protected final MqAddress address;
    protected final MqSecuritySetting securitySetting;

    /**
     * Unlike other Mq documentation model classes, this not created from a DOM. Instead, the object
     * that owns the list of endpoints extracts the adddress and security elements from the MQ
     * configuration file and matches them. Those objects are used to create the endpoint.
     *
     * @param address
     * @param securitySetting
     */
    public MqEndpoint(MqAddress address, MqSecuritySetting securitySetting) {
        notNull(address);
        notNull(securitySetting);
        this.address = address;
        this.securitySetting = securitySetting;
    }

    /**
     * Return the LDAP roles associated with this permission.
     *
     * @return
     */
    public Map<String, List<String>> getPermissions() {
        return new HashMap<>(securitySetting.permissionToLdapRoles);
    }

    public String getName() {
        return address.name;
    }

    public String getRoutingType() {
        return address.routingType;
    }

    public List<MqDocumentationComment> getDocumentation() {
        return new ArrayList<>(address.documentationComments);
    }
}
