package icd;

import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.*;

public class MqEndpoint {

    private final MqAddress address;
    private final MqSecuritySetting securitySetting;

    public MqEndpoint(MqAddress address, MqSecuritySetting securitySetting) {
        notNull(address);
        notNull(securitySetting);
        this.address = address;
        this.securitySetting = securitySetting;
    }

    public Map<String, List<String>> getPermissions() {
        return null;
    }

    public String getName() {
        return address.name;
    }


    public String getRoutingType() {
        return null;
    }
}
