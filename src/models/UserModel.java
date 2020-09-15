package models;

import jade.core.AID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModel {

    private AID name;
    private int userXCoordinate;
    private int userYCoordinate;
    private int destinationXCoordinate;
    private int destinationYCoordinate;
    private int money;
    private boolean hasReservedParkingSpot;
    private UserPreferencesModel userPreferencesModel;

}