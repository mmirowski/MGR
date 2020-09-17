package genericBehaviours;

import jade.core.behaviours.OneShotBehaviour;

public class TerminateAgent extends OneShotBehaviour {
    public void action() {
        myAgent.doDelete();
    }

    protected void takeDown() {
        System.out.println("Agent " + myAgent.getLocalName() + " has done his work and is going to be turned off.");
    }
}



