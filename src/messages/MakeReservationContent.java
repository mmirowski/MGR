package messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class MakeReservationContent implements Serializable {

    private boolean reservationStatus;

}
