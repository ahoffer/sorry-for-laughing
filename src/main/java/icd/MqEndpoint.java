package icd;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.*;

public class MqEndpoint {

    protected final MqAddress address;
    protected final MqSecuritySetting securitySetting;

    public MqEndpoint(MqAddress address, MqSecuritySetting securitySetting) {
        notNull(address);
        notNull(securitySetting);
        this.address = address;
        this.securitySetting = securitySetting;
    }

    public Map<String, List<String>> getPermissions() {
        return new HashMap<>(securitySetting.typeToPermissions);
    }

    public String getName() {
        return address.name;
    }

    public String getRoutingType() {
        return address.routingType;
    }

    public List<DocumentationComment> getDocumentation() {
        return new ArrayList<>(address.documentationComments);
    }
}
