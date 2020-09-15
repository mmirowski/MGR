package messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class CheckParkingLotsAvailabilityContent implements Serializable {

    private boolean available;

}
