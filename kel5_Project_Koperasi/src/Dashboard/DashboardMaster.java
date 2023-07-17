package Dashboard;

import Master.*;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardMaster extends JFrame{
    private JPanel DashboardAdmin;
    private JButton backButton;
    private JPanel MainPanel;
    private JLabel lblAnggota;
    private JLabel lblPath;
    private JLabel lblJenisSimpanan;
    private JLabel lblDenda;
    private JLabel lblPinjaman;
    private JLabel lblGaji;
    private JLabel lblNamaAdmin;

    public DashboardMaster(String data){
        setContentPane(DashboardAdmin);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        lblNamaAdmin.setText(data);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardAwalAdmin(data).setVisible(true);
                dispose();
            }
        });
        lblAnggota.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Kelola Anggota");
                Member member = new Member();
                member.Member.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(member.Member);
            }
        });
        lblJenisSimpanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Kelola Jenis Simpanan");
                JenisSimpanan jenis = new JenisSimpanan();
                jenis.JenisSimpanan.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(jenis.JenisSimpanan);
            }
        });
        lblDenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Kelola Denda");
                Denda denda = new Denda();
                denda.Denda.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(denda.Denda);
            }
        });
        lblPinjaman.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Kelola Kategori Pinjaman");
                KategoriPinjaman pinjam = new KategoriPinjaman();
                pinjam.KategoriPinjaman.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(pinjam.KategoriPinjaman);
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
        lblGaji.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                MainPanel.removeAll();
                MainPanel.revalidate();
                MainPanel.repaint();
                lblPath.setText("Home / Kelola Gaji");
                Gaji gaji = new Gaji();
                gaji.Gaji.setVisible(true);
                MainPanel.setLayout(new BorderLayout());
                MainPanel.add(gaji.Gaji);
            }
        });
    }
}
