package roles;

import jade.core.Agent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BankAgent extends Agent {

    protected void setup() {
        System.out.println("Agent " + getAID().getName() + " is ready to work.");
        setBehavioursQueue();
    }

    private void setBehavioursQueue() {

    }
}