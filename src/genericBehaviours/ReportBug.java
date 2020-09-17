package genericBehaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Constants;

import java.util.Date;

public class ReportBug extends OneShotBehaviour {
    public void action() {
        ACLMessage bugReport = new ACLMessage(ACLMessage.REQUEST);
        Date date = new Date();
        // ToDo#1 Ask, how to get ServiceAgent's AID to put it here - as a constant bug reports receiver.
        bugReport.addReceiver();
        bugReport.setLanguage(Constants.MESSAGE_LANGUAGE);
        bugReport.setContent(date + Constants.BUG_REPORT_MESSAGE_CONTENT);
        // paymentReminder.setOntology("I guess I won't be using an ontology");
        bugReport.setSender(myAgent.getAID());
        bugReport.setDefaultEnvelope();
        myAgent.send(bugReport);
    }
}