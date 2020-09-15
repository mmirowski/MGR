import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.Date;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
//        long seed = new Date().getTime();
//        Random generator = new Random(seed);

        Runtime runtime = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        Object[] object = new Object[]{ 50.23, 41.902, 50, 40 };
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
