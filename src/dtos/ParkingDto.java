package dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class ParkingDto {

    private int id;
    private double parkingXCoordinate;
    private double parkingYCoordinate;
    private int maxCapacity;
    private int freeSpaces;
    private double cost;
    private Date freeSpacesLastUpdate;
    private boolean isCovered;
    private boolean isSecured;
    private boolean isSpecial;

    private List<RequestDto> offers;

}