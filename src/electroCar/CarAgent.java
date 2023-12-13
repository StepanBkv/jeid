package electroCar;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CarAgent extends Agent {
    // Каталог книг, выставленных на продажу (сопоставляет название книги с ее ценой)
    static boolean carStart = false;
    boolean refull = false;
    private int step = 0;
    // Графический интерфейс, с помощью которого пользователь может добавлять книги в каталог

    private static AID[] transformAgent = {new AID("transform1", AID.ISLOCALNAME)};

    protected void setup() {
        addBehaviour(new OfferRequestsServer());
    }
    class OfferRequestsServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;
        private MessageTemplate mt;

        public void action() {
            mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            switch (step) {
                case 0:
                    if (msg != null) {
                        boolean refuse = true;
                        // Получено сообщение CFP. Обработайте это
                        String Move = msg.getContent();
                        ACLMessage reply = msg.createReply();
                        switch (Move) {
                            case "UP":
                                refuse = toMove("Машина двигается вперёд");
                                step = 1;
                                break;
                            case "DOWN":
                                refuse = toMove("Машина двигается назад");
                                step = 1;
                                break;
                            case "LEFT":
                                refuse = toMove("Машина двигается влево");
                                step = 1;
                                break;
                            case "RIGHT":
                                refuse = toMove("Машина двигается вправо");
                                step = 1;
                                break;
                            case "PRESS_S":
                                carStart = true;
                                System.out.println("Машина начала движение");
                                break;
                            case "PRESS_E":
                                carStart = false;
                                System.out.println("Машина закончила движение");
                                break;
                            case "PRESS_F":
                                System.out.println("Машина заряжается");
                                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                                for (int i = 0; i < transformAgent.length; ++i) {
                                    cfp.addReceiver(transformAgent[i]);
                                }
                                cfp.setContent("refull");
                                cfp.setConversationId("refull");
                                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                                myAgent.send(cfp);
                                step = 2;
                                break;
                        }
                        if (refuse) {
                            // Запрошенный заказ доступен для перевозки. Ответьте с указанием времени перевозки
                            reply.setPerformative(ACLMessage.PROPOSE);
                            // Отправляем время перевозки
                            reply.setContent(String.valueOf("OK"));
                        } else {
                            // Принятый заказ больше, чем можно перевезти.
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("Error");
                        }
                        myAgent.send(reply);

                    } else {
                        block();
                    }
                    break;

                case 1:
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    mt = MessageTemplate.MatchConversationId("move");
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            System.out.println("Заряд машины: " + Integer.parseInt(reply.getContent()));
                        }
                    }
                    else {
                        block();
                    }
                    step = 0;
                    break;

                case 2:
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    mt = MessageTemplate.MatchConversationId("refull");
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            System.out.println("Машина заряжена!");
                        }
                    }
                    else {
                        block();
                    }
                    step = 0;
                    break;
            }
        }
        boolean toMove(String Move){
            int min = 5;
            int max = 100;
            int diff = max - min;
            Random random = new Random();
            int obstacle = random.nextInt(diff + 1) + min;
            if(carStart){
                if(obstacle != 50){
                    System.out.println(Move);
                    fill();
                    return true;
                }
                else System.out.println("Впереди препятствие!");
            }
            else System.out.println("Машина не заведена!");
            return false;
        }

        protected void fill(){
            addBehaviour(new OneShotBehaviour(this.getAgent()) {
                @Override
                public void action() {
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < transformAgent.length; ++i) {
                        cfp.addReceiver(transformAgent[i]);
                    }
                    cfp.setContent("move");
                    cfp.setConversationId("move");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                }
            });
        }
    }
}