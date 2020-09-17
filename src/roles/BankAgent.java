package roles;

import genericBehaviours.ReportBug;
import genericBehaviours.TerminateAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
    private HashMap<AID, Double> clientsOffers = new HashMap<>();

    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready to work.");
        addBehaviour(new ListenForParkingManagerCalls());
    }

    private class ListenForParkingManagerCalls extends CyclicBehaviour {
        public void action() {
            // Parking Agent asks Bank Agent to perform an action: conduct an auction
            MessageTemplate auctionRequest = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage parkingManagerMessage = receive(auctionRequest);
            if (parkingManagerMessage != null) {
                // From the Parking Agent's message retrieve information about auction necessity: if one should be
                // performed, then set class variable auctionIsNeeded to true.


                // Handle auction process
                if (auctionIsNeeded) {
                    addBehaviour(new InitializeAuction());
                    // During auction make sure, that Clients won't bid more, than they have on their accounts'
                    // (money >= bid)
                    addBehaviour(new ConductAuction());
                    // After finishing implementation of ConductAuction Behaviour, set class variable auctionIsNeeded
                    // to false

                    // Offers will be sent only to these Clients, which are on the finalCustomers list
                    addBehaviour(new SendOffers());
                } else {
                    addBehaviour(new SendOffers());
                }
            } else {
                block();
            }
        }
    }

    private class SendOffers extends OneShotBehaviour {
        public void action() {
            // Send offers to auction winners / clients now and receive their responses


            addBehaviour(new FinalizeTransaction());
        }
    }

    private class FinalizeTransaction extends OneShotBehaviour {
        public void action() {
            clientsOffers.forEach((clientAgent, acceptedOfferPrice) -> {
                ACLMessage paymentReminder = preparePaymentReminder(clientAgent);
                send(paymentReminder);
                bankVault += acceptedOfferPrice;
            });

            // ToDo#8 Ask whether a behaviour can be accessed optionally - that would be ideal to mock bug reporting.
            //  Right now a bug report is sent with dummy data approx. once in every 5 runs of Bank Agent:
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