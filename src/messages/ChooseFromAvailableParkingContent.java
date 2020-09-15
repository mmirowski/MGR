package messages;

import lombok.AllArgsConstructor;
import dtos.ParkingDto;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
public class ChooseFromAvailableParkingContent implements Serializable {

    private List<ParkingDto> availableParkings;

    public void addParking(ParkingDto parkingDto) {
        availableParkings.add(parkingDto);
    }

    public void removeParking(ParkingDto parkingDto) {
        int parkingModelIndex = availableParkings.indexOf(parkingDto);
        availableParkings.remove(parkingModelIndex);
    }

}
