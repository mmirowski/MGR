package dtos;

import jade.core.AID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String name;
    private double userXCoordinate;
    private double userYCoordinate;
    private double money;
    private boolean hasReservedParkingSpot;

}