package roles;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParkingAgent extends Agent {

    private double parkingAgentXCoordinate;
    private double parkingAgentYCoordinate;
    private int maxCapacity;
    private int freeSpaces;

    // ToDo#2 Ask, why does initialization by GUI gives the System.out.println'es -
    //  and initialization on Main does not?
    @Override
    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready to work.");
        setBehaviourQueue();

//        AgentActivitiesLogger agentActivitiesLogger;
//        agentActivitiesLogger = new AgentActivitiesLogger();

//        AID receiverAgentID = new AID("Agent02", AID.ISLOCALNAME);
//        ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
//        Methods.configureACLMessage(aclMessage, "OpenDoor01Now" , receiverAgentID);
//        aclMessage.setSender(getAID());
//        aclMessage.setReplyWith("Door01Reply");
//        send(aclMessage);
        if (maxCapacity == 0) {
            doDelete();
        }
    }

    private void setBehaviourQueue() {
        OneShotBehaviour initializeParkingData = new InitializeParkingData();
        OneShotBehaviour sayHello = new SayHello();
        addBehaviour(initializeParkingData);
        addBehaviour(sayHello);
    }

    public class InitializeParkingData extends OneShotBehaviour {
        public void action() {
            System.out.println("Please, enter parking's configuration parameters in the following order: x " +
                    "coordinate," +
                    " y coordinate, maximum capacity, free spaces. Split numbers with comma.");
            Object[] arguments = getArguments();
            if (arguments != null && arguments.length > 0) {
                parkingAgentXCoordinate = Double.parseDouble(arguments[0].toString());
                parkingAgentYCoordinate = Double.parseDouble(arguments[1].toString());
                maxCapacity = Integer.parseInt(arguments[2].toString());
                freeSpaces = Integer.parseInt(arguments[3].toString());
            }
        }
    }

    public class SayHello extends OneShotBehaviour {
        public void action() {
            System.out.println("Agent's local name is: " + getAID().getLocalName());
            System.out.println("Agent's addresses are: " + String.join(", ", getAID().getAddressesArray()));
            System.out.println("Agent's coordinates: (" + parkingAgentXCoordinate + ";" + parkingAgentYCoordinate +
                    ")");
            System.out.println("Parking maximum capacity: " + maxCapacity);
            System.out.println("Parking free spaces: " + freeSpaces);
        }
    }

    public class TestOneShotBeahviour extends OneShotBehaviour {
        public void action() {
            // doSomething
            // for example say Hello and initialize parkingData ;)

        }
        // done() always returns true in OneShotBehaviours and is final - thus cannot be overwritten
    }

    public class TestCyclicBehaviour extends CyclicBehaviour {
        public void action() {
            //doSomething
            // for example wait for the message
        }
        // done() always returns false in CyclicBehaviours - have to use block()
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " has done his work and is going to be turned off.");
    }

}