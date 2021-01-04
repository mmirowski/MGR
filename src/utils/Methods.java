package utils;

import dtos.ParkingDto;
import dtos.RequestDto;
import dtos.UserDto;
import dtos.ValuationMechanismDto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Methods {
    public static List<UserDto> extractUsersDataFromSurveyResponses() {
        List<UserDto> appUsers = new ArrayList<>();
        int surveyID = 1;

        try {
            FileReader fileReader = new FileReader(Constants.PATH_TO_SURVEY_RESPONSES_FILE);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
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
            System.out.println("An error occurred while reading and processing lines from the usersResponses.txt file.");
            e.printStackTrace();
        }

        return appUsers;
    }

    private static UserDto configureUserDto(List<String> userParameters, int surveyID) {
        return new UserDto(
                // Name parameter is negligible from the programming point of view
                "",
                surveyID,
                // Initial user coordinates are picked randomly within town map range
                ThreadLocalRandom.current().nextDouble(0.0, Constants.MAP_COORDINATES_BOUNDARY),
                ThreadLocalRandom.current().nextDouble(0.0, Constants.MAP_COORDINATES_BOUNDARY),
                // Funds are assumed to be available for each app user
                ThreadLocalRandom.current().nextDouble(Constants.USER_FUNDS_LOWER_BOUND, Constants.USER_FUNDS_UPPER_BOUND),
                false,
                Double.parseDouble(userParameters.get(0)) * Constants.SCALE_MODIFIER,
                Double.parseDouble(userParameters.get(1)),
                Boolean.parseBoolean(userParameters.get(2)),
                Boolean.parseBoolean(userParameters.get(3)),
                Boolean.parseBoolean(userParameters.get(4)),
                Boolean.parseBoolean(userParameters.get(5)));
    }

    public static List<ParkingDto> initializeParkingsData() {
        List<ParkingDto> parkings = new ArrayList<>();
        BufferedReader bufferedReader;
        int parkingID = 1;

        try {
            FileReader fileReader = new FileReader(Constants.PATH_TO_TOWN_MAP_FILE);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                List<String> parkingCoordinates = Arrays.asList(line.split(" "));
                ParkingDto newParking = configureParkingDto(parkingCoordinates, parkingID, new Date());
                parkings.add(newParking);
                line = bufferedReader.readLine();
                parkingID++;
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading and processing lines from the townMap.txt file.");
            e.printStackTrace();
        }

        return parkings;
    }

    private static ParkingDto configureParkingDto(List<String> parkingCoordinates, int parkingID, Date timeStamp) {
        ValuationMechanismDto valuationMechanismDto = configureValuationMechanismDto();

        return ParkingDto.builder()
                .id(parkingID)
                .parkingXCoordinate(Double.parseDouble(parkingCoordinates.get(0)))
                .parkingYCoordinate(Double.parseDouble(parkingCoordinates.get(1)))
                .maxCapacity(valuationMechanismDto.getMaxCapacity())
                .freeSpaces(valuationMechanismDto.getFreeSpaces())
                .cost(getCostBasedOnValuationMechanism(Constants.CHOSEN_VALUATION_MECHANISM_ID, valuationMechanismDto))
                .freeSpacesLastUpdate(timeStamp)
                .isCovered(valuationMechanismDto.isCovered())
                .isSecured(valuationMechanismDto.isSecured())
                .isSpecial(valuationMechanismDto.isSpecial())
                .offers(new ArrayList<>())
                .build();
    }

    private static ValuationMechanismDto configureValuationMechanismDto() {
        int maxCapacity = getLinearRandomNumber(Constants.PARKING_SPOT_BOUND_EXCLUSIVE);
        int freeSpaces = (int) Math.floor(Constants.FREE_SPACES_NUMBER_COEFFICIENT * maxCapacity);
        boolean isCovered = ThreadLocalRandom.current().nextBoolean();
        boolean isSecured = ThreadLocalRandom.current().nextBoolean();
        boolean isSpecial = ThreadLocalRandom.current().nextBoolean();

        return ValuationMechanismDto.builder()
                .maxCapacity(maxCapacity)
                .freeSpaces(freeSpaces)
                .isCovered(isCovered)
                .isSecured(isSecured)
                .isSpecial(isSpecial)
                .build();
    }

    public static int getLinearRandomNumber(int maxSize) {
        // Get a linearly multiplied random number
        int randomMultiplier = maxSize * (maxSize + 1) / 2;
        Random r = new Random();
        int randomInt = r.nextInt(randomMultiplier);

        // Linearly iterate through the possible values to find the correct one
        int linearRandomNumber = 0;
        for (int i = maxSize; randomInt >= 0; i--) {
            randomInt -= i;
            linearRandomNumber++;
        }

        return linearRandomNumber;
    }

    private static double getCostBasedOnValuationMechanism(int chosenValuationMechanismId, ValuationMechanismDto VMDto) {
        double basePrice = ThreadLocalRandom.current()
                .nextDouble(Constants.LOWER_BASE_PRICE_LIMIT, Constants.UPPER_BASE_PRICE_LIMIT);
        double coveredCoefficient = VMDto.isCovered() ? ThreadLocalRandom.current()
                .nextDouble(Constants.LOWER_COVERED_PRICE_LIMIT, Constants.UPPER_COVERED_PRICE_LIMIT) : 0;
        double securedCoefficient = VMDto.isSecured() ? ThreadLocalRandom.current()
                .nextDouble(Constants.LOWER_SECURED_PRICE_LIMIT, Constants.UPPER_SECURED_PRICE_LIMIT) : 0;
        double specialCoefficient = VMDto.isSpecial() ? ThreadLocalRandom.current()
                .nextDouble(Constants.LOWER_SPECIAL_PRICE_LIMIT, Constants.UPPER_SPECIAL_PRICE_LIMIT) : 0;

        double freeSpacesCoefficient = (VMDto.getMaxCapacity() / (double) VMDto.getFreeSpaces()) *
                Constants.FREE_SPACES_PRICE_COEFFICIENT;
        double parkingCharacteristicsCoefficient = coveredCoefficient + securedCoefficient + specialCoefficient;

        double finalPrice = basePrice;

        switch (chosenValuationMechanismId) {
            case 1:
                break;
            case 2:
                finalPrice += freeSpacesCoefficient;
                break;
            case 3:
                finalPrice += freeSpacesCoefficient + parkingCharacteristicsCoefficient;
                break;
        }

        return finalPrice;
    }

    public static RequestDto configureRequestDto(UserDto user) {
        return new RequestDto(
                user.getId(),
                // Journey destination coordinates are picked randomly within town map range
                ThreadLocalRandom.current().nextDouble(0.0, Constants.MAP_COORDINATES_BOUNDARY),
                ThreadLocalRandom.current().nextDouble(0.0, Constants.MAP_COORDINATES_BOUNDARY),
                user.getMaxDistanceFromDestination(),
                user.getMaxCost(),
                user.isNeedCoveredParking(),
                user.isNeedSecuredParking(),
                user.isNeedSpecialParking(),
                user.isWillingToPayExtra(),
                false
        );
    }


    public static void printIterationInformation(int algorithmIteration, List<UserDto> appUsers, List<ParkingDto> parkings, int allParkingSpaces) {
        int validOffers = checkIfOffersNumberMatch(parkings);
        int usersWithParkingLots = countSatisfiedUsers(appUsers);
        int freeParkingSpaces = countAllFreeParkingSpaces(parkings);
        double happinessPercentage = countHappinnessPercentage(usersWithParkingLots, appUsers);
        double takenParkingSpacesPercentage = countTakenParkingSpacesPercentage(usersWithParkingLots, allParkingSpaces);

        System.out.println("Iteration number: " + algorithmIteration);
        System.out.println("Iteration starts with: " + freeParkingSpaces + " free parking spaces.");
        System.out.println("Number of valid offers: " + validOffers);
        System.out.println("Reserved parking spaces percentage: " + takenParkingSpacesPercentage + "%");
        System.out.println("Satisfied users number: " + usersWithParkingLots + " (" + happinessPercentage + "%)" + "\n");
    }

    // ToDo#k Check this method
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
    private static int countAllFreeParkingSpaces(List<ParkingDto> parkings) {
        int freeParkingSpaces = 0;
        for (ParkingDto p : parkings) {
            freeParkingSpaces += p.getFreeSpaces();
        }

        return freeParkingSpaces;
    }

    private static double countHappinnessPercentage(int usersWithParkingLots, List<UserDto> appUsers) {
        int numberOfUsers = appUsers.size();
        double percentage = (double) usersWithParkingLots / (double) numberOfUsers * 100;
        return (double) Math.round(percentage * 100) / 100;
    }

    private static double countTakenParkingSpacesPercentage(int usersWithParkingLots, int freeParkingSpaces) {
        double percentage = (double) usersWithParkingLots / (double) freeParkingSpaces * 100;
        return  (double) Math.round(percentage * 100) / 100;
    }
}
