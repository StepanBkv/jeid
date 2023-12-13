package electroCar;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransformerAgent extends Agent {
    int charge = 15;

    protected void setup() {
        addBehaviour(new OfferRequestsServer());
    }
        // Сервер запросов предложений (обслуживание)
    private class OfferRequestsServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // Получено сообщение CFP. Обработайте это
                String Move = msg.getContent();
                ACLMessage reply = msg.createReply();
                if (Objects.equals(Move, "move")) {
                    charge--;
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setConversationId("move");
                    reply.setContent(String.valueOf(charge));
                }
                else if (Objects.equals(Move, "refull")) {
                    charge = 100;
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setConversationId("refull");
                    reply.setContent(String.valueOf(charge));
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }  // Конец сервера запросов предложений внутреннего класса
}