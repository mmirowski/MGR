package genericBehaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;

public class TerminateAgent extends OneShotBehaviour {
    public void action() {
        myAgent.doDelete();
    }

    protected void takeDown() {
        try {
            DFService.deregister(myAgent);
            System.out.println("Agent " + myAgent.getLocalName() + " has done his work and is going to be turned off.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



