package agents;

import genericBehaviours.TerminateAgent;
import dtos.BugReportDto;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.Constants;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAgent extends Agent {

    private List<BugReportDto> bugReports = new LinkedList<>();

    protected void setup() {
        AID serviceAgentID = getAID();
        registerWithinDF(serviceAgentID);
        System.out.println("Agent " + serviceAgentID.getName() + " is ready to work.");
        setBehavioursQueue();
    }

    private void registerWithinDF(AID serviceAgentID) {
        // To register an Agent in the Directory Facilitator, Service Description is performed
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(Constants.SERVICE_AGENT_CLASS_TYPE);
        serviceDescription.setName(Constants.SERVICE_AGENT_NICKNAME);

        DFAgentDescription dfServiceAgentDescription = new DFAgentDescription();
        dfServiceAgentDescription.setName(serviceAgentID);
        dfServiceAgentDescription.addServices(serviceDescription);

        // Try performing registering action
        try {
            DFService.register(this, dfServiceAgentDescription);
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    private void setBehavioursQueue() {
        // ToDo#1 User verification and vehicles checking behaviours should be added here during further
        //  implementation work - especially when application will be used for business purposes
        FSMBehaviour fsmBehaviour = new FSMBehaviour(this);
        fsmBehaviour.registerFirstState(new SayHello(), Constants.INITIAL_STATE);
        fsmBehaviour.registerState(new ListenForBugReports(), Constants.STATE_A);
        fsmBehaviour.registerState(new ProcessBugReports(), Constants.STATE_B);
        fsmBehaviour.registerLastState(new TerminateAgent(), Constants.FINAL_STATE);

        fsmBehaviour.registerDefaultTransition(Constants.INITIAL_STATE, Constants.STATE_A);
        fsmBehaviour.registerDefaultTransition(Constants.STATE_A, Constants.STATE_B);
        fsmBehaviour.registerDefaultTransition(Constants.STATE_B, Constants.STATE_A);
        fsmBehaviour.registerDefaultTransition(Constants.STATE_A, Constants.FINAL_STATE);

        addBehaviour(fsmBehaviour);
    }

    private class SayHello extends OneShotBehaviour {
        public void action() {
            System.out.println("Service Agent's local name is: " + getAID().getLocalName());
            System.out.println("Agent's addresses are: " + String.join(", ", getAID().getAddressesArray()));
            System.out.println("Service Agent is ready to help and assist.");
        }
    }

    private class ListenForBugReports extends CyclicBehaviour {
        public void action() {
            // Agent sending bug report asks Service Agent to perform an action: pass bug info to IT Services
            MessageTemplate bugReportMessage = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchProtocol(Constants.BUG_REPORT_MESSAGE_PROTOCOL));
            ACLMessage message = receive(bugReportMessage);
            ACLMessage reply = message.createReply();
            Date date = new Date();
            if (message != null) {
                try {
                    bugReports.add((BugReportDto) message.getContentObject());
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent(date + Constants.MESSAGE_SENT_SUCCESSFULLY);
                    send(reply);
                } catch (UnreadableException e) {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent(date + Constants.MESSAGE_NOT_SENT);
                    send(reply);
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }

    private class ProcessBugReports extends OneShotBehaviour {
        public void action() {
            Properties systemProperties = System.getProperties();
            systemProperties.setProperty(Constants.MAIL_SETUP_PROPERTY, Constants.LOCALHOST);
            Session session = Session.getDefaultInstance(systemProperties);
            for (BugReportDto element : bugReports) {
                Date date = new Date();
                try {
                    sendMail(session, element);
                    System.out.println(date + ": email was sent successfully.");
                } catch (MessagingException e) {
                    e.printStackTrace();
                    System.out.println(date + ": email was not sent due to an error that occurred in sendMail() " +
                            "method.");
                }
            }
        }
    }

    private void sendMail(Session session, BugReportDto element) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(Constants.MAS_SERVICES_CONTACT_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Constants.IT_SERVICES_CONTACT_EMAIL));
        message.setSubject(element.getSubject());
        message.setText(element.getMessage());
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("Bug Report (ID: ")
                .append(element.getBugReportIDNumber())
                .append(") sent by: ")
                .append(element.getBugReporter());
        message.setDescription(stringBuilder.toString());
        Transport.send(message);
    }
}