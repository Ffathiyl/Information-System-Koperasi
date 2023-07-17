package Laporan;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LaporanSimpanan extends JFrame{
    public JPanel LaporanSImpanan;
    private JTable tableLaporanSimpanan;
    private JComboBox cbFilter;
    private JComboBox cbSubFIlter;
    private JButton cariButton;
    private JLabel lblJml;
    private JButton semuaButton;
    private DefaultTableModel model = new DefaultTableModel();
    DBConnect connection = new DBConnect();
    public LaporanSimpanan(){
    model = new DefaultTableModel();
    tableLaporanSimpanan.setModel(model);
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
                cbFilter.setSelectedIndex(0);
                cbSubFIlter.setSelectedIndex(0);
            }
        });
    }

    public void addColumn() {
        model.addColumn("ID Transaksi");
        model.addColumn("Jenis Simpanan");
        model.addColumn("Nama Anggota");
        model.addColumn("Tanggal Transaksi");
        model.addColumn("Jumlah Simpanan");
        model.addColumn("Nama Admin");
        model.addColumn("Lama Simpanan");
        model.addColumn("Status");
    }

    public void loadDataSpec() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan,tbAdmin.Nama,t.LamaSimpanan, t.Status " +
                    "FROM tbTrsSimpanan t " +
                    "JOIN tbMember ON t.IdAnggota = tbMember.IdAnggota " +
                    "JOIN tbJenisSimpanan ON t.IdJenisSimpanan = tbJenisSimpanan.IdJenisSimpanan " +
                    "JOIN tbAdmin ON t.IdAdmin = tbAdmin.IdAdmin " +
                    "WHERE DATENAME(MONTH, t.TanggalTransaksi) = '" + cbSubFIlter.getSelectedItem()+"' " +
                    "AND DATENAME(YEAR, t.TanggalTransaksi) = '"+cbFilter.getSelectedItem()+"'";
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
                obj[5] = connection.result.getString("Nama");
                obj[6] = connection.result.getInt("LamaSimpanan");
                if (connection.result.getString("Status").equals("1")){
                    obj[7] = "Aktif";
                } else {
                    obj[7] = "Tidak Aktif";
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
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan,tbAdmin.Nama,t.LamaSimpanan, t.Status " +
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
                obj[5] = connection.result.getString("Nama");
                obj[6] = connection.result.getInt("LamaSimpanan");
                if (connection.result.getString("Status").equals("1")){
                    obj[7] = "Aktif";
                } else {
                    obj[7] = "Tidak Aktif";
                }
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

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT t.IdTrsSimpanan, tbJenisSimpanan.NamaJenis,tbMember.NamaAnggota,t.TanggalTransaksi,t.JumlahSimpanan,tbAdmin.Nama,t.LamaSimpanan, t.Status " +
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
                obj[5] = connection.result.getString("Nama");
                obj[6] = connection.result.getInt("LamaSimpanan");
                if (connection.result.getString("Status").equals("1")){
                    obj[7] = "Aktif";
                } else {
                    obj[7] = "Tidak Aktif";
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
