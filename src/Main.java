import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Constants;

public class Main {
    public static void main(String[] args) {
//        long seed = new Date().getTime();
//        Random generator = new Random(seed);

        Runtime runtime = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        Object[] object = new Object[]{50.23, 41.902, 50, 40, true, true, false};
        ContainerController containerController = runtime.createMainContainer(p);
        try {
            AgentController serviceAgent = containerController.createNewAgent(Constants.SERVICE_AGENT_NICKNAME,
                    Constants.SERVICE_AGENT_CLASS_NAME, null);
            System.out.println("This is a dummy text before agents activation");
            serviceAgent.activate();

            for (int i = 0; i < 3; i++) {
                AgentController parkingAgent = containerController.createNewAgent(Constants.PARKING_AGENT_NICKNAME + i,
                        Constants.PARKING_AGENT_CLASS_NAME, object);
                parkingAgent.getState();
            }

            System.out.println("This is another placeholder.");
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}