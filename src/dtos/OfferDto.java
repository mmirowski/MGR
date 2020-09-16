package dtos;

import jade.core.AID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OfferDto {

    private AID client;
    private double parkingXCoordinate;
    private double parkingYCoordinate;
    private double distanceFromDestination;
    private double price;

}