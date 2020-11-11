import dtos.UserDto;
import utils.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    public static void main(String[] args) {
        List<UserDto> appUsers = new ArrayList<>();
        extractDataFromSurveyResponses(appUsers);
        // for each appUser prepare a requestDto to the closest to the destination parking
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
}
