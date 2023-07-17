package Dashboard;

import Transaksi.Angsuran;
import Transaksi.Pinjaman;
import Transaksi.Simpanan;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DashboardTransaksi extends JFrame {
    private JPanel DashboardTransaksi;
    private JButton backButton;
    private JLabel lblSimpanan;
    private JLabel lblPinjaman;
    private JLabel lblAngsuran;
    private JPanel MainPanel;
    private JLabel lblPath;
    private JLabel lblNamaAdmin;

    public DashboardTransaksi(String data) {
        setContentPane(DashboardTransaksi);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        lblNamaAdmin.setText(data);
        lblSimpanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Transaksi Simpanan");
                Simpanan simpan = new Simpanan(data,HariIni());
                simpan.Simpanan.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(simpan.Simpanan);
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardAwalAdmin(data).setVisible(true);
                dispose();
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
        lblPinjaman.addComponentListener(new ComponentAdapter() {
        });
        lblPinjaman.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Transaksi Pinjaman");
                Pinjaman pinjam = new Pinjaman(data,HariIni());
                pinjam.Pinjaman.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(pinjam.Pinjaman);
            }
        });
        lblAngsuran.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Transaksi Angsuran");
                Angsuran angsuran = new Angsuran(data,HariIni());
                angsuran.Angsuran.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(angsuran.Angsuran);
            }
        });
    }

    public String HariIni(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date current = calendar.getTime();
        String formattedDate = formatter.format(current);
        return formattedDate;
    }
}
