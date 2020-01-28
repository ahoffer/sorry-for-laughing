package icd;

import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static icd.MqConfigurationParsingTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;

public class MqConfigurationComponentTest {

    @Test
    public void testFullConfiguration() throws IOException {
        Document doc = getResourceAsDocument("/artemis.xml");
        MqConfiguration config = new MqConfiguration(doc);
        assertThat(config.size(), is(2));
        List<MqEndpoint> endpoints = config.getEndpoints();
        MqEndpoint afdcgsEndpoint = endpoints.get(0);
        assertThat(afdcgsEndpoint.getName(), is("afdcgs.geoint.chat.message.high"));
        assertThat(afdcgsEndpoint.getRoutingType(), is("multicast"));
        Map<String, List<String>> permissions = afdcgsEndpoint.getPermissions();
        assertThat(permissions.size(), is(1));
        assertThat(permissions, hasKey("createNonDurableQueue"));
        assertThat(permissions.get("createNonDurableQueue"), containsInAnyOrder("nonDurable"));
        MqEndpoint pulseEndpoint = endpoints.get(1);
        assertThat(pulseEndpoint.getName(), is("pulse.mission.information"));
        assertThat(pulseEndpoint.getRoutingType(), is("anycast"));
        Map<String, List<String>> pulsePermissions = pulseEndpoint.getPermissions();
        assertThat(pulsePermissions.size(), is(2));
        assertThat(pulsePermissions, hasKey("thing1"));
        assertThat(pulsePermissions, hasKey("thing2"));
        assertThat(
                pulsePermissions.get("thing1"), containsInAnyOrder("admin"));
        assertThat(pulsePermissions.get("thing2"), containsInAnyOrder("manager"));
    }
}
