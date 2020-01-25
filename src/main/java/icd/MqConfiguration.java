package icd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.nodes.Document;

public class MqConfiguration  {

  List<MqEndpoint> endpoints = new ArrayList<>();

  public MqEndpoint get(int i) {
    return endpoints.get(i);
  }

  public int size() {
    return endpoints.size();
  }

  public MqConfiguration parse(Document doc) {
    return null;
  }
}
