package icd;

import static icd.IcdDocumentationUnitTest.getResourceFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.junit.Test;

public class MqEndpointFactoryTest {

    @Test
    public void testFullConfiguration() throws IOException {
        List<MqEndpoint> endpoints = new MqEndpointFactory(getResourceFile("/component_test.xml"))
            .getAllEndpoints();
        assertThat(endpoints.size(), is(2));

        //Verify first endpoint
        MqEndpoint afdcgsEndpoint = endpoints.get(0);
        assertThat(afdcgsEndpoint.getName(), is("afdcgs.geoint.chat.message.high"));
        assertThat(afdcgsEndpoint.getRoutingType(), is("multicast"));
        assertThat(afdcgsEndpoint.getDocumentation(), hasSize(1));
        MultiValuedMap<String, String> afdcgsLdapPermissions = afdcgsEndpoint.getPermissions();
        MultiValuedMap<String, String> afdcgsInternalPermissions = afdcgsEndpoint.securitySetting.permissionToInternalRoles;

        // There are no LDAP permissions that map to "createDurableQueue" so key should not exist
        assertThat(afdcgsLdapPermissions.get("createNonDurableQueue"), empty());

        assertThat(afdcgsInternalPermissions.size(), is(1));
        assertThat(afdcgsInternalPermissions.get("createNonDurableQueue"), hasSize(1));
        assertThat(afdcgsInternalPermissions.get("createNonDurableQueue"),
            containsInAnyOrder("analyst"));

        // Verify second endpoint
        MqEndpoint pulseEndpoint = endpoints.get(1);
        assertThat(pulseEndpoint.getName(), is("pulse.mission.information"));
        assertThat(pulseEndpoint.getRoutingType(), is("anycast"));
        MultiValuedMap<String, String> pulseInternalPermissions = pulseEndpoint.securitySetting.permissionToInternalRoles;
        assertThat(pulseInternalPermissions.size(), is(2));
        assertThat(
            pulseInternalPermissions.get("thing1"), containsInAnyOrder("admin"));
        assertThat(pulseInternalPermissions.get("thing2"), containsInAnyOrder("manager"));
        MultiValuedMap<String, String> pulseLdapPermissions = pulseEndpoint.getPermissions();
        assertThat(pulseLdapPermissions.get("thing2"),
            containsInAnyOrder("ent SOA ESB Receiver", "ent SOA ESB Sender"));
        endpoints.forEach(e -> e.debugPrintOn(System.out));
    }

    @Test
    public void processBrokerFile() throws IOException {
        List<MqEndpoint> endpoints = new MqEndpointFactory(getResourceFile("/broker.xml.epp"))
            .getAllEndpoints();
        endpoints.forEach(e -> e.debugPrintOn(System.out));
    }

    @Test
    public void testNoMatchingSecuritySetting() throws IOException {
        List<MqEndpoint> endpoints = new MqEndpointFactory(
            getResourceFile("/no-security-match.xml")).getAllEndpoints();
        assertThat(endpoints, hasSize(1));
        assertThat(endpoints.get(0).getPermissions().isEmpty(), is(true));
    }
}
