package icd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

public class IcdDocumentationUnitTest {

    static String getResourceAsString(String filename) throws IOException {
        return IOUtils.toString(
            getResourceFile(filename).toURI(),
            StandardCharsets.UTF_8);
    }

    static File getResourceFile(String filename) {
        return new File(IcdDocumentationUnitTest.class.getResource(filename).getFile());
    }

    static Document getResourceAsDocument(String filename) throws IOException {
        return Jsoup.parse(getResourceAsString(filename));
    }

    @Test
    public void testAddress() throws IOException {
        Element e = getResourceAsDocument("/address.xml").getElementsByTag("address").first();
        MqAddress addr = new MqAddress(e);
        assertThat(addr.getName(), is("qname"));
        assertThat(addr.getRoutingType(), is("anycast"));
        assertThat(addr.getDocumentationComments().size(), is(1));
    }

    @Test
    public void testSecurity() throws IOException {
        List<MqSecuritySetting> mqSecuritySettings = new MqSecuritySettingFactory(
            getResourceAsDocument("/security.xml"))
            .getAllSecuritySettings();
        assertThat(mqSecuritySettings, hasSize(1));
        MqSecuritySetting securitySetting = mqSecuritySettings.get(0);
        assertThat(securitySetting.match, is("input.#"));
        MultiValuedMap<String, String> permissions = securitySetting.permissionToInternalRoles;
        assertThat(permissions.get("type"), containsInAnyOrder("one", "two", "three"));
    }


    @Test
    public void testRecognizedComment() throws IOException {
        // Test trimming the string too
        MqDocumentationComment dc = MqDocumentationCommentFactory.create("DESCRIPTION:teapot  ");
        assertThat(dc.token, is(MagicToken.DESCRIPTION));
        assertThat(dc.text, is("teapot"));
    }

    @Test
    public void testUnrecognizedComment() throws IOException {
        MqDocumentationComment dc = MqDocumentationCommentFactory
            .create("UNRECOGNIZED: I am not a teapot");
        assertThat(dc, nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddressesElementNotFound() throws IOException {
        new MqEndpointFactory(getResourceFile("/missing-addresses.xml")).getAllEndpoints();
    }
}
