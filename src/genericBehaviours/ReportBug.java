package genericBehaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportBug extends OneShotBehaviour {
    public void action() {
        List<DFAgentDescription> serviceAgents = getListOfServiceAgents(new ArrayList<>());

        if (!serviceAgents.isEmpty()) {
            ACLMessage bugReport = configureBugReport(serviceAgents);
            myAgent.send(bugReport);
        } else {
            System.out.println("An error occurred in the ReportBug behaviour.");
        }
    }

    private List<DFAgentDescription> getListOfServiceAgents(List<DFAgentDescription> serviceAgents) {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(Constants.SERVICE_AGENT_CLASS_TYPE);
        dfAgentDescription.addServices(serviceDescription);

        try {
            serviceAgents = Arrays.asList(DFService.search(myAgent, dfAgentDescription));
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return serviceAgents;
    }

    private ACLMessage configureBugReport(List<DFAgentDescription> serviceAgents) {
        ACLMessage bugReport = new ACLMessage(ACLMessage.REQUEST);
        Date date = new Date();
        // ToDo#2.3 Solve this
//        bugReport.addReceiver(serviceAgents.get(0));
        bugReport.setLanguage(Constants.MESSAGE_LANGUAGE);
        bugReport.setProtocol(Constants.BUG_REPORT_MESSAGE_PROTOCOL);
        bugReport.setContent(date + Constants.BUG_REPORT_MESSAGE_CONTENT);
        // bugReport.setOntology("I guess I won't be using an ontology");
        bugReport.setSender(myAgent.getAID());
        bugReport.setDefaultEnvelope();
        return bugReport;
    }
}