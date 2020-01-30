package icd;

import static icd.MqEndpointFactoryParsingTest.getResourceAsDocument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class MqEndpointFactoryComponentTest {

    @Test
    public void testFullConfiguration() throws IOException {
        Document doc = getResourceAsDocument("/artemis.xml");
        List<MqEndpoint> endpoints = new MqEndpointFactory(doc).getAllEndpoints();
        assertThat(endpoints.size(), is(2));

        //Verify first endpoint
        MqEndpoint afdcgsEndpoint = endpoints.get(0);
        assertThat(afdcgsEndpoint.getName(), is("afdcgs.geoint.chat.message.high"));
        assertThat(afdcgsEndpoint.getRoutingType(), is("multicast"));
        assertThat(afdcgsEndpoint.getDocumentation(), hasSize(1));
        Map<String, List<String>> afdcgsLdapPermissions = afdcgsEndpoint.getPermissions();
        Map<String, List<String>> afdcgsInternalPermissions = afdcgsEndpoint.securitySetting.permissionToInternalRoles;

        // There are no LDAP permissions that map to "createDurableQueue" so key should not exist
        assertThat(afdcgsLdapPermissions, not(hasKey("createNonDurableQueue")));

        assertThat(afdcgsInternalPermissions.size(), is(1));
        assertThat(afdcgsInternalPermissions, hasKey("createNonDurableQueue"));
        assertThat(afdcgsInternalPermissions.get("createNonDurableQueue"),
            containsInAnyOrder("analyst"));

        // Verify second endpoint
        MqEndpoint pulseEndpoint = endpoints.get(1);
        assertThat(pulseEndpoint.getName(), is("pulse.mission.information"));
        assertThat(pulseEndpoint.getRoutingType(), is("anycast"));
        Map<String, List<String>> pulseInternalPermissions = pulseEndpoint.securitySetting.permissionToInternalRoles;
        assertThat(pulseInternalPermissions.size(), is(2));
        assertThat(pulseInternalPermissions, hasKey("thing1"));
        assertThat(pulseInternalPermissions, hasKey("thing2"));
        assertThat(
            pulseInternalPermissions.get("thing1"), containsInAnyOrder("admin"));
        assertThat(pulseInternalPermissions.get("thing2"), containsInAnyOrder("manager"));
        Map<String, List<String>> pulseLdapPermissions = pulseEndpoint.getPermissions();
        assertThat(pulseLdapPermissions.size(), is(1));
        assertThat(pulseLdapPermissions, hasKey("thing2"));
        assertThat(pulseLdapPermissions.get("thing2"),
            contains("ent SOA ESB Receiver", "ent SOA ESB Sender"));


        endpoints.forEach(e-> e.debugPrintOn(System.out));
    }



}
