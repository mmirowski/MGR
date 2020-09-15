import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
//        long seed = new Date().getTime();
//        Random generator = new Random(seed);

        System.out.println("Please, enter parking's configuration parameters in the following order: x " +
                "coordinate, y coordinate, maximum capacity, free spaces. Split numbers with comma.");

        Runtime runtime = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        Object[] object = new Object[]{50.23, 41.902, 50, 40, true, true, false};
        ContainerController containerController = runtime.createMainContainer(p);
        try {
            AgentController testAgent = containerController.createNewAgent("PA02", "roles.ParkingAgent", object);
            testAgent.getState();
            System.out.println("Log");
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}