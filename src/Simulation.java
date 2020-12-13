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
        int algorithmIteration = 0;
        int usersWithParkingLots = 0;
        List<UserDto> appUsers = new ArrayList<>();
        List<RequestDto> usersRequests = new ArrayList<>();
        List<ParkingDto> parkings = new ArrayList<>();
        HashMap<UserDto, RequestDto> userRequestMapping = new HashMap<>();
        HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping = new HashMap<>();

        extractDataFromSurveyResponses(appUsers);
        initializeParkingsData(parkings);

        do {
            int validOffers = 0;
            prepareSimulationRequests(appUsers, usersRequests, userRequestMapping);
            prepareClosestParkingsListForUser(usersRequests, parkings, requestClosestParkingsMapping);
            sendRequestsToParkings(requestClosestParkingsMapping, algorithmIteration);
            orderOffersFromBest(parkings);
            chooseBestOffers(parkings, appUsers);

            validOffers = checkIfOffersNumberMatch(parkings);
            usersWithParkingLots = countSatisfiedUsers(appUsers);
            System.out.println(validOffers);
            System.out.println("Satisfied users number: " + usersWithParkingLots);
            algorithmIteration++;
            // Repeat process for those users, who did not get the parking space
        } while (isStopConditionMet(requestClosestParkingsMapping, appUsers, algorithmIteration));
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

            Collections.shuffle(appUsers);

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
                ThreadLocalRandom.current().nextDouble(0.0, Constants.BOUND),
                ThreadLocalRandom.current().nextDouble(0.0, Constants.BOUND),
                // Funds are assumed to be available for each app user
                ThreadLocalRandom.current().nextDouble(50.0, 100.0),
                false,
                Double.parseDouble(userParameters.get(0)) * Constants.SCALE_MODIFIER,
                Double.parseDouble(userParameters.get(1)),
                Boolean.parseBoolean(userParameters.get(2)),
                Boolean.parseBoolean(userParameters.get(3)),
                Boolean.parseBoolean(userParameters.get(4)),
                Boolean.parseBoolean(userParameters.get(5)));
    }

    private static void prepareSimulationRequests(List<UserDto> appUsers, List<RequestDto> usersRequests,
                                                  HashMap<UserDto, RequestDto> userRequestMapping) {
        for (UserDto user : appUsers) {
            if (!user.isHasReservedParkingSpot()) {
                RequestDto newRequest = configureRequestDto(user);
                usersRequests.add(newRequest);
                userRequestMapping.put(user, newRequest);
            }
        }
    }

    private static RequestDto configureRequestDto(UserDto user) {
        return new RequestDto(
                user.getId(),
                // Journey destination coordinates are picked randomly within town map range
                ThreadLocalRandom.current().nextDouble(0.0, Constants.BOUND),
                ThreadLocalRandom.current().nextDouble(0.0, Constants.BOUND),
                user.getMaxDistanceFromDestination(),
                user.getMaxCost(),
                user.isNeedCoveredParking(),
                user.isNeedSecuredParking(),
                user.isNeedSpecialParking(),
                false
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
                Boolean.parseBoolean(parkingParameters.get(7)),
                new ArrayList<>()
        );
    }

    private static void prepareClosestParkingsListForUser(List<RequestDto> usersRequests, List<ParkingDto> parkings,
                                                          HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping) {
        for (RequestDto request : usersRequests) {
            if (!request.isDone()) {
                List<ParkingDto> acceptableParkings = selectAcceptableParkings(request, parkings);
                List<ParkingDto> sortedParkings = configureParkingDistanceMapping(request, acceptableParkings);
                requestClosestParkingsMapping.put(request, sortedParkings);
            }
        }
    }

    private static List<ParkingDto> selectAcceptableParkings(RequestDto request, List<ParkingDto> allParkings) {
        List<ParkingDto> localParkings = new ArrayList<>(allParkings);
        localParkings.removeIf(parking -> !checkIfParkingIsAcceptable(request, parking));
        return localParkings;
    }

    private static boolean checkIfParkingIsAcceptable(RequestDto request, ParkingDto parking) {
        int coveredReq = request.isNeedCoveredParking() ? 1 : 0;
        int securedReq = request.isNeedSecuredParking() ? 1 : 0;
        int specialReq = request.isNeedSpecialParking() ? 1 : 0;

        int isCovered = parking.isCovered() ? 1 : 0;
        int isSecured = parking.isSecured() ? 1 : 0;
        int isSpecial = parking.isSpecial() ? 1 : 0;

        double distance = calculateDistance(request, parking);
        double acceptableDistance = request.getMaxDistanceFromDestination();

        return coveredReq <= isCovered && securedReq <= isSecured && specialReq <= isSpecial && distance <= acceptableDistance;
    }

    private static List<ParkingDto> configureParkingDistanceMapping(RequestDto request, List<ParkingDto> allParkings) {
        HashMap<ParkingDto, Double> parkingDistanceMapping = new HashMap<>();

        for (ParkingDto parking : allParkings) {
            if (parking.getFreeSpaces() > 0) {
                Double distanceToParking = calculateDistance(request, parking);
                parkingDistanceMapping.put(parking, distanceToParking);
            }
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
                    parkingDistanceMapping.remove(closestParking);
                    break;
                }
            }
        }

        return sortedParkings;
    }

    private static void sendRequestsToParkings(HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping,
                                               int algorithmIteration) {

        for (RequestDto key : requestClosestParkingsMapping.keySet()) {
            Iterator<ParkingDto> iterator = requestClosestParkingsMapping.get(key).iterator();

            for (int i = 0; i < algorithmIteration; i++) {
                if (iterator.hasNext()) {
                    iterator.next();
                }
            }

            if (iterator.hasNext()) {
                ParkingDto highestPriorityParking = iterator.next();
                List<RequestDto> existingOffers = highestPriorityParking.getOffers();
                existingOffers.add(key);
                highestPriorityParking.setOffers(existingOffers);
            }
        }
    }

    // Choose these requests, which are the best from the parking point of view - and assign parking spaces

    private static void chooseBestOffers(List<ParkingDto> parkings, List<UserDto> appUsers) {
        for (ParkingDto parking : parkings) {
            int lotsAvailable = parking.getFreeSpaces();
            int index = Math.min(lotsAvailable, parking.getOffers().size());
            List<RequestDto> bestOffers = parking.getOffers().subList(0, index);

            for (RequestDto request : bestOffers) {
                UserDto user = appUsers
                        .stream()
                        .filter(obj -> obj.getId() == request.getId())
                        .findAny()
                        .orElseGet(null);

                if (user == null) {
                    System.out.println("One of the requests didn't match any user - debug chooseBestOffers method!");
                    break;
                }

                double diff = user.getMoney() - request.getMaxCost();

                if (diff >= 0) {
                    user.setMoney(diff);
                    lotsAvailable--;
                    parking.setFreeSpaces(lotsAvailable);
                    parking.setFreeSpacesLastUpdate(new Date());
                    user.setHasReservedParkingSpot(true);
                }
            }

            parking.getOffers().subList(0, index).clear();
        }
    }

    private static void orderOffersFromBest(List<ParkingDto> parkings) {
        for (ParkingDto parking : parkings) {
            List<RequestDto> submittedOffers = parking.getOffers();
            submittedOffers.sort(new RequestDto.sortByCost());
            Collections.reverse(submittedOffers);
        }
    }

    private static int checkIfOffersNumberMatch(List<ParkingDto> parkings) {
        int offersNumber = 0;

        for (ParkingDto parking : parkings) {
            offersNumber += parking.getOffers().size();
        }

        return offersNumber;
    }

    private static int countSatisfiedUsers(List<UserDto> appUsers) {
        int satisfied = 0;

        for (UserDto user : appUsers) {
            if (user.isHasReservedParkingSpot()) {
                satisfied++;
            }
        }
        return satisfied;
    }

    private static boolean isStopConditionMet(HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping,
                                              List<UserDto> appUsers, int algorithmIteration) {
        boolean areFreeSpacesAvailable = false;
        boolean areUsersLookingForAParking = false;
        boolean doUsersHaveFunds = false;

        for (RequestDto key : requestClosestParkingsMapping.keySet()) {
            List<ParkingDto> acceptableParkingsLeft = requestClosestParkingsMapping.get(key);
            for (ParkingDto parking : acceptableParkingsLeft) {
                if (parking.getFreeSpaces() > 0) {
                    areFreeSpacesAvailable = true;
                    break;
                }
            }
        }

        for (RequestDto key : requestClosestParkingsMapping.keySet()) {
            List<ParkingDto> acceptableParkingsLeft = requestClosestParkingsMapping.get(key);
            for (ParkingDto parking : acceptableParkingsLeft) {
                for (UserDto user : appUsers) {
                    if (user.getMoney() > parking.getCost()) {
                        doUsersHaveFunds = true;
                        break;
                    }
                }
            }
        }

        for (UserDto user : appUsers) {
            if (!user.isHasReservedParkingSpot()) {
                areUsersLookingForAParking = true;
                break;
            }
        }

        return areFreeSpacesAvailable && areUsersLookingForAParking && doUsersHaveFunds && algorithmIteration < 5;
    }
}
