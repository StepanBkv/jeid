package orderAndDriver;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;

public class DriverAgent extends Agent {
    // Каталог книг, выставленных на продажу (сопоставляет название книги с ее ценой)
    List<Integer> List_of_vessel_resource = new ArrayList<>();
    // Графический интерфейс, с помощью которого пользователь может добавлять книги в каталог
    private DriverGui myGui;

    protected void setup() {
// Создайте список ресурса перевозчика

// Создайте и покажите графический интерфейс
        myGui = new DriverGui(this);
        myGui.showGUI();

// Добавьте поведение, обслуживающее запросы на предложение от агентов заказчиков
        addBehaviour(new OfferRequestsServer());
// Добавьте поведение при обслуживании заказов на покупку от агентов заказчиков
        addBehaviour(new PurchaseOrdersServer());
    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Seller-agent " + getAID().getName() + " terminating.");
    }

    /**
     * Это вызывается графическим интерфейсом, когда пользователь добавляет новую книгу для продажи
     */
    public void updateCatalogue(final int MaxWeight, final int CruiseTime) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                List_of_vessel_resource.add(new Integer(CruiseTime));
                List_of_vessel_resource.add(new Integer(MaxWeight));
            }
        });
    }
    // Сервер запросов предложений (обслуживание)
    private class OfferRequestsServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // Получено сообщение CFP. Обработайте это
                Integer CargoWeight = Integer.valueOf((String) msg.getContent());
                ACLMessage reply = msg.createReply();
//                Integer price = (Integer) List_of_vessel_resource.get(title);

                if (!List_of_vessel_resource.isEmpty()) {
                    System.out.println(List_of_vessel_resource.get(0));
                    if (CargoWeight <= List_of_vessel_resource.get(0)) {
                        // Запрошенный заказ доступен для перевозки. Ответьте с указанием времени перевозки
                        reply.setPerformative(ACLMessage.PROPOSE);
                        // Отправляем время перевозки
                        reply.setContent(String.valueOf(List_of_vessel_resource.get(1)));
                    } else {
                        // Принятый заказ больше, чем можно перевезти.
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("not-available");
                    }
            }
                myAgent.send(reply);
            }
            else {
                block();
            }
        }
    }  // Конец сервера запросов предложений внутреннего класса

    // Сервер заказов на покупку(действие при обслуживании)
    private class PurchaseOrdersServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ПОЛУЧЕНО сообщение О ПРИНЯТИИ ПРЕДЛОЖЕНИЯ. Обработайте это
                Integer account = Integer.valueOf((String) msg.getContent());
                ACLMessage reply = msg.createReply();

                //Integer price = (Integer) List_of_vessel_resource.remove(title);
                if (List_of_vessel_resource.get(1) != null) {
                    Integer timeToDrive = List_of_vessel_resource.get(1);
                    List_of_vessel_resource.remove(1);
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println("За "+account+"у.е продана услуга агенту: "+msg.getSender().getName());
                    try {
                        Thread.sleep(timeToDrive*1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    List_of_vessel_resource.add(1,timeToDrive);
                }
                else {
                    // Другой заказчик взял услугу.
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            }
            else {
                block();
            }
        }
    }  //  Конец сервера запросов предложений внутреннего класса

}