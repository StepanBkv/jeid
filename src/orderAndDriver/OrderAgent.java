package orderAndDriver;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
    public class OrderAgent extends Agent {

        private int CargoWeight;

        private int Account;

        // The list of known seller agents
        private AID[] sellerAgents = {new AID("driver1", AID.ISLOCALNAME), new AID("driver2", AID.ISLOCALNAME),
                new AID("driver3", AID.ISLOCALNAME)};

        protected void setup() {
// Printout a welcome message
            System.out.println("Hello! Order - agent "+getAID().getName() +"is ready.");
// Get the title of the book to buy as a start-up argument

            Object[] args = getArguments();

            if (args != null && args.length > 0) {//Если есть название книги
                CargoWeight = Integer.valueOf((String) args[0]);
                Account = Integer.valueOf((String) args[1]);
                System.out.println("Trying to buy " + CargoWeight);
// добавить TickerBehaviour, которое организует запрос к «продавцу» каждую минуту
                addBehaviour(new TickerBehaviour(this, 20000) {

                    protected void onTick() {
                        myAgent.addBehaviour(new RequestPerformer());
                    }
                });
            } else {
// Make the agent terminate
                System.out.println("No target book title specified");
                doDelete();
            }
        }

        protected void takeDown() {
// Printout a dismissal message
            System.out.println("Buyer-agent " + getAID().getName() + " terminating.");

        }
        private class RequestPerformer extends Behaviour {
            private static final long serialVersionUID = 1L;

            private AID bestDriver; // The agent who provides the best offer

            private int bestTime = 0;// The best cruise time
            private int repliesCnt = 0; // The counter of replies from seller agents
            private MessageTemplate mt; // The template to receive replies
            private int step = 0;

            public void action() {
                switch (step) {
                    case 0:
                        // Send the cfp to all sellers
                        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                        for (int i = 0; i < sellerAgents.length; ++i) {
                            cfp.addReceiver(sellerAgents[i]);
                        }
                        cfp.setContent(String.valueOf(CargoWeight));
                        cfp.setConversationId("book-trade");
                        cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                        myAgent.send(cfp);
                        // Prepare the template to get proposals
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                                MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                        step = 1;
                        break;
                    case 1:
                        // Receive all proposals/refusals from seller agents
                        ACLMessage reply = myAgent.receive(mt);
                        if (reply != null) {
                            // Reply received
                            if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                // This is an offer
                                int price = Integer.parseInt(reply.getContent());
                                if (bestDriver == null || price < bestTime) {
                                    // This is the best offer at present
                                    bestTime = price;
                                    bestDriver = reply.getSender();
                                }
                            }
                            repliesCnt++;
                            if (repliesCnt >= sellerAgents.length) {
                                // We received all replies
                                step = 2;
                            }
                        }
                        else {
                            block();
                        }
                        break;
                    case 2:
                        // Send the purchase order to the seller that provided the best offer
                        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        order.addReceiver(bestDriver);
                        order.setContent(String.valueOf(Account));
                        order.setConversationId("book-trade");
                        order.setReplyWith("order"+System.currentTimeMillis());
                        myAgent.send(order);

                        // Prepare the template to get the purchase order reply
                        mt = MessageTemplate.and(
                                MessageTemplate.MatchConversationId("book-trade"),
                                MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                        step = 3;
                        break;
                    case 3:
                        // Receive the purchase order reply
                        reply = myAgent.receive(mt);
                        if (reply != null) {
                            // Purchase order reply received
                            if (reply.getPerformative() == ACLMessage.INFORM) {
                                // Purchase successful. We can terminate
                                System.out.println(Account
                                        + " successfully purchased from agent "
                                        + reply.getSender().getName());
                                System.out.println("Price = " + bestTime);
                                myAgent.doDelete();
                            }
                            else {
                                System.out.println("Attempt failed: requested book already sold.");
                            }

                            step = 4;
                        }
                        else {
                            block();
                        }
                        break;
                }
            }

            public boolean done() {
                if (step == 2 && bestDriver== null) {
                    System.out.println("Attempt failed: "+bestTime+" not available for sale");
                }
                return ((step == 2 && bestDriver == null) || step == 4);
            }
        }  // End of inner class RequestPerformer
    }