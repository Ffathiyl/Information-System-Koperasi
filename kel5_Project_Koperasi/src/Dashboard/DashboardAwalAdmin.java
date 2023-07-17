package Dashboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardAwalAdmin extends JFrame{
    public JPanel DashboardAwalAdmin;
    private JButton exitButton;
    private JButton button1;
    private JButton button2;
    private JLabel labelUsername;

    public DashboardAwalAdmin(String data){
        setContentPane(DashboardAwalAdmin);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        labelUsername.setText(data);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardMaster(data).setVisible(true);
                dispose();
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                dispose();
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardTransaksi(data).setVisible(true);
                dispose();
            }
        });
    }
}
