package utils;

public final class Constants {

    public static final String SERVICE_AGENT_NICKNAME = "Service Agent";
    public static final String BANK_AGENT_NICKNAME = "Bank Agent";
    public static final String PARKING_AGENT_NICKNAME = "Parking Agent";
    public static final String CLIENT_AGENT_NICKNAME = "Client Agent";

    public static final String SERVICE_AGENT_CLASS_NAME = "roles.ServiceAgent";
    public static final String BANK_AGENT_CLASS_NAME = "roles.BankAgent";
    public static final String PARKING_AGENT_CLASS_NAME = "roles.ParkingAgent";
    public static final String CLIENT_AGENT_CLASS_NAME = "roles.ClientAgent";

    public static final String SERVICE_AGENT_CLASS_TYPE = "Service";
    public static final String BANK_AGENT_CLASS_TYPE = "Bank";
    public static final String PARKING_AGENT_CLASS_TYPE = "Parking";
    public static final String CLIENT_AGENT_CLASS_TYPE = "Client";

    public static final String MESSAGE_LANGUAGE = "English";

    public static final String INITIAL_STATE = "State 0";
    public static final String STATE_A = "State A";
    public static final String STATE_B = "State B";
    public static final String FINAL_STATE = "State n";

    public static final String IT_SERVICES_CONTACT_EMAIL = "itservices@tempmail.com";
    public static final String MAS_SERVICES_CONTACT_EMAIL = "masservice@tempmail.com";
    public static final String MAIL_SETUP_PROPERTY = "mail.smtp.host";
    public static final String LOCALHOST = "localhost";

    public static final String MESSAGE_SENT_SUCCESSFULLY = "Your message was sent successfully.";
    public static final String MESSAGE_NOT_SENT = "An error occurred: Your message was not sent.";
    public static final String BUG_REPORT_MESSAGE_CONTENT = "Sample bug report message content.";
    public static final String CALL_FOR_PROPOSAL_CONTENT = "Call for proposal - Client is looking for a Parking.";
    public static final String FINAL_OFFER_MESSAGE = "This is a final offer.";
    public static final String ACCEPT_MESSAGE = "Accept";
    public static final String PAYMENT_REMINDER_MESSAGE = "This is a payment reminder message.";
    public static final String CLIENT_IS_STAYING_MESSAGE = "Client is staying.";
    public static final String CLIENT_IS_LEAVING_MESSAGE = "Client is leaving a parking spot.";

    public static final String BUG_REPORT_MESSAGE_PROTOCOL = "Bug report";
    public static final String REQUEST_FOR_PARKING_SPACE = "Call for proposal";
    public static final String CALL_FOR_AN_AUCTION = "Call for an auction";
    public static final String CALL_FOR_FINAL_DECISION = "Call for final decision: accept or deny parking space reservation";

    public static final String PATH_TO_SURVEY_RESPONSES_FILE = "src/files/usersResponses.txt";
    public static final String PATH_TO_TOWN_MAP_FILE = "src/files/townMap.txt";

    // Constants
    public static final int FINAL_ITERATION = 6;
    public static final int TWENTY_SECONDS = 20000;
    public static final int SCALE_MODIFIER = 1000;
    public static final double MAP_COORDINATES_BOUNDARY = 2250.0;

    // Parameters
    // Modify these to impact simulation
    public static final int CHOSEN_VALUATION_MECHANISM_ID = 1;
    public static final int CLOSE_DESTINATION_MODIFIER = 80;

    public static final double FREE_SPACES_NUMBER_COEFFICIENT = 0.05;
    public static final int PARKING_SPOT_BOUND_EXCLUSIVE = 51;
    public static final double USER_FUNDS_LOWER_BOUND = 10;
    public static final double USER_FUNDS_UPPER_BOUND = 100;
    public static final double LOWER_BASE_PRICE_LIMIT = 0;
    public static final double UPPER_BASE_PRICE_LIMIT = 20;
    public static final double LOWER_COVERED_PRICE_LIMIT = 1;
    public static final double UPPER_COVERED_PRICE_LIMIT = 4;
    public static final double LOWER_SECURED_PRICE_LIMIT = 3;
    public static final double UPPER_SECURED_PRICE_LIMIT = 8;
    public static final double LOWER_SPECIAL_PRICE_LIMIT = 8;
    public static final double UPPER_SPECIAL_PRICE_LIMIT = 15;
    public static final double FREE_SPACES_PRICE_COEFFICIENT = 0.4;
}
