package electroCar;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DriverAgent extends Agent {
    private DriverGui myGui;
    private AID[] carAgent = {new AID("car1", AID.ISLOCALNAME)};
    private MessageTemplate mt;

    protected void setup() {
        System.out.println("Hello! Driver - agent "+getAID().getName() +"is ready.");
        myGui = new DriverGui(this);
        myGui.showGUI();
        myGui.addKeyListener(new KeyListener() {//описание обработчика клавиши
            @Override
            public void keyPressed(KeyEvent keyEvent) {//при удерживании клавиши
                switch (keyEvent.getKeyCode()) {//считывание кода нажатой клавиши
                    case KeyEvent.VK_UP://если нажата клавиша вверх
                        move("UP");
                        //вызов метода up
                        break;
                    case KeyEvent.VK_DOWN://если нажата клавиша вниз
                        move("DOWN");//вызов метода down
                        break;
                    case KeyEvent.VK_LEFT://если нажата клавиша влево
                        move("LEFT");//вызов метода left
                        break;
                    case KeyEvent.VK_RIGHT://если нажата клавиша вправо
                        move("RIGHT");//вызов метода right
                        break;
                    case KeyEvent.VK_S:
                        move("PRESS_S");
                        break;
                    case KeyEvent.VK_E:
                        move("PRESS_E");
                        break;
                    case KeyEvent.VK_F:
                        move("PRESS_F");
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }//при отпускании клавиши ничего не делать

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }//при вводе клавиши ничего не делать
        });
    }

    protected void move(String move){
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt;
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < carAgent.length; ++i) {
                    cfp.addReceiver(carAgent[i]);
                }
                cfp.setContent(move);
                cfp.setConversationId("move");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                myAgent.send(cfp);
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("move"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                ACLMessage reply = myAgent.receive(mt);
            }
        });
    }
}  // End of inner class RequestPerformer
