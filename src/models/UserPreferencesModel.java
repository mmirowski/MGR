package models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPreferencesModel {

    private int maxDistanceFromDestination;
    private boolean needCoveredParking;
    private boolean needSecuredParking;
    private boolean needSpecialParking;

}