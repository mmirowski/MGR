package roles;

import dtos.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Methods;

import java.util.LinkedList;
import java.util.List;

import static utils.Constants.TWENTY_SECONDS;

@Getter
@Setter
@AllArgsConstructor
public class ClientAgent extends Agent {

    private UserDto userDto;
    private List<FavouritesDto> favouritesList = new LinkedList<>();
    private List<VehicleDto> vehiclesList = new LinkedList<>();
    private RequestDto requestDto;

    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready to work.");
        setBehavioursQueue();
    }

    private void setBehavioursQueue() {
        addBehaviour(new InitializeUser());
        addBehaviour(new PresentUser());
        addBehaviour(new ManageAccount());
        addBehaviour(new TickerBehaviour(this, TWENTY_SECONDS) {
            @Override
            protected void onTick() {
                updateUserPosition();
            }
        });
        addBehaviour(new PrepareRequest());
        addBehaviour(new SendRequest());
        addBehaviour(new JoinAuction());
        addBehaviour(new ListenForOffers());
        addBehaviour(new AcceptOffer());
        addBehaviour(new Park());
        addBehaviour(new Leave());
        addBehaviour(new ReportBug());
        addBehaviour(new TerminateAgent());
    }

    private class InitializeUser extends OneShotBehaviour {
        public void action() {
            Object[] arguments = getArguments();

            if (arguments != null && arguments.length > 0) {
                userDto.setUserXCoordinate(Double.parseDouble(arguments[0].toString()));
                userDto.setUserYCoordinate(Double.parseDouble(arguments[1].toString()));
                userDto.setMoney(Double.parseDouble(arguments[2].toString()));
                userDto.setName(arguments[3].toString());
                userDto.setHasReservedParkingSpot(false);
            }
        }
    }

    private class PresentUser extends OneShotBehaviour {
        public void action() {
            System.out.println("Parking Agent's local name is: " + getAID().getLocalName());
            System.out.println("Agent's addresses are: " + String.join(", ", getAID().getAddressesArray()));
            System.out.println("Agent's coordinates: (" + userDto.getUserXCoordinate() + ";"
                    + userDto.getUserYCoordinate() + ")");
            System.out.println("Client's nick is: " + userDto.getName());
            System.out.println("Client has: " + userDto.getMoney() + "PLN");
            System.out.println("Client is looking for a parking spot.");
        }
    }

    private class ManageAccount extends OneShotBehaviour {
        public void action() {
            // ToDo#4 ask, if there is any way to stop application run - to insert values for the favourites /
            //  vehicles form in the User profile
            System.out.println("Client's Favourites could be set here (using favouritesList).");
            System.out.println("Also, Client's Vehicles could be configured here (using vehiclesList).");
        }
    }

    private void updateUserPosition() {
        // ToDo#3 pass values read from the Google Maps GPS - right now dummy ones are inserted
        userDto.setUserXCoordinate();
        userDto.setUserYCoordinate();
    }

    private class PrepareRequest extends OneShotBehaviour {
        public void action() {
            // ToDo#5 get input from User considering parking position and parameters ... - so essentially the same
            //  as in #4
            requestDto.setDestinationXCoordinate();
            requestDto.setDestinationYCoordinate();
            requestDto.setMaxDistanceFromDestination();
            requestDto.setMaxCost();
            requestDto.setNeedCoveredParking();
            requestDto.setNeedSecuredParking();
            requestDto.setNeedSpecialParking();
        }
    }

    private class SendRequest extends OneShotBehaviour {
        public void action() {
//            AgentActivitiesLogger agentActivitiesLogger;
//            agentActivitiesLogger = new AgentActivitiesLogger();

            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);

            AID receiverAgentID = new AID("Agent02", AID.ISLOCALNAME);
            aclMessage.addReceiver(receiverAgentID);
            aclMessage.setContent("messageContent");
//          aclMessage.setLanguage(MESSAGE_LANGUAGE);
            aclMessage.setDefaultEnvelope();
            //aclMessage.setOntology("I guess I won't be using an ontology");
            aclMessage.setSender(getAID());
            aclMessage.setReplyWith("Door01Reply");
            aclMessage.setContentObject(new BugReportDto());
            send(aclMessage);

            // messages and all that stuff to the ParkingAgent
        }
    }

    private class TerminateAgent extends OneShotBehaviour {
        public void action() {
            doDelete();
        }
    }

    protected void takeDown() {
        System.out.println("Client " + getLocalName() + " has finished his journeys and is going to be turned off.");
    }
}