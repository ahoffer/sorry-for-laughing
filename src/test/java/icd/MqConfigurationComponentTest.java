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
    MqEndpoint endpoint = config.get(0);
    assertThat(endpoint.getName(), is("afdcgs.geoint.chat.message.high"));
    assertThat(endpoint.getRoutingType(), is("multicast"));
    Map<String, List<String>> permissions = endpoint.getPermissions();
    assertThat(permissions.size(), is(2));
    assertThat(permissions, hasKey("createNonDurableQueue"));
    assertThat(permissions.get("createNonDurableQueue"), containsInAnyOrder("nonDurable"));
    MqEndpoint endpoint1 = config.get(1);
    assertThat(endpoint1.getName(), is("pulse.mission.information"));
    assertThat(endpoint1.getRoutingType(), is("anycast"));
    Map<String, List<String>> permissions2 = endpoint1.getPermissions();
    assertThat(permissions2.size(), is(3));
    assertThat(permissions2, hasKey("createNonDurableQueue"));
    assertThat(permissions2, hasKey("deleteDurableQueue"));
    assertThat(permissions2, hasKey("addedtype"));
    assertThat(
        permissions.get("createNonDurabflupleQueue"), containsInAnyOrder("nonDurable", "addRole"));
    assertThat(permissions2.get("deleteDurableQueue"), containsInAnyOrder("manager", "tester"));
    assertThat(permissions2.get("addedType"), containsInAnyOrder("manager"));
  }
}
