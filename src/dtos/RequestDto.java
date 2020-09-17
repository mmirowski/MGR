package dtos;

import jade.core.AID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class RequestDto implements Serializable {

    private AID client;
    private double destinationXCoordinate;
    private double destinationYCoordinate;
    private double maxDistanceFromDestination;
    private double maxCost;
    private boolean needCoveredParking;
    private boolean needSecuredParking;
    private boolean needSpecialParking;

}