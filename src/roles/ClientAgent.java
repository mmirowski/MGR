package roles;

import genericBehaviours.ReportBug;
import genericBehaviours.TerminateAgent;
import dtos.FavouritesDto;
import dtos.RequestDto;
import dtos.UserDto;
import dtos.VehicleDto;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import java.io.IOException;
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
        addBehaviour(new ListenForOffers());
        addBehaviour(new JoinAuction());
        // AcceptOffer behaviour should contain inside it's logic diminishing Client funds by the amount of money
        // agreed during the Auction - this will simulate "real" payment process
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
            requestDto.setClient(getAID());
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
            // ClientAgent sending calls for proposals to ParkingAgents
            ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
            // ToDo#11 Same thing as in BankAgent line number 107 - how to get receiver Agent AID? Also, should I
            //  create a Handler Agent, that would send CFPs only to these ParkingAgents, which meet the
            //  requirements? In such case he should be able to get information about every parking from the unique
            //  ParkingAgents and that requires additional communication ...
            // foreach parking add receiver ...
            aclMessage.addReceiver();

            aclMessage.setContent(Constants.CALL_FOR_PROPOSAL_CONTENT);
             aclMessage.setLanguage(Constants.MESSAGE_LANGUAGE);
            //aclMessage.setOntology("I guess I won't be using an ontology");
            aclMessage.setSender(getAID());
            aclMessage.setReplyWith(getAID().getName());

            try {
                aclMessage.setContentObject(requestDto);
            } catch (IOException e) {
                e.printStackTrace();
            }

            aclMessage.setDefaultEnvelope();
            send(aclMessage);
        }
    }

    private class Park extends OneShotBehaviour {
        public void action() {
            // ClientAgent is leaving parking spot and sends request to ParkingAgent to update free spaces
            ACLMessage stayNote = prepareMessageToParkingAgent();
            stayNote.setContent(Constants.CLIENT_IS_STAYING_MESSAGE);
            send(stayNote);
        }
    }

    private ACLMessage prepareMessageToParkingAgent() {
        ACLMessage messageToParkingAgent = new ACLMessage(ACLMessage.REQUEST);
        // ToDo#12 Same as above considering addReceiver ... this time though it is sent only to one ParkingAgent
        messageToParkingAgent.addReceiver();
        messageToParkingAgent.setLanguage(Constants.MESSAGE_LANGUAGE);
        // messageToParkingAgent.setOntology("I guess I won't be using an ontology");
        messageToParkingAgent.setSender(getAID());
        messageToParkingAgent.setReplyWith(getAID().getName());
        messageToParkingAgent.setDefaultEnvelope();
        return messageToParkingAgent;
    }

    private class Leave extends OneShotBehaviour {
        public void action() {
            // ClientAgent is leaving parking spot and sends request to ParkingAgent to update free spaces
            ACLMessage leaveNote = prepareMessageToParkingAgent();
            leaveNote.setContent(Constants.CLIENT_IS_LEAVING_MESSAGE);
            send(leaveNote);
        }
    }
}