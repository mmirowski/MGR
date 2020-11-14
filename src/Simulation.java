import dtos.ParkingDto;
import dtos.RequestDto;
import dtos.UserDto;
import utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    public static void main(String[] args) {
        List<UserDto> appUsers = new ArrayList<>();
        List<RequestDto> usersRequests = new ArrayList<>();
        List<ParkingDto> parkings = new ArrayList<>();
        HashMap<UserDto, RequestDto> userRequestMapping = new HashMap<>();
        HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping = new HashMap<>();

        extractDataFromSurveyResponses(appUsers);
        prepareSimulationRequests(appUsers, usersRequests, userRequestMapping);
        initializeParkingsData(parkings);
        prepareClosestParkingsListForUser(usersRequests, parkings, requestClosestParkingsMapping);
        
        // for each appUser prepare a requestDto to the closest to the destination parking
//        for (UserDto user : urpMappings.) {
//
//        }
        // choose these requestDtos which are the best from the parking point of view and assign those parking spaces
        // repeat process for those users, who did not get the parking space
        // check out the results
    }

    private static void extractDataFromSurveyResponses(List<UserDto> appUsers) {
        BufferedReader bufferedReader;
        int surveyID = 1;

        try {
            FileReader fileReader = new FileReader(Constants.PATH_TO_SURVEY_RESPONSES_FILE);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                List<String> userParameters = Arrays.asList(line.split(" "));
                UserDto newUser = configureUserDto(userParameters, surveyID);
                appUsers.add(newUser);
                line = bufferedReader.readLine();
                surveyID++;
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading lines from the usersResponses.txt file.");
            e.printStackTrace();
        }
    }

    private static UserDto configureUserDto(List<String> userParameters, int surveyID) {
        return new UserDto(
                // Name parameter is negligible from the programming point of view
                "",
                surveyID,
                // Initial user coordinates are picked randomly within town map range
                ThreadLocalRandom.current().nextDouble(0.0, 100.0),
                ThreadLocalRandom.current().nextDouble(0.0, 100.0),
                // Funds are assumed to be available for each app user
                ThreadLocalRandom.current().nextDouble(50.0, 100.0),
                false,
                Double.parseDouble(userParameters.get(0)),
                Double.parseDouble(userParameters.get(1)),
                Boolean.parseBoolean(userParameters.get(2)),
                Boolean.parseBoolean(userParameters.get(3)),
                Boolean.parseBoolean(userParameters.get(4)),
                Boolean.parseBoolean(userParameters.get(5)));
    }

    private static void prepareSimulationRequests(List<UserDto> appUsers, List<RequestDto> usersRequests,
                                                  HashMap<UserDto, RequestDto> userRequestMapping) {
        for (UserDto user: appUsers) {
            RequestDto newRequest = configureRequestDto(user);
            usersRequests.add(newRequest);
            userRequestMapping.put(user, newRequest);
        }
    }

    private static RequestDto configureRequestDto(UserDto user) {
        return new RequestDto(
                user.getId(),
                // Journey destination coordinates are picked randomly within town map range
                ThreadLocalRandom.current().nextDouble(0.0, 100.0),
                ThreadLocalRandom.current().nextDouble(0.0, 100.0),
                user.getMaxDistanceFromDestination(),
                user.getMaxCost(),
                user.isNeedCoveredParking(),
                user.isNeedSecuredParking(),
                user.isNeedSpecialParking()
        );
    }

    private static void initializeParkingsData(List<ParkingDto> parkings) {
        BufferedReader bufferedReader;
        int parkingID = 1;

        try {
            FileReader fileReader = new FileReader(Constants.PATH_TO_TOWN_MAP_FILE);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                List<String> parkingParameters = Arrays.asList(line.split(" "));
                ParkingDto newParking = configureParkingDto(parkingParameters, parkingID, new Date());
                parkings.add(newParking);
                line = bufferedReader.readLine();
                parkingID++;
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading lines from the townMap.txt file.");
            e.printStackTrace();
        }
    }

    private static ParkingDto configureParkingDto(List<String> parkingParameters, int parkingID, Date timeStamp) {
        return new ParkingDto(
                parkingID,
                Double.parseDouble(parkingParameters.get(0)),
                Double.parseDouble(parkingParameters.get(1)),
                Integer.parseInt(parkingParameters.get(2)),
                Integer.parseInt(parkingParameters.get(3)),
                Double.parseDouble(parkingParameters.get(4)),
                timeStamp,
                Boolean.parseBoolean(parkingParameters.get(5)),
                Boolean.parseBoolean(parkingParameters.get(6)),
                Boolean.parseBoolean(parkingParameters.get(7))
                );
    }

    private static void prepareClosestParkingsListForUser(List<RequestDto> usersRequests, List<ParkingDto> parkings, HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping) {
        for (RequestDto request : usersRequests) {
            List<ParkingDto> sortedParkings = configureParkingDistanceMapping(request, parkings);
            requestClosestParkingsMapping.put(request, sortedParkings);
        }
    }

    private static List<ParkingDto> configureParkingDistanceMapping(RequestDto request, List<ParkingDto> allParkings) {
        HashMap<ParkingDto, Double> parkingDistanceMapping = new HashMap<>();

        for (ParkingDto parking : allParkings) {
            Double distanceToParking = calculateDistance(request, parking);
            parkingDistanceMapping.put(parking, distanceToParking);
        }

        return sortParkingsFromClosestToFarthest(parkingDistanceMapping);
    }

    private static double calculateDistance(RequestDto request, ParkingDto parking) {
        // For a given request, calculate distance to a specific parking
        return (Math.abs(request.getDestinationXCoordinate() - parking.getParkingXCoordinate()) +
                Math.abs(request.getDestinationYCoordinate() - parking.getParkingYCoordinate()));
    }

    private static List<ParkingDto> sortParkingsFromClosestToFarthest(HashMap<ParkingDto, Double> parkingDistanceMapping) {
        List<ParkingDto> sortedParkings = new ArrayList<>();
        List<Double> values = new ArrayList<>(parkingDistanceMapping.values());
        Collections.sort(values);

        for (Double val : values) {
            for (Map.Entry<ParkingDto, Double> entry : parkingDistanceMapping.entrySet()) {
                if (entry.getValue().equals(val)) {
                    ParkingDto closestParking = entry.getKey();
                    sortedParkings.add(closestParking);
                }
            }
        }

        return sortedParkings;
    }
}
