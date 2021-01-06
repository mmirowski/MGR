package utils;

import dtos.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Methods {
    public static void printPreamble() {
        System.out.println("=== Multi-agent parking spot geolocalization system run simulation ===");
        System.out.println("=== Valuation mechanism chosen: MW" + Constants.CHOSEN_VALUATION_MECHANISM_ID + " ===");
        Date startDate = new Date();
        System.out.println("=== Simulation initiation date: " + startDate + " ===\n");
        System.out.println("=== Parameters values used during the simulation ===\n");
        System.out.println("Close destination coefficient: " + Constants.CLOSE_DESTINATION_MODIFIER);
        System.out.println("Free parking spaces coefficient: " + Constants.FREE_SPACES_NUMBER_COEFFICIENT);
        System.out.println("Highest maximum capacity of a single parking: " + Constants.PARKING_SPOT_BOUND_EXCLUSIVE);
        System.out.println("User funds lower bound: " + Constants.USER_FUNDS_LOWER_BOUND);
        System.out.println("User funds higher bound: " + Constants.USER_FUNDS_UPPER_BOUND);
        System.out.println("Lower base parking price limit: " + Constants.LOWER_BASE_PRICE_LIMIT);
        System.out.println("Upper base parking price limit: " + Constants.UPPER_BASE_PRICE_LIMIT);
        System.out.println("Lower covered price limit: " + Constants.LOWER_COVERED_PRICE_LIMIT);
        System.out.println("Upper covered price limit: " + Constants.UPPER_COVERED_PRICE_LIMIT);
        System.out.println("Lower secured price limit: " + Constants.LOWER_SECURED_PRICE_LIMIT);
        System.out.println("Upper secured price limit: " + Constants.UPPER_SECURED_PRICE_LIMIT);
        System.out.println("Lower special price limit: " + Constants.LOWER_SPECIAL_PRICE_LIMIT);
        System.out.println("Upper special price limit: " + Constants.UPPER_SPECIAL_PRICE_LIMIT);
        System.out.println("Inverse free spaces price coefficient: " + Constants.FREE_SPACES_PRICE_COEFFICIENT + "\n");
    }

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
                finalPrice = freeSpacesCoefficient;
                break;
            case 3:
                finalPrice = freeSpacesCoefficient + parkingCharacteristicsCoefficient;
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

    public static IterationInformationDto printAndSaveIterationInformation(IterationInformationDto iiDto) {
        int validOffers = checkIfOffersNumberMatch(iiDto.getParkings());
        int usersWithParkingLots = countSatisfiedUsers(iiDto.getAppUsers());
        int freeParkingSpaces = countAllFreeParkingSpaces(iiDto.getParkings());
        double happinessPercentage = countHappinnessPercentage(usersWithParkingLots, iiDto.getAppUsers());
        double takenParkingSpacesPercentage = countTakenParkingSpacesPercentage(usersWithParkingLots, iiDto.getAllParkingSpaces());

        System.out.println("Iteration number: " + iiDto.getAlgorithmIteration());
        System.out.println("Iteration starts with: " + freeParkingSpaces + " free parking spaces.");
        System.out.println("Number of valid offers: " + validOffers);
        System.out.println("Reserved parking spaces percentage: " + takenParkingSpacesPercentage + "%");
        System.out.println("Satisfied users number: " + usersWithParkingLots + " (" + happinessPercentage + "%)" + "\n");

        iiDto.setSatisfiedUsersPercentage(happinessPercentage);
        iiDto.setReservedParkingSpacesPercentage(takenParkingSpacesPercentage);
        return iiDto;
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

    public static IterationInformationDto configureIterationInformationDto(int algorithmIteration,
                                                                           List<UserDto> appUsers,
                                                                           List<ParkingDto> parkings,
                                                                           int allParkingSpaces) {
        return IterationInformationDto.builder()
                .algorithmIteration(algorithmIteration)
                .appUsers(appUsers)
                .parkings(parkings)
                .allParkingSpaces(allParkingSpaces)
                .build();
    }

    public static double calculateAverageParkingSpacePrice(HashMap<UserDto, ParkingDto> satisfiedUsers) {
        double averagePrice;
        double fullCost = 0;
        int numberOfReservedLots = 0;

        for (ParkingDto reservedParking : satisfiedUsers.values()) {
            fullCost += reservedParking.getCost();
            numberOfReservedLots++;
        }

        averagePrice = fullCost / (double) numberOfReservedLots;

        return Math.round(averagePrice);
    }

    public static double calculateExactlyMetRequirementsPercentage(HashMap<UserDto, ParkingDto> satisfiedUsers) {
        int index = 0;
        int satisfiedUsersCoefficient = 0;

        for (Map.Entry<UserDto, ParkingDto> entry : satisfiedUsers.entrySet()) {
            UserDto user = entry.getKey();
            ParkingDto parking = entry.getValue();

            int covReq = user.isNeedCoveredParking() == parking.isCovered() ? 1 : 0;
            int secReq = user.isNeedSecuredParking() == parking.isSecured() ? 1 : 0;
            int speReq = user.isNeedSpecialParking() == parking.isSpecial() ? 1 : 0;

            index += covReq + secReq +speReq;
            satisfiedUsersCoefficient += 3;
        }

        double percentage = (double) index / (double) satisfiedUsersCoefficient * 100;
        return Math.round(percentage * 100) / 100.0;
    }

    public static void runStatisticalAnalysis(List<IterationInformationDto> gatheredData) {
        List<Double> sup = new ArrayList<>();
        List<Double> rpsp = new ArrayList<>();
        List<Double> apsp = new ArrayList<>();
        List<Double> emrp = new ArrayList<>();

        for (IterationInformationDto iiDto : gatheredData) {
            sup.add(iiDto.getSatisfiedUsersPercentage());
            rpsp.add(iiDto.getReservedParkingSpacesPercentage());
            apsp.add(iiDto.getAverageParkingSpacePrice());
            emrp.add(iiDto.getExactlyMetRequirementsPercentage());
        }

        Collections.sort(sup);
        Collections.sort(rpsp);
        Collections.sort(apsp);
        Collections.sort(emrp);

        double supA = sup.stream().mapToDouble(val -> val).average().orElse(0.0);
        double rpspA = rpsp.stream().mapToDouble(val -> val).average().orElse(0.0);
        double apspA = apsp.stream().mapToDouble(val -> val).average().orElse(0.0);
        double emrpA = emrp.stream().mapToDouble(val -> val).average().orElse(0.0);

        System.out.println("Minimal values of parameters: satisfiedUsersPercentage: " + Math.round(Collections.min(sup) * 100) / 100.0 +
                " , reservedParkingSpacesPercentage: " + Math.round(Collections.min(rpsp) * 100) / 100.0 +
                " , averageParkingSpacePrice: " + Math.round(Collections.min(apsp) * 100) / 100.0 +
                " , exactlyMetRequirementsPercentage: " + Math.round(Collections.min(emrp) * 100) / 100.0);

        System.out.println("Maximal values of parameters: satisfiedUsersPercentage: " + Math.round(Collections.max(sup) * 100) / 100.0 +
                " , reservedParkingSpacesPercentage: " + Math.round(Collections.max(rpsp) * 100) / 100.0 +
                " , averageParkingSpacePrice: " + Math.round(Collections.max(apsp) * 100) / 100.0 +
                " , exactlyMetRequirementsPercentage: " + Math.round(Collections.max(emrp) * 100) / 100.0);

        System.out.println("Median values of parameters: satisfiedUsersPercentage: " + Math.round(median(sup) * 100) / 100.0 +
                " , reservedParkingSpacesPercentage: " + Math.round(median(rpsp) * 100) / 100.0 +
                " , averageParkingSpacePrice: " + Math.round(median(apsp) * 100) / 100.0 +
                " , exactlyMetRequirementsPercentage: " + Math.round(median(emrp) * 100) / 100.0);

        System.out.println("Variation values of parameters: satisfiedUsersPercentage: " + Math.round(sd(sup)*sd(sup) * 100) / 100.0 +
                " , reservedParkingSpacesPercentage: " + Math.round(sd(rpsp)*sd(rpsp) * 100) / 100.0 +
                " , averageParkingSpacePrice: " + Math.round(sd(apsp)*sd(apsp) * 100) / 100.0 +
                " , exactlyMetRequirementsPercentage: " + Math.round(sd(emrp)*sd(emrp) * 100) / 100.0);

        System.out.println("Standard deviation values of parameters: satisfiedUsersPercentage: " + Math.round(sd(sup) * 100) / 100.0 +
                " , reservedParkingSpacesPercentage: " + Math.round(sd(rpsp) * 100) / 100.0 +
                " , averageParkingSpacePrice: " + Math.round(sd(apsp) * 100) / 100.0 +
                " , exactlyMetRequirementsPercentage: " + Math.round(sd(emrp) * 100) / 100.0);

        System.out.println("Average satisfied Users percentage: " + Math.round(supA * 100) / 100.0  + "%");
        System.out.println("Average reserved parking spaces percentage: " + Math.round(rpspA * 100) / 100.0 + "%");
        System.out.println("Average parking space price: " + Math.round(apspA * 100) / 100.0 + " PLN");
        System.out.println("Exactly met requirement coefficient: " + Math.round(emrpA * 100) / 100.0 + "%\n");
    }

    public static double sum(List<Double> a) {
        if (a.size() > 0) {
            int sum = 0;

            for (Double i : a) {
                sum += i;
            }

            return sum;
        }

        return 0;
    }

    public static double mean(List<Double> a) {
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return mean;
    }

    public static double median(List<Double> a) {
        int middle = a.size() / 2;

        if (a.size() % 2 == 1) {
            return a.get(middle);
        } else {
            return (a.get(middle - 1) + a.get(middle)) / 2.0;
        }
    }

    public static double sd(List<Double> a) {
        int sum = 0;
        double mean = mean(a);

        for (Double i : a)
            sum += Math.pow((i - mean), 2);
        return Math.sqrt(sum / (a.size() - 1.0)); // sample
    }

}
