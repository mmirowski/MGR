package templates;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class CheckParkingPositionContent implements Serializable {

    private int parkingXCoordinate;
    private int parkingYCoordinate;

}
