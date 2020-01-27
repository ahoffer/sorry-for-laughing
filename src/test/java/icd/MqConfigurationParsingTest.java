package icd;

import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsMapContaining;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class MqConfigurationParsingTest {

    @Test
    public void testAddress() throws IOException {
        Element e = getResourceAsDocument("/address.xml").getElementsByTag("address").first();
        MqAddress addr = new MqAddress(e);
        assertThat(addr.name, is("qname"));
        assertThat(addr.routingType, is("anycast"));
        assertThat(addr.documentationComments.size(), is(1));
        assertThat(addr.documentationComments.get(0).text, is("I am a teapot"));
    }

    @Test
    public void testSecurity() throws IOException {
        Element e = getResourceAsDocument("/security.xml").getElementsByTag("security-setting").first();
        MqSecuritySetting mqSecuritySettings = new MqSecuritySetting(e);
        assertThat(mqSecuritySettings.match, is("input.#"));
        Map<String, List<String>> permissions = mqSecuritySettings.typeToPermissions;
        assertThat(permissions.size(), is(1));
        assertThat(permissions, IsMapContaining.hasKey(("type")));
        assertThat(permissions.get("type"), containsInAnyOrder("one", "two", "three"));
    }

    @Test
    public void testRecognizedComment() throws IOException {
        // Test trimming the string too
        DocumentationComment dc =
                DocumentationComment.attemptToCreate("DESCRIPTION:teapot  ");
        assertThat(dc.token, is(MagicToken.DESCRIPTION));
        assertThat(dc.text, is("teapot"));
    }

    @Test
    public void testUnrecognizedComment() throws IOException {
        // Test trimming the string too
        DocumentationComment dc =
                DocumentationComment.attemptToCreate("UNRECOGNIZED: I am not a teapot");
        assertThat(dc, nullValue());
    }

    static String getResourceAsString(String filename) throws IOException {
        return IOUtils.toString(
                new File(MqConfigurationComponentTest.class.getResource(filename).getFile()).toURI());
    }

    static Document getResourceAsDocument(String filename) throws IOException {
        return Jsoup.parse(getResourceAsString(filename));
    }
}
