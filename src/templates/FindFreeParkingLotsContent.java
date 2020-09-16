package templates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class FindFreeParkingLotsContent implements Serializable {

    private int driverXCoordinate;
    private int driverYCoordinate;

}
