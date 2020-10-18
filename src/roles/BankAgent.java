package roles;

import genericBehaviours.ReportBug;
import genericBehaviours.TerminateAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
import lombok.Setter;
import utils.Constants;

import java.util.HashMap;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class BankAgent extends Agent {

    private double bankVault;
    private boolean auctionIsNeeded;
    private HashMap<AID, Double> clientsOffersMapping = new HashMap<>();
    private HashMap<AID, Double> finalClientsMapping = new HashMap<>();

    protected void setup() {
        AID bankAgentID = getAID();
        registerWithinDF(bankAgentID);
        System.out.println("Agent " + bankAgentID.getName() + " is ready to work.");
        addBehaviour(new ListenForParkingManagerCalls());
    }

    private void registerWithinDF(AID bankAgentID) {
        // To register an Agent in the Directory Facilitator, Service Description is performed
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(Constants.BANK_AGENT_CLASS_TYPE);
        serviceDescription.setName(Constants.BANK_AGENT_NICKNAME);

        DFAgentDescription dfBankAgentDescription = new DFAgentDescription();
        dfBankAgentDescription.setName(bankAgentID);
        dfBankAgentDescription.addServices(serviceDescription);

        // Try performing registering action
        try {
            DFService.register(this, dfBankAgentDescription);
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    private class ListenForParkingManagerCalls extends CyclicBehaviour {
        public void action() {
            // Parking Agent asks Bank Agent to perform an action: conduct an auction
            MessageTemplate auctionRequest = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchProtocol(Constants.CALL_FOR_AN_AUCTION));
            ACLMessage parkingManagerMessage = receive(auctionRequest);
            if (parkingManagerMessage != null) {
                // From the Parking Agent's message retrieve information about auction necessity:
                // if one should be performed, then set class variable auctionIsNeeded to true.
                try {
                    auctionIsNeeded = (boolean) parkingManagerMessage.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                // Handle auction process
                if (auctionIsNeeded) {
                    addBehaviour(new InitializeAuction());
                    // After finishing ConductAuction Behaviour run, class variable auctionIsNeeded is set to false
                    addBehaviour(new ConductAuction());
                    // Offers will be sent only to these Clients, which are on the finalCustomers list - they should
                    //  confirm once more, that they are willing to make a reservation.
                    addBehaviour(new SendFinalOffers());
                } else {
                    addBehaviour(new SendFinalOffers());
                }
            } else {
                block();
            }
        }
    }

    private class InitializeAuction extends OneShotBehaviour {
        public void action() {
            // ToDo#n-1 implement that method
            // During auction make sure, that Clients won't bid more, than they have on their accounts'
            //  (money >= bid)
        }
    }

    private class ConductAuction extends OneShotBehaviour {
        public void action() {
            // ToDo#n-2 implement that method
            auctionIsNeeded = false;
        }
    }

    private class SendFinalOffers extends OneShotBehaviour {
        public void action() {
            // Send final offers to auction winners
            clientsOffersMapping.forEach((clientAgent, acceptedOfferPrice) -> {
                ACLMessage offer = prepareFinalOffer(clientAgent);
                send(offer);
            });
            addBehaviour(new ListenForClientFinalDecision());
        }
    }

    private ACLMessage prepareFinalOffer(AID clientAgent) {
        ACLMessage offer = new ACLMessage(ACLMessage.PROPOSE);
        offer.setProtocol(Constants.CALL_FOR_FINAL_DECISION);
        offer.setLanguage(Constants.MESSAGE_LANGUAGE);
        offer.setContent(Constants.FINAL_OFFER_MESSAGE);
        offer.addReceiver(clientAgent);
        // offer.setOntology("I guess I won't be using an ontology");
        offer.setSender(getAID());
        offer.setDefaultEnvelope();
        return offer;
    }

    private class ListenForClientFinalDecision extends CyclicBehaviour {
        public void action() {
            MessageTemplate finalDecisionMessage = MessageTemplate
                    .and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                            MessageTemplate.MatchProtocol(Constants.ACCEPT_MESSAGE));
            ACLMessage acceptMessage = receive(finalDecisionMessage);
            if (acceptMessage != null) {
                Double acceptedPrice = clientsOffersMapping.get(acceptMessage.getSender());
                finalClientsMapping.put(acceptMessage.getSender(), acceptedPrice);
                addBehaviour(new FinalizeTransaction());
            } else {
                block();
            }
        }
    }

    private class FinalizeTransaction extends OneShotBehaviour {
        public void action() {
            finalClientsMapping.forEach((clientAgent, acceptedOfferPrice) -> {
                ACLMessage paymentReminder = preparePaymentReminder(clientAgent);
                send(paymentReminder);
                bankVault += acceptedOfferPrice;
            });

            // ToDo#8 During further implementation and app designing add a dedicated functionality to report a bug.
            //  Right now a bug report is sent with dummy data approx. once in every 5 runs of Bank Agent.
            Random random = new Random();
            int upperBound = 5;
            if (random.nextInt(upperBound) == 0) {
                addBehaviour(new ReportBug());
            }

            System.out.println("Bank is now unavailable");
            addBehaviour(new TerminateAgent());
        }
    }

    private ACLMessage preparePaymentReminder(AID clientAgent) {
        ACLMessage paymentReminder = new ACLMessage(ACLMessage.REQUEST);
        paymentReminder.addReceiver(clientAgent);
        paymentReminder.setLanguage(Constants.MESSAGE_LANGUAGE);
        paymentReminder.setContent(Constants.PAYMENT_REMINDER_MESSAGE);
        // paymentReminder.setOntology("I guess I won't be using an ontology");
        paymentReminder.setSender(getAID());
        paymentReminder.setDefaultEnvelope();
        return paymentReminder;
    }
}