package icd;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        return address.getName();
    }

    public String getRoutingType() {
        return address.getRoutingType();
    }

    public List<MqDocumentationComment> getDocumentation() {
        return new ArrayList<>(address.getDocumentationComments());
    }

    void debugPrintOn(PrintStream printStream) {
        debugPrintOn(printStream, "");
    }

    void debugPrintOn(PrintStream printStream, String indent) {
        printStream.println(indent + getName());
        printStream.println(indent + "\t" + getRoutingType());
        getDocumentation().forEach(c -> c.debugPrintOn(printStream, indent + "\t"));
        securitySetting.debugPrintOn(printStream, indent + "\t");
        //Print blank line
        printStream.println();
    }
}
