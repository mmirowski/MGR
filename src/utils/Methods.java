package utils;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

//import static utils.Constants.MESSAGE_LANGUAGE;

public final class Methods {

    public static void configureACLMessage(ACLMessage aclMessage, String messageContent, AID receiverAgentID) {
        aclMessage.setContent(messageContent);
        aclMessage.addReceiver(receiverAgentID);
//        aclMessage.setLanguage(MESSAGE_LANGUAGE);
        aclMessage.setDefaultEnvelope();


        //aclMessage.setOntology("I guess I won't be using an ontology");
    }
}
