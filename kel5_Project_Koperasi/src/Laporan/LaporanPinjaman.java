package Laporan;

import DBConnection.DBConnect;
import com.toedter.calendar.JDateChooser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LaporanPinjaman extends JFrame{
    public JPanel LaporanPinjaman;
    private JTable tableLaporanPinjaman;
    private JComboBox cbFilter;
    private JComboBox cbSubFIlter;
    private JButton cariButton;
    private JLabel lblJml;
    private JButton semuaButton;
    private JButton cetakButton;
    private JPanel PanelAwal;
    private JPanel PanelAkhir;
    DBConnect connection = new DBConnect();
    DefaultTableModel model = new DefaultTableModel();
    JDateChooser dateAwal = new JDateChooser();
    JDateChooser dateAkhir = new JDateChooser();
    Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public LaporanPinjaman(){
        model = new DefaultTableModel();
        PanelAwal.add(dateAwal);
        PanelAkhir.add(dateAkhir);
        tableLaporanPinjaman.setModel(model);
        addColumn();
        lblJml.setText("0");
        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long total = 0;
                if (dateAwal.getDate() == null || dateAkhir.getDate() == null){
                    JOptionPane.showMessageDialog(null,"Isi tanggal terlebih dahulu!");
                    return;
                }
                loadDataSpec();
                int k = tableLaporanPinjaman.getRowCount();
                if (k == 0){
                    JOptionPane.showMessageDialog(LaporanPinjaman,"Data tidak Ditemukan");
                    return;
                }
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
            }
        });
        cetakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = tableLaporanPinjaman.getRowCount();
                if (i == 0){
                    JOptionPane.showMessageDialog(null,"Data Laporan Kosong");
                    return;
                }
                try{
                    HashMap param = new HashMap();

                    JRDataSource dataSource = new JRTableModelDataSource(tableLaporanPinjaman.getModel());
                    JasperDesign jd = JRXmlLoader.load("C:\\College\\SEMESTER 2\\Project\\kel5_Project_Koperasi\\src\\LaporanPinjaman\\LaporanPinjaman.jrxml");
                    JasperReport jr = JasperCompileManager.compileReport(jd);
                    JasperPrint jp = JasperFillManager.fillReport(jr,param,dataSource);
                    JasperViewer viewer = new JasperViewer(jp,false);
                    viewer.setVisible(true);
                    viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                } catch (Exception ex){
                    System.out.println("gagal "+ex);
                }
            }
        });
    }

    public void addColumn() {
        model.addColumn("ID Transaksi");
        model.addColumn("Nama Anggota");
        model.addColumn("Nama Kategori");
        model.addColumn("Tanggal Transaksi");
        model.addColumn("Total Pinjaman");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,t.TanggalTransaksi, " +
                    "t.TotalPinjaman " +
                    "FROM tbTrsPinjaman t " +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota " +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin " +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori ";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableLaporanPinjaman.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
            //tableLaporanPinjaman.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsPinjaman");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("NamaKategori");
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

                obj[3] = TanggalTransaksi;
                obj[4] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
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

        String tanggalAwal = formatter.format(dateAwal.getDate());
        String tanggalAkhir = formatter.format(dateAkhir.getDate());

        try {
            DBConnect connection = new DBConnect();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,t.TanggalTransaksi, " +
                    "t.TotalPinjaman " +
                    "FROM tbTrsPinjaman t " +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota " +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin " +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori " +
                    "WHERE t.TanggalTransaksi BETWEEN ? AND ?";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1,tanggalAwal);
            connection.pstat.setString(2,tanggalAkhir);
                    /*"WHERE DATENAME(MONTH, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"' " +
                    "AND DATENAME(YEAR, t.TanggalTransaksi) = '"+ cbFilter.getSelectedItem()+"'";*/
            connection.result = connection.pstat.executeQuery();

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableLaporanPinjaman.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
            //tableLaporanPinjaman.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsPinjaman");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("NamaKategori");
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

                obj[3] = TanggalTransaksi;
                obj[4] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan Spesifik : " + e);
        }
    }

    public void loadDataTahun() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,t.TanggalTransaksi, " +
                    "t.TotalPinjaman, " +
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
