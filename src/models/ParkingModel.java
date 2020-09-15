package models;

import jade.core.AID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ParkingModel {

    final private int MAX_CAPACITY;

    private AID name;
    private int parkingXCoordinate;
    private int parkingYCoordinate;
    private Date freeSpacesLastUpdate;
    private boolean covered;
    private boolean secured;
    private boolean special;

    @Setter(AccessLevel.NONE)
    private int freeSpaces;

    public void setFreeSpaces(int freeSpaces) {
        this.freeSpaces = freeSpaces;
        this.freeSpacesLastUpdate = new Date();
    }
}