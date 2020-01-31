package icd;

import java.io.PrintStream;

public class MqDocumentationComment {

    /**
     * This class is part of the Interface Control Document (ICD) generation component. It models an
     * XML comment in the MQ configuration file.
     **/

    final MagicToken token;
    final String text;

    public MqDocumentationComment(MagicToken token, String text) {
        this.token = token;
        this.text = text;
    }


    public void debugPrintOn(PrintStream printStream, String parentIndex) {
        String myIndent = parentIndex + "\t";
        printStream.println(myIndent + token + ":" + text);
    }
}
