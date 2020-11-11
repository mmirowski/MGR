package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private String name;
    private int id;
    private double userXCoordinate;
    private double userYCoordinate;
    private double money;
    private boolean hasReservedParkingSpot;

    private double maxDistanceFromDestination;
    private double maxCost;
    private boolean needCoveredParking;
    private boolean needSecuredParking;
    private boolean needSpecialParking;
    private boolean isWillingToPayExtra;

}