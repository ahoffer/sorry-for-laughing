package icd;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;

public class MqConfigurationComponentTest {

  @Test
  public void testFullConfiguration() throws IOException {
    Document doc = MqConfigurationParsingTest.getResourceAsDocument("/artemis.xml");
    MqConfiguration config = new MqConfiguration().parse(doc);
    assertThat(config.size(), is(2));
    MqEndpoint afdcgsEndpoint = config.get(0);
    assertThat(afdcgsEndpoint.getName(), is("afdcgs.geoint.chat.message.high"));
    assertThat(afdcgsEndpoint.getRoutingType(), is("multicast"));
    Map<String, List<String>> permissions = afdcgsEndpoint.getPermissions();
    assertThat(permissions.size(), is(2));
    assertThat(permissions, hasKey("createNonDurableQueue"));
    assertThat(permissions.get("createNonDurableQueue"), containsInAnyOrder("nonDurable"));
    MqEndpoint pulseEndpoint = config.get(1);
    assertThat(pulseEndpoint.getName(), is("pulse.mission.information"));
    assertThat(pulseEndpoint.getRoutingType(), is("anycast"));
    Map<String, List<String>> pulsePermissions = pulseEndpoint.getPermissions();
    assertThat(pulsePermissions.size(), is(3));
    assertThat(pulsePermissions, hasKey("createNonDurableQueue"));
    assertThat(pulsePermissions, hasKey("deleteDurableQueue"));
    assertThat(pulsePermissions, hasKey("addedtype"));
    assertThat(
        permissions.get("createNonDurabflupleQueue"), containsInAnyOrder("nonDurable", "addRole"));
    assertThat(pulsePermissions.get("deleteDurableQueue"), containsInAnyOrder("manager", "tester"));
    assertThat(pulsePermissions.get("addedType"), containsInAnyOrder("manager"));
  }
}
