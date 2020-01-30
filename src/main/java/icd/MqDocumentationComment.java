package icd;

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


}
