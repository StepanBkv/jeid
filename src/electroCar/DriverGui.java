package electroCar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;//подключение пакетов
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
class DriverGui extends JFrame {
    private static final long serialVersionUID = 1L;

    private DriverAgent myAgent;

    private JTextField titleField, priceField;

    DriverGui(DriverAgent a) {
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(6, 1));
        p.add(new JLabel("Данное окошко обрабатывает все команды"));
        p.add(new JLabel("которые водитель посылает машине;"));
        p.add(new JLabel("Стрелочки - управление;"));
        p.add(new JLabel("S - завести машину;"));
        p.add(new JLabel("E - заглушить машину;"));
        p.add(new JLabel("F - заправить машину."));
        getContentPane().add(p, BorderLayout.CENTER);
        p = new JPanel();
        getContentPane().add(p, BorderLayout.SOUTH);

        // Сделайте так, чтобы агент завершал работу, когда пользователь закрывает
        // графический интерфейс с помощью кнопки в правом верхнем углу
        addWindowListener(new	WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        } );
    }

    public void showGUI() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setVisible(true);
    }
}