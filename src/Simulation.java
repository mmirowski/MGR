import dtos.ParkingDto;
import dtos.RequestDto;
import dtos.UserDto;
import utils.Constants;
import utils.Methods;

import java.util.*;

public class Simulation {
    public static void main(String[] args) {
        int algorithmIteration = 0;

        List<RequestDto> usersRequests = new ArrayList<>();

        HashMap<UserDto, RequestDto> userRequestMapping = new HashMap<>();
        HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping = new HashMap<>();

        List<UserDto> appUsers = Methods.extractUsersDataFromSurveyResponses();
        List<ParkingDto> parkings = Methods.initializeParkingsData();

        do {
            Methods.printIterationInformation(algorithmIteration, appUsers, parkings);
            prepareSimulationRequests(appUsers, usersRequests, userRequestMapping);
            prepareClosestParkingsListForUser(usersRequests, parkings, requestClosestParkingsMapping);
            sendRequestsToParkings(requestClosestParkingsMapping, algorithmIteration);
            orderOffersFromBest(parkings);
            chooseBestOffers(parkings, appUsers);

            algorithmIteration++;
            // Repeat process for those users, who did not get the parking space
        } while (isStopConditionMet(requestClosestParkingsMapping, appUsers, algorithmIteration));
    }

    private static void prepareSimulationRequests(List<UserDto> appUsers, List<RequestDto> allUsersRequests,
                                                  HashMap<UserDto, RequestDto> userRequestMapping) {
        for (UserDto user : appUsers) {
            if (!user.isHasReservedParkingSpot()) {
                RequestDto newRequest = Methods.configureRequestDto(user);
                allUsersRequests.add(newRequest);
                userRequestMapping.put(user, newRequest);
            }
        }
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
        double distance = calculateDistance(request, parking);
        double cost = parking.getCost();

        int isCovered = parking.isCovered() ? 1 : 0;
        int isSecured = parking.isSecured() ? 1 : 0;
        int isSpecial = parking.isSpecial() ? 1 : 0;
        double acceptableDistance = request.getMaxDistanceFromDestination();
        double acceptableCost = request.getMaxCost();

        boolean isExtraCostConditionMet = request.isWillingToPayExtra() &&
                        Math.abs(distance - acceptableDistance) < Constants.CLOSE_DESTINATION_MODIFIER;

        return parking.getFreeSpaces() > 0 &&
                coveredReq <= isCovered &&
                securedReq <= isSecured &&
                specialReq <= isSpecial &&
                distance <= acceptableDistance &&
                (cost <= acceptableCost || isExtraCostConditionMet);
    }

    private static double calculateDistance(RequestDto request, ParkingDto parking) {
        // For a given request, calculate distance to a specific parking
        return (Math.abs(request.getDestinationXCoordinate() - parking.getParkingXCoordinate()) +
                Math.abs(request.getDestinationYCoordinate() - parking.getParkingYCoordinate()));
    }

    private static List<ParkingDto> configureParkingDistanceMapping(RequestDto request, List<ParkingDto> acceptedParkings) {
        HashMap<ParkingDto, Double> parkingDistanceMapping = new HashMap<>();

        for (ParkingDto p : acceptedParkings) {
            Double distanceToParking = calculateDistance(request, p);
            parkingDistanceMapping.put(p, distanceToParking);
        }

        return sortParkingsFromClosestToFarthest(parkingDistanceMapping);
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

            setCurrentlyConsideredEntry(algorithmIteration, iterator);

            if (iterator.hasNext()) {
                ParkingDto highestPriorityParking = iterator.next();
                List<RequestDto> existingOffers = highestPriorityParking.getOffers();
                existingOffers.add(key);
                highestPriorityParking.setOffers(existingOffers);
            }
        }
    }

    /**
     * setCurrentlyConsideredEntry method checks which iteration is now run and designates the iterator to the
     * matching algorithmIteration next() item.
     * Every RequestDto -- ClosestParkingDto mapping has (or could have) several ParkingDtos as the values.
     * During the algorithmIteration == 0 every RequestDto should be sent to its 'best' (closest) parking mapped.
     * For a single RequestDto every next iteration should handle next ParkingDto from the mapped list, and that
     * is because: if RequestDto A is considered in that iteration, therefore it didn't get the place in the
     * highest-listed ParkingDto B. In that case, there is no sense in sending same RequestDto A to the ParkingDto B;
     * it should instead be sent to the next on the list, ParkingDto C. Parameters taken by the method are:
     * @param algorithmIteration is the index of currently run iteration,
     * @param iterator is made of the requestClosestParkingsMapping.get(key) and contains ParkingDtos ordered from the
     *                 closest (best) to the farthest (worst) one.
     */
    private static void setCurrentlyConsideredEntry(int algorithmIteration, Iterator<ParkingDto> iterator) {
        for (int i = 0; i < algorithmIteration; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }
    }

    private static void orderOffersFromBest(List<ParkingDto> parkings) {
        for (ParkingDto parking : parkings) {
            List<RequestDto> submittedOffers = parking.getOffers();
            submittedOffers.sort(new RequestDto.sortByCost());
            Collections.reverse(submittedOffers);
        }
    }

    // Choose these requests, which are the best from the parking point of view - and assign parking spaces

    private static void chooseBestOffers(List<ParkingDto> parkings, List<UserDto> appUsers) {
        for (ParkingDto p : parkings) {
            int lotsAvailable = p.getFreeSpaces();
            int minFromLotsAndOffersNumbers = Math.min(lotsAvailable, p.getOffers().size());
            List<RequestDto> bestOffers = p.getOffers().subList(0, minFromLotsAndOffersNumbers);

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
                    p.setFreeSpaces(--lotsAvailable);
                    p.setFreeSpacesLastUpdate(new Date());
                    user.setHasReservedParkingSpot(true);
                    request.setDone(true);
                }
            }

            p.setOffers(p.getOffers().subList(minFromLotsAndOffersNumbers, p.getOffers().size()));
        }
    }

    private static boolean isStopConditionMet(HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping,
                                              List<UserDto> appUsers, int algorithmItertion) {

        boolean areFreeSpacesAvailable = areThereFreeSpacesAvailable(requestClosestParkingsMapping);
        boolean areUsersLookingForAParking = areThereUsersLookingForAParking(requestClosestParkingsMapping, appUsers);

        return areFreeSpacesAvailable && areUsersLookingForAParking && algorithmItertion < Constants.FINAL_ITERATION;
    }

    private static boolean areThereFreeSpacesAvailable(HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping) {
        boolean areFreeSpacesAvailable = false;

        for (RequestDto key : requestClosestParkingsMapping.keySet()) {
            List<ParkingDto> acceptableParkingsLeft = requestClosestParkingsMapping.get(key);
            for (ParkingDto parking : acceptableParkingsLeft) {
                if (parking.getFreeSpaces() > 0) {
                    areFreeSpacesAvailable = true;
                    break;
                }
            }
        }

        return areFreeSpacesAvailable;
    }

    private static boolean areThereUsersLookingForAParking(HashMap<RequestDto, List<ParkingDto>> requestClosestParkingsMapping, List<UserDto> appUsers) {
        boolean areUsersLookingForAParking = false;

        for (RequestDto key : requestClosestParkingsMapping.keySet()) {
            List<ParkingDto> acceptableParkingsLeft = requestClosestParkingsMapping.get(key);
            for (ParkingDto parking : acceptableParkingsLeft) {
                for (UserDto user : appUsers) {
                    if (!user.isHasReservedParkingSpot() && user.getMoney() > parking.getCost()) {
                        areUsersLookingForAParking = true;
                        break;
                    }
                }
            }
        }

        return areUsersLookingForAParking;
    }

}
