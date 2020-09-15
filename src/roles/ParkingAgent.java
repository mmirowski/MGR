package roles;

import dtos.ParkingDto;
import dtos.RequestDto;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    // ToDo#2 Ask, why does initialization by GUI gives the System.out.println'es -
    //  and initialization on Main does not?
    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready to work.");
        setBehavioursQueue();
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

        addBehaviour(new SendOffers());
        // update freeSpaces
        // check Clients positions and sensors data
        addBehaviour(new ConfirmClientIsStaying());
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
            System.out.println("Sth");
            // doSomething
            // wait for the request from teh Client
        }
        // done() always returns false in CyclicBehaviours - have to use block()
    }

    private class TerminateAgent extends OneShotBehaviour {
        public void action() {
            doDelete();
        }
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " has done his work and is going to be turned off.");
    }
}