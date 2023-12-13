package orderAndDriver;

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
class DriverGui extends JFrame {
    private static final long serialVersionUID = 1L;

    private DriverAgent myAgent;

    private JTextField titleField, priceField;

    DriverGui(DriverAgent a) {
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.add(new JLabel("Max weight:"));
        titleField = new JTextField(15);
        p.add(titleField);
        p.add(new JLabel("Cruiser time:"));
        priceField = new JTextField(15);
        p.add(priceField);
        getContentPane().add(p, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    String MaxWeight = priceField.getText().trim();
                    String CruiseTime = titleField.getText().trim();
                    myAgent.updateCatalogue(Integer.parseInt(MaxWeight), Integer.parseInt(CruiseTime));
                    titleField.setText("");
                    priceField.setText("");
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(DriverGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } );
        p = new JPanel();
        p.add(addButton);
        getContentPane().add(p, BorderLayout.SOUTH);

        // Сделайте так, чтобы агент завершал работу, когда пользователь закрывает
        // графический интерфейс с помощью кнопки в правом верхнем углу
        addWindowListener(new	WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent.doDelete();
            }
        } );

        setResizable(false);
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