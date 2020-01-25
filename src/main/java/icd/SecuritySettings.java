package icd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class SecuritySettings {

  String match;
  Map<String, List<String>> typeToPermissions = new HashMap<>();

  public SecuritySettings parse(Element element) {
    match = element.attr("match");
    element.children().forEach(this::parsePermission);
    return this;
  }

  void parsePermission(Element element) {
    if (!element.tagName().equals("permission")) return;
    String type = element.attr("type");
    String roles = element.attr("roles");
    Arrays.asList(roles.split(","));
    typeToPermissions.put(type, Arrays.asList(roles.split(",")));
  }
}
