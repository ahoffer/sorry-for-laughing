package icd;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;


/**
 * This class is part of the Interface Control Document (ICD) generation component. It models an
 * Artemis endpoint. It includes the endpoint's name, routing type (any/multi cast), permissions,
 * and documentation comments. This class assumes that every endpoint has a unique name.
 */
class MqEndpoint implements Comparable<MqEndpoint> {

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
     */
    public MultiValuedMap<String, String> getPermissions() {
        return new ArrayListValuedHashMap<>(securitySetting.permissionToLdapRoles);
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

    void debugPrintOn(PrintStream printStream, String parentIndex) {
        String plusOneIndent = parentIndex + "\t";

        printStream.println(parentIndex + "Name: " + getName());
        printStream.println(plusOneIndent + "Routing type: " + getRoutingType());
        if (getDocumentation().isEmpty()) {
            printStream
                .println(plusOneIndent + "Documentation: No documentation for this endpoint");
        } else {
            printStream.println(plusOneIndent + "Documentation:");
            getDocumentation().forEach(c -> c.debugPrintOn(printStream, plusOneIndent));
        }

        securitySetting.debugPrintOn(printStream, parentIndex + "\t");

        //Print blank line
        printStream.println();
    }

    @Override
    public int compareTo(MqEndpoint o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
}
