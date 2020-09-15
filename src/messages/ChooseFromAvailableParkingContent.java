package messages;

import lombok.AllArgsConstructor;
import models.ParkingModel;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
public class ChooseFromAvailableParkingContent implements Serializable {

    private List<ParkingModel> availableParkings;

    public void addParking(ParkingModel parkingModel) {
        availableParkings.add(parkingModel);
    }

    public void removeParking(ParkingModel parkingModel) {
        int parkingModelIndex = availableParkings.indexOf(parkingModel);
        availableParkings.remove(parkingModelIndex);
    }

}
