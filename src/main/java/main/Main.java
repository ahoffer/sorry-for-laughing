package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.deployers.impl.FileConfigurationParser;

public class Main {
  public static void main(String[] args) throws Exception {

    File brokerFile = new File("/home/aaron/Downloads/artemis.xml");
    InputStream istream = new FileInputStream(brokerFile);
    MyFileConfigurationParser parser = new MyFileConfigurationParser();
    Configuration config = parser.parseMainConfig(istream);
  }
}
