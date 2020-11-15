package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Comparator;

@Getter
@Setter
@AllArgsConstructor
public class RequestDto implements Serializable {

//    private AID client;
    private int id;
    private double destinationXCoordinate;
    private double destinationYCoordinate;
    private double maxDistanceFromDestination;
    private double maxCost;
    private boolean needCoveredParking;
    private boolean needSecuredParking;
    private boolean needSpecialParking;
    private boolean isDone;

    public static class sortByCost implements Comparator<RequestDto> {
        public int compare(RequestDto r1, RequestDto r2) {
            return (int) (r1.getMaxCost() - r2.getMaxCost());
        }
    }

}