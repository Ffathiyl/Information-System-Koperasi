package Dashboard;

import Laporan.LaporanPinjaman;
import Laporan.LaporanSimpanan;
import Master.Admin;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardKetua extends JFrame{
    public JPanel DashboardKetua;
    private JPanel MainPanel;
    private JButton backButton;
    private JLabel lblAdmin;
    private JLabel lblPath;
    private JLabel lblNamaKetua;
    private JLabel lblLaporanSimpanan;
    private JLabel lblLaporanPinjaman;

    public DashboardKetua(String data) {
    setContentPane(DashboardKetua);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setUndecorated(true);
    lblNamaKetua.setText(data);
    backButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Login().setVisible(true);
            dispose();
        }
    });
        lblAdmin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                Admin admin = new Admin();
                admin.Admin.setVisible(true);
                lblPath.setText("Home / Kelola Admin");
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(admin.Admin);
            }
        });
        lblPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home");
            }
        });
        lblLaporanSimpanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                LaporanSimpanan form = new LaporanSimpanan();
                form.LaporanSImpanan.setVisible(true);
                lblPath.setText("Home / Laporan Simpanan");
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(form.LaporanSImpanan);
            }
        });
        lblLaporanPinjaman.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                LaporanPinjaman form = new LaporanPinjaman();
                form.LaporanPinjaman.setVisible(true);
                lblPath.setText("Home / Laporan Pinjaman");
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(form.LaporanPinjaman);
            }
        });
    }
}
