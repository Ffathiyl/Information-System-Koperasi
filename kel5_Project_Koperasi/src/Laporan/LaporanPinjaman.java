package Laporan;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LaporanPinjaman extends JFrame{
    public JPanel LaporanPinjaman;
    private JTable tableLaporanPinjaman;
    private JComboBox cbFilter;
    private JComboBox cbSubFIlter;
    private JButton cariButton;
    private JLabel lblJml;
    private JButton semuaButton;
    DBConnect connection = new DBConnect();
    DefaultTableModel model = new DefaultTableModel();
    public LaporanPinjaman(){
        model = new DefaultTableModel();
        tableLaporanPinjaman.setModel(model);
        addColumn();
        cbFilter.setSelectedIndex(0);
        cbSubFIlter.setSelectedIndex(0);
        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long total = 0;

                if (cbFilter.getSelectedItem()==null){
                    JOptionPane.showMessageDialog(null,"Data Tidak Ditemukan!");
                    return;
                }
                if (cbSubFIlter.getSelectedItem()==null){
                    JOptionPane.showMessageDialog(null,"Data Tidak Ditemukan!");
                    return;
                }
                loadDataSpec();
                try{
                    int j = tableLaporanPinjaman.getModel().getRowCount();
                    for (int i = 0; i < j; i++){
                        System.out.println("DADADADAH "+i);
                        String angka = model.getValueAt(i,7).toString();
                        String format = angka.replace("Rp. ","");
                        angka = format.replace(",","");
                        long maks = Long.parseLong(angka) / 100;
                        total = total + maks;
                    }
                    try{
                        String sbyr = String.valueOf(total);
                        double dblByr = Double.parseDouble(sbyr);
                        DecimalFormat df = new DecimalFormat("#,###,###");
                        if (dblByr > 999) {
                            lblJml.setText(df.format(dblByr));
                        } else {
                            lblJml.setText(sbyr);
                        }
                    }catch (Exception ex){

                    }
                    //lblJml.setText();
                } catch (Exception ex){

                }
            }
        });
        semuaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long total = 0;
                loadData();
                try{
                    int j = tableLaporanPinjaman.getModel().getRowCount();
                    for (int i = 0; i < j; i++){
                        System.out.println("DADADADAH "+i);
                        String angka = model.getValueAt(i,4).toString();
                        String format = angka.replace("Rp. ","");
                        angka = format.replace(",","");
                        long maks = Long.parseLong(angka) / 100;
                        total = total + maks;
                    }
                    try{
                        String sbyr = String.valueOf(total);
                        double dblByr = Double.parseDouble(sbyr);
                        DecimalFormat df = new DecimalFormat("#,###,###");
                        if (dblByr > 999) {
                            lblJml.setText(df.format(dblByr));
                        } else {
                            lblJml.setText(sbyr);
                        }
                    }catch (Exception ex){

                    }
                    //lblJml.setText();
                } catch (Exception ex){

                }
                cbFilter.setSelectedIndex(0);
                cbSubFIlter.setSelectedIndex(0);
            }
        });
    }

    public void addColumn() {
        model.addColumn("ID Transaksi");
        model.addColumn("Nama Anggota");
        model.addColumn("Nama Kategori");
        model.addColumn("Nama Admin");
        model.addColumn("Tanggal Transaksi");
        model.addColumn("Jumlah Pinjaman");
        model.addColumn("Jangka Waktu");
        model.addColumn("Total Pinjaman");
        model.addColumn("Batas Pengembalian");
        model.addColumn("Status");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,a.Nama,t.TanggalTransaksi, " +
                    "t.JumlahPinjaman,t.JangkaWaktu,t.TotalPinjaman,t.BatasPengembalian,t.Status " +
                    "FROM tbTrsPinjaman t " +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota " +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin " +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsPinjaman");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("NamaKategori");
                obj[3] = connection.result.getString("Nama");
                String TanggalTransaksi = connection.result.getString("TanggalTransaksi");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(TanggalTransaksi);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    TanggalTransaksi = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[4] = TanggalTransaksi;
                obj[5] = connection.result.getInt("JumlahPinjaman");

                String BatasWaktu = connection.result.getString("BatasPengembalian");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(BatasWaktu);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    BatasWaktu = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[6] = connection.result.getString("JangkaWaktu");
                obj[7] = connection.result.getInt("TotalPinjaman");
                obj[8] = BatasWaktu;
                if (connection.result.getString("Status").equals("1")){
                    obj[9] = "Lunas";
                } else {
                    obj[9] = "Belum Lunas";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }

    public void loadDataSpec() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,a.Nama,t.TanggalTransaksi, " +
                    "t.JumlahPinjaman,t.JangkaWaktu,t.TotalPinjaman,t.BatasPengembalian,t.Status " +
                    "FROM tbTrsPinjaman t " +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota " +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin " +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori " +
                    "WHERE DATENAME(MONTH, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"' " +
                    "AND DATENAME(YEAR, t.TanggalTransaksi) = '"+ cbFilter.getSelectedItem()+"'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsPinjaman");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("NamaKategori");
                obj[3] = connection.result.getString("Nama");
                String TanggalTransaksi = connection.result.getString("TanggalTransaksi");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(TanggalTransaksi);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    TanggalTransaksi = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[4] = TanggalTransaksi;
                obj[5] = connection.result.getInt("JumlahPinjaman");

                String BatasWaktu = connection.result.getString("BatasPengembalian");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(BatasWaktu);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    BatasWaktu = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[6] = connection.result.getString("JangkaWaktu");
                obj[7] = connection.result.getInt("TotalPinjaman");
                obj[8] = BatasWaktu;
                if (connection.result.getString("Status").equals("1")){
                    obj[9] = "Lunas";
                } else {
                    obj[9] = "Belum Lunas";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }

    public void loadDataTahun() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,a.Nama,t.TanggalTransaksi, " +
                    "t.JumlahPinjaman,t.JangkaWaktu,t.TotalPinjaman,t.BatasPengembalian,t.Status " +
                    "FROM tbTrsPinjaman t " +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota " +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin " +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori " +
                    "WHERE DATENAME(YEAR, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsPinjaman");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("NamaKategori");
                obj[3] = connection.result.getString("Nama");
                String TanggalTransaksi = connection.result.getString("TanggalTransaksi");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(TanggalTransaksi);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    TanggalTransaksi = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[4] = TanggalTransaksi;
                obj[5] = connection.result.getInt("JumlahPinjaman");

                String BatasWaktu = connection.result.getString("BatasPengembalian");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(BatasWaktu);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    Date datee = calendar.getTime();
                    BatasWaktu = formatter.format(datee);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[6] = connection.result.getString("JangkaWaktu");
                obj[7] = connection.result.getInt("TotalPinjaman");
                obj[8] = BatasWaktu;
                if (connection.result.getString("Status").equals("1")){
                    obj[9] = "Lunas";
                } else {
                    obj[9] = "Belum Lunas";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }
}
