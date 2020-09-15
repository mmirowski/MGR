package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class OfferDto {

    private double parkingXCoordinate;
    private double parkingYCoordinate;
    private double distanceFromDestination;
    private double price;

}