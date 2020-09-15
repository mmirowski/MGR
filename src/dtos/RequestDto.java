package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestDto {

    private double destinationXCoordinate;
    private double destinationYCoordinate;
    private double maxDistanceFromDestination;
    private double maxCost;
    private boolean needCoveredParking;
    private boolean needSecuredParking;
    private boolean needSpecialParking;

}