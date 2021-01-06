package dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class IterationInformationDto {
    private int algorithmIteration;
    private int allParkingSpaces;
    private List<UserDto> appUsers;
    private List<ParkingDto> parkings;

    private double satisfiedUsersPercentage;
    private double reservedParkingSpacesPercentage;
    private double averageParkingSpacePrice;
    private double exactlyMetRequirementsPercentage;

}

