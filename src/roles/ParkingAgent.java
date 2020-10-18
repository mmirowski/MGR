package roles;

import dtos.ParkingDto;
import dtos.RequestDto;
import genericBehaviours.ReportBug;
import genericBehaviours.TerminateAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParkingAgent extends Agent {

    private ParkingDto parkingDto;
    private List<RequestDto> requestsList = new LinkedList<>();

    protected void setup() {
        AID parkingAgentID = getAID();
        registerWithinDF(parkingAgentID);
        System.out.println("Agent " + parkingAgentID.getName() + " is ready to work.");
        setBehavioursQueue();
    }

    private void registerWithinDF(AID parkingAgentID) {
        // To register an Agent in the Directory Facilitator, Service Description is performed
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(Constants.PARKING_AGENT_CLASS_TYPE);
        serviceDescription.setName(Constants.PARKING_AGENT_NICKNAME);

        DFAgentDescription dfParkingAgentDescription = new DFAgentDescription();
        dfParkingAgentDescription.setName(parkingAgentID);
        dfParkingAgentDescription.addServices(serviceDescription);

        // Try performing registering action
        try {
            DFService.register(this, dfParkingAgentDescription);
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    private void setBehavioursQueue() {
        addBehaviour(new InitializeParkingData());
        addBehaviour(new PresentParkingParameters());
        addBehaviour(new ObtainRequests());

        // Requests' handling
        if (requestsList != null && checkIfAuctionIsNeeded()) {
            addBehaviour(new ExchangeDataWithBank());
            addBehaviour(new ObtainAuctionResults());
        }

        // Inside the SendOffers behaviour ParkingAgent should pass also his AID - to make further communication
        // possible for the ClientAgent
        addBehaviour(new SendOffers());
        // update freeSpaces
        // check Clients positions and sensors data
        addBehaviour(new ConfirmClientIsStaying());
        addBehaviour(new ConfirmClientIsLeaving());
        addBehaviour(new ReportBug());
        addBehaviour(new TerminateAgent());
    }

    private boolean checkIfAuctionIsNeeded() {
        return requestsList.size() > parkingDto.getFreeSpaces();
    }

    private class InitializeParkingData extends OneShotBehaviour {
        public void action() {
            Object[] arguments = getArguments();

            if (arguments != null && arguments.length > 0) {
                parkingDto.setParkingXCoordinate(Double.parseDouble(arguments[0].toString()));
                parkingDto.setParkingYCoordinate(Double.parseDouble(arguments[1].toString()));
                parkingDto.setMaxCapacity(Integer.parseInt(arguments[2].toString()));
                parkingDto.setFreeSpaces(Integer.parseInt(arguments[3].toString()));
                Date date = new Date();
                parkingDto.setFreeSpacesLastUpdate(date);
                parkingDto.setCovered(Boolean.parseBoolean(arguments[4].toString()));
                parkingDto.setSecured(Boolean.parseBoolean(arguments[5].toString()));
                parkingDto.setSpecial(Boolean.parseBoolean(arguments[6].toString()));
            }
        }
    }

    private class PresentParkingParameters extends OneShotBehaviour {
        public void action() {
            System.out.println("Parking Agent's local name is: " + getAID().getLocalName());
            System.out.println("Agent's addresses are: " + String.join(", ", getAID().getAddressesArray()));
            System.out.println("Agent's coordinates: (" + parkingDto.getParkingXCoordinate() + ";"
                    + parkingDto.getParkingYCoordinate() + ")");
            System.out.println("Parking maximum capacity: " + parkingDto.getMaxCapacity());
            System.out.println("Parking free spaces: " + parkingDto.getFreeSpaces());
            String parkingCharacteristics = getParkingCharacteristics();
            System.out.println("Parking characteristics: " + parkingCharacteristics);
        }
    }

    private String getParkingCharacteristics() {
        ArrayList<String> parkingCharacteristics = new ArrayList<>();

        if (parkingDto.isCovered()) {
            parkingCharacteristics.add("covered");
        }

        if (parkingDto.isSecured()) {
            parkingCharacteristics.add("secured");
        }

        if (parkingDto.isSpecial()) {
            parkingCharacteristics.add("special");
        }

        String result = String.join(", ", parkingCharacteristics);

        if (!result.isEmpty()) {
            return result;
        }

        return "parking has no special attributes";
    }


    private class ObtainRequests extends CyclicBehaviour {
        public void action() {
            // ParkingAgent listening for calls for proposals from ClientAgents
            MessageTemplate clientCFPTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
                    MessageTemplate.MatchProtocol(Constants.REQUEST_FOR_PARKING_SPACE));
            ACLMessage clientCFP = receive(clientCFPTemplate);

            if (clientCFP != null) {
                try {
                    requestsList.add((RequestDto) clientCFP.getContentObject());
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }

        }
    }
}