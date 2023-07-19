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

public class LaporanSimpanan extends JFrame{
    public JPanel LaporanSImpanan;
    private JTable tableLaporanSimpanan;
    private JComboBox cbFilter;
    private JComboBox cbSubFIlter;
    private JButton cariButton;
    private JLabel lblJml;
    private JButton semuaButton;
    private JButton cetakButton;
    private JPanel JpanelDari;
    private JPanel JpanelSampai;
    private DefaultTableModel model = new DefaultTableModel();
    DBConnect connection = new DBConnect();
    JDateChooser dateAwal = new JDateChooser();
    JDateChooser dateAkhir = new JDateChooser();
    Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public LaporanSimpanan(){
    model = new DefaultTableModel();
    JpanelDari.add(dateAwal);
    JpanelSampai.add(dateAkhir);
    tableLaporanSimpanan.setModel(model);
    lblJml.setText("0");
    addColumn();
        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long total = 0;
                if (dateAwal.getDate() == null || dateAkhir.getDate() == null){
                    JOptionPane.showMessageDialog(LaporanSImpanan,"Isi tanggal terlebih dahulu!");
                    return;
                }
                loadDataSpec();
                int k = tableLaporanSimpanan.getRowCount();
                if (k == 0){
                    JOptionPane.showMessageDialog(LaporanSImpanan,"Data tidak Ditemukan");
                    return;
                }
                try{
                    int j = tableLaporanSimpanan.getModel().getRowCount();
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
                    int j = tableLaporanSimpanan.getModel().getRowCount();
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
                int i = tableLaporanSimpanan.getRowCount();
                if (i == 0){
                    JOptionPane.showMessageDialog(null,"Data Laporan Kosong");
                    return;
                }
                try{
                    HashMap param = new HashMap();

                    JRDataSource dataSource = new JRTableModelDataSource(tableLaporanSimpanan.getModel());
                    JasperDesign jd = JRXmlLoader.load("C:\\College\\SEMESTER 2\\Project\\kel5_Project_Koperasi\\src\\LaporanSimpanan\\LaporanSimpanan.jrxml");
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
        model.addColumn("Jenis Simpanan");
        model.addColumn("Nama Anggota");
        model.addColumn("Tanggal Transaksi");
        model.addColumn("Jumlah Simpanan");
    }

    public void loadDataSpec() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        String tanggalAwal = formatter.format(dateAwal.getDate());
        String tanggalAkhir = formatter.format(dateAkhir.getDate());

        try {
            DBConnect connection = new DBConnect();
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan " +
                    "FROM tbTrsSimpanan t " +
                    "JOIN tbMember ON t.IdAnggota = tbMember.IdAnggota " +
                    "JOIN tbJenisSimpanan ON t.IdJenisSimpanan = tbJenisSimpanan.IdJenisSimpanan " +
                    "JOIN tbAdmin ON t.IdAdmin = tbAdmin.IdAdmin " +
                    "WHERE t.TanggalTransaksi BETWEEN ? AND ?";
                    /*"WHERE DATENAME(MONTH, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"' " +
                    "AND DATENAME(YEAR, t.TanggalTransaksi) = '"+cbFilter.getSelectedItem()+"'";*/
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1,tanggalAwal);
            connection.pstat.setString(2,tanggalAkhir);
            connection.result = connection.pstat.executeQuery();

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableLaporanSimpanan.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsSimpanan");
                obj[1] = connection.result.getString("NamaJenis");
                obj[2] = connection.result.getString("NamaAnggota");
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
                obj[4] = Rupiah.format(connection.result.getInt("JumlahSimpanan"));
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
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan " +
                    "FROM tbTrsSimpanan t " +
                    "JOIN tbMember ON t.IdAnggota = tbMember.IdAnggota " +
                    "JOIN tbJenisSimpanan ON t.IdJenisSimpanan = tbJenisSimpanan.IdJenisSimpanan " +
                    "JOIN tbAdmin ON t.IdAdmin = tbAdmin.IdAdmin " +
                    "WHERE DATENAME(YEAR, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"'";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableLaporanSimpanan.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsSimpanan");
                obj[1] = connection.result.getString("NamaJenis");
                obj[2] = connection.result.getString("NamaAnggota");
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
                obj[4] = Rupiah.format(connection.result.getInt("JumlahSimpanan"));
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        //(t.JumlahSimpanan+(t.JumlahSimpanan*tbJenisSimpanan.Bunga/100))as Total

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan " +
                    "FROM tbTrsSimpanan t " +
                    "JOIN tbMember ON t.IdAnggota = tbMember.IdAnggota " +
                    "JOIN tbJenisSimpanan ON t.IdJenisSimpanan = tbJenisSimpanan.IdJenisSimpanan " +
                    "JOIN tbAdmin ON t.IdAdmin = tbAdmin.IdAdmin ";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableLaporanSimpanan.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsSimpanan");
                obj[1] = connection.result.getString("NamaJenis");
                obj[2] = connection.result.getString("NamaAnggota");
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
                obj[4] = Rupiah.format(connection.result.getInt("JumlahSimpanan"));
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }
}
