package tools;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import lombok.NoArgsConstructor;

import java.util.Iterator;

@NoArgsConstructor
public class AgentActivitiesLogger {

    // ToDo#1 Check whether this has to be written with Iterator;
    public static void sendMessage(ACLMessage aclMessage, Agent agent) {
        for (Iterator iterator = aclMessage.getAllIntendedReceiver(); iterator.hasNext();) {
            System.out.println("Agent: " + agent + " sent a message to: " + iterator.next() + "." +
                    "Message content: " + aclMessage);
        }
    }

    public static void receiveMessage(ACLMessage aclMessage, Agent agent) {
        System.out.println("Agent: " + agent + " received a message from: " + aclMessage.getSender() + "." +
                "Message content: " + aclMessage);
    }
}
