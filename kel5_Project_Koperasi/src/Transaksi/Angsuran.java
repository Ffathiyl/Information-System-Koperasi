package Transaksi;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Angsuran extends JFrame{
    public JPanel Angsuran;
    private JTable tableAngsuran;
    private JTable tableTemp;
    private JLabel lblTanggal;
    private JLabel lblAdmin;
    private JComboBox cbAnggota;
    private JLabel lblPinjaman;
    private JComboBox cbDenda;
    private JLabel lblID;
    private JButton simpanButton;
    private JButton batalButton;
    private JLabel lblJmlAngsuran;
    private JTextField txtTotal;
    private JTextField txtBayar;
    private JLabel lblDenda;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();
    private DefaultTableModel model1 = new DefaultTableModel();
    private long bulan,sisabulan,minggu,sisaminggu,hari;
    private String idDenda[] = new String[3];
    private long qtyDenda[] = new long[3];
    private List<String> IdDenda = new ArrayList<>();
    private List<Long> QtyDenda = new ArrayList<>();
    private String waktu = "";

    public Angsuran(String data,String today){
        setContentPane(Angsuran);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        model1 = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableAngsuran.setModel(model);
        tableTemp.setModel(model1);
        lblAdmin.setText(data);
        lblTanggal.setText(today);
        autokode();
        showMember();
        cbAnggota.setSelectedIndex(-1);
        addColumnTemp();
        addColumnTrs();
        //loadDataTemp();
        loadDataTrs();
        lblPinjaman.setText("");
        lblJmlAngsuran.setText("");
        lblDenda.setText("");
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        cbAnggota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataTemp();
            }
        });
        tableTemp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableTemp.getSelectedRow();
                if(i == -1){
                    return;
                }
                waktu = model1.getValueAt(i,5).toString();
                IdDenda.clear();
                QtyDenda.clear();
                long selisih = 0;
                String tanggal = model1.getValueAt(i,5).toString();
                String Denda = lblJmlAngsuran.getText();
                String angka = Denda.replace("Rp. ","");
                angka = angka.replace(",","");
                long maks = Long.parseLong(angka) / 100;
                String angsuran = String.valueOf(maks);
                double sehari = 0.0, seminggu = 0.0, sebulan = 0.0;
                System.out.println(tanggal+" <<< Deadline");
                int row = 0;
                try {

                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT * FROM tbDenda";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        if (connection.result.getString("Deskripsi").equals("Telat Sehari")){
                            sehari = connection.result.getDouble("JumlahDenda");
                            idDenda[0] = connection.result.getString("IdDenda");
                        } else if (connection.result.getString("Deskripsi").equals("Telat Seminggu")){
                            seminggu = connection.result.getDouble("JumlahDenda");
                            idDenda[1] = connection.result.getString("IdDenda");
                        } else if (connection.result.getString("Deskripsi").equals("Telat Sebulan")){
                            sebulan = connection.result.getDouble("JumlahDenda");
                            idDenda[2] = connection.result.getString("IdDenda");
                        }
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(waktu);
                    LocalDate tglTable = LocalDate.of(date.getYear()+1900,date.getMonth() - 3,date.getDate());
                    System.out.println(waktu+" <<< tglTable");
                    LocalDate today = LocalDate.now();
                    System.out.println(today+" <<< Today");

                    selisih = ChronoUnit.DAYS.between(today,tglTable);
                    System.out.println(selisih+" <<< selisih berapa hari");
                    if (selisih < 0){
                        JOptionPane.showMessageDialog(null,"Telat Selama "+selisih+" Hari");
                        long pselisih = -selisih;
                        bulan = pselisih / 30;
                        sisabulan = pselisih % 30;
                        minggu = sisabulan / 7;
                        sisaminggu = sisabulan % 7;
                        hari = sisaminggu / 1;

                        qtyDenda[0] = bulan;
                        qtyDenda[1] = minggu;
                        qtyDenda[2] = hari;

                        if (bulan > 0){
                            IdDenda.add(idDenda[2]);;
                            QtyDenda.add(qtyDenda[0]);
                        }
                        if (minggu > 0){
                            IdDenda.add(idDenda[1]);
                            QtyDenda.add(qtyDenda[1]);
                        }
                        if (hari > 0){
                            IdDenda.add(idDenda[0]);
                            QtyDenda.add(qtyDenda[2]);
                        }

                        for (int ik = 0 ; ik < IdDenda.size();ik++){
                            System.out.println(IdDenda.get(ik)+" <<< IdDenda");
                        }
                        for (int ik = 0 ; ik < QtyDenda.size();ik++){
                            System.out.println(QtyDenda.get(ik)+" <<< QtyDenda");
                        }

                        DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                        formatRp.setCurrencySymbol("Rp. ");
                        formatRp.setMonetaryDecimalSeparator(',');
                        formatRp.setGroupingSeparator('.');
                        Rupiah.setDecimalFormatSymbols(formatRp);

                        double denda = Double.valueOf(bulan * sebulan) + Double.valueOf(minggu * seminggu) + Double.valueOf(hari * sehari);
                        String ppp = Rupiah.format(denda);
                        lblDenda.setText(String.valueOf(ppp));
                        double totalangsuran = denda + Double.parseDouble(angsuran);
                        String Total = Rupiah.format(totalangsuran);
                        txtTotal.setText(String.valueOf(Total));

                        System.out.println("Telat selama "+bulan+" bulan, "+minggu+" minggu dan "+hari+" hari");
                    } else {
                        JOptionPane.showMessageDialog(null,"Tidak telat");
                        DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                        formatRp.setCurrencySymbol("Rp. ");
                        formatRp.setMonetaryDecimalSeparator(',');
                        formatRp.setGroupingSeparator('.');
                        Rupiah.setDecimalFormatSymbols(formatRp);
                        txtTotal.setText("");
                        lblDenda.setText("");
                        double totalangsuran = Double.parseDouble(angsuran);
                        String Total = Rupiah.format(totalangsuran);
                        txtTotal.setText(Total);
                    }
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }
            }
        });

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                String IdAnggota = "";
                String IdAdmin = "";

                //TextField Total
                String harga = txtTotal.getText();
                String harga_ribuan = harga;
                harga = harga_ribuan.replace(",","");
                harga = harga.replace("Rp. ","");
                long Harga = Long.valueOf(harga)/100;
                harga = String.valueOf(Harga);

                //TextField Angsuran
                String angsuran = lblJmlAngsuran.getText();
                String angsuran_ribuan = angsuran;
                angsuran = angsuran_ribuan.replace(",","");
                angsuran = angsuran.replace("Rp. ","");
                long Angsuran = Long.valueOf(angsuran)/100;
                angsuran = String.valueOf(Angsuran);

                //TextField Denda
                String denda = "";
                if (!lblDenda.getText().isEmpty()) {
                    denda = lblDenda.getText();
                    String denda_ribuan = denda;
                    denda = denda_ribuan.replace(",", "");
                    denda = denda.replace("Rp. ", "");
                    long Denda = Long.valueOf(denda) / 100;
                    denda = String.valueOf(Denda);
                }

                try{
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(waktu);
                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    waktu = format.format(date.getTime());

                    connection.stat = connection.conn.createStatement();
                    String syntaxx = "SELECT IdAnggota FROM tbMember WHERE NamaAnggota='" + cbAnggota.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntaxx);
                    while (connection.result.next()) {
                        IdAnggota = (String) connection.result.getString("IdAnggota");
                    }

                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdAdmin FROM tbAdmin WHERE Nama='" + lblAdmin.getText() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        IdAdmin = (String) connection.result.getString("IdAdmin");
                    }

                    String query = "INSERT INTO tbTrsAngsuran VALUES (?,?,?,?,?,?,?,?,?)";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,lblID.getText());
                    connection.pstat.setString(2,IdAnggota);
                    connection.pstat.setString(3,lblPinjaman.getText());
                    connection.pstat.setString(4,today);
                    connection.pstat.setString(5,angsuran);
                    if (!lblDenda.getText().isEmpty()){
                    connection.pstat.setString(6,denda);
                    } else {
                        connection.pstat.setDouble(6,0.0);
                    }
                    connection.pstat.setString(7,harga);
                    connection.pstat.setString(8,IdAdmin);
                    connection.pstat.setInt(9,1);
                    connection.pstat.executeUpdate();
                    for (int i = 0; i < IdDenda.size();i++){
                        String detail = "INSERT INTO tbDetailAngsuran VALUES (?,?,?)";
                        connection.pstat = connection.conn.prepareStatement(detail);
                        connection.pstat.setString(1,lblID.getText());
                        connection.pstat.setString(2,IdDenda.get(i));
                        connection.pstat.setString(3,String.valueOf(QtyDenda.get(i)));
                        connection.pstat.executeUpdate();
                    }
                    String update = "UPDATE tbAngsuran SET Status = 1 WHERE IdTrsPinjaman = ? AND " +
                            "IdAnggota = ? AND BatasPengembalian = ?";
                    connection.pstat = connection.conn.prepareStatement(update);
                    connection.pstat.setString(1,lblPinjaman.getText());
                    connection.pstat.setString(2,IdAnggota);
                    connection.pstat.setString(3,waktu);
                    connection.pstat.executeUpdate();
                    String Status = "SELECT dbo.JmlAngsuran ('"+IdAnggota+"', '"+lblPinjaman.getText()+"') as jml";
                    connection.result = connection.stat.executeQuery(Status);
                    int p = 0;
                    while (connection.result.next()){
                        p = connection.result.getInt("jml");
                        break;
                    }
                    if (p == 0){
                        String quer = "UPDATE tbTrsPinjaman SET Status = 1 WHERE IdAnggota = ? AND IdTrsPinjaman = ?";
                        connection.pstat = connection.conn.prepareStatement(quer);
                        connection.pstat.setString(1,IdAnggota);
                        connection.pstat.setString(2,lblPinjaman.getText());
                        connection.pstat.executeUpdate();
                    }
                    System.out.println(p+" <<< jml di tbAngsur");
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Transaksi Angsuran Berhasil");
                    loadDataTrs();
                    Clear();
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Gagal Input Transaksi Angsuran "+ex);
                }
            }
        });
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbTrsAngsuran ORDER BY IdTrsAngsuran desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdTrsAngsuran").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                lblID.setText("ANG" + nol + AN);
                cbAnggota.requestFocus();

            } else {
                lblID.setText("ANG001");
                cbAnggota.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Transaksi Angsuran: " + e1);
        }
    }

    public void showMember() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdAnggota, NamaAnggota, Status FROM tbMember";
            connection.result = connection.stat.executeQuery(sql);

            while (connection.result.next()) {
                if (connection.result.getString("Status").equals("1"))
                    cbAnggota.addItem(connection.result.getString("NamaAnggota"));
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException e) {
            System.out.println("Terjadi error saat load data Anggota " + e);
        }
    }

    public void loadDataTemp() {
        String IdPinjaman = "";
        String Angsuran = "";
        model1.getDataVector().removeAllElements();
        model1.fireTableDataChanged();

        try {

            connection.stat = connection.conn.createStatement();
            String syntax = "SELECT t.IdTrsPinjaman,m.NamaAnggota,k.NamaKategori,a.Nama,t.TanggalTransaksi,\n" +
                    "\t\t\tt.JumlahPinjaman,t.JangkaWaktu,t.BatasPengembalian,t.Status\n" +
                    "FROM tbTrsPinjaman t\n" +
                    "JOIN tbMember m ON t.IdAnggota = m.IdAnggota\n" +
                    "JOIN tbAdmin a ON t.IdAdmin = a.IdAdmin\n" +
                    "JOIN tbKategoriPinjaman k ON t.IdKategori = k.IdKategori " +
                    "WHERE m.NamaAnggota = '" + cbAnggota.getSelectedItem() + "' AND t.Status = 0";
            connection.result = connection.stat.executeQuery(syntax);
            while (connection.result.next()) {
                IdPinjaman = (String) connection.result.getString("IdTrsPinjaman");
                break;
            }

            connection.stat = connection.conn.createStatement();
            String query = "SELECT p.IdTrsPinjaman,tm.NamaAnggota,k.NamaKategori,ta.Nama,a.TanggalTransaksi," +
                    "a.BatasPengembalian,p.TotalPinjaman,a.Angsuran,a.Status " +
                    "FROM tbAngsuran a " +
                    "JOIN tbTrsPinjaman p ON a.IdTrsPinjaman = p.IdTrsPinjaman " +
                    "JOIN tbAdmin ta ON a.IdAdmin = ta.IdAdmin " +
                    "JOIN tbMember tm ON a.IdAnggota = tm.IdAnggota " +
                    "JOIN tbKategoriPinjaman k ON a.IdKategori = k.IdKategori " +
                    "WHERE tm.NamaAnggota = '" + cbAnggota.getSelectedItem() + "' AND p.IdTrsPinjaman = '" + IdPinjaman + "' AND a.Status = 0";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableTemp.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
            tableTemp.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

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
                obj[5] = BatasWaktu;
                obj[6] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
                obj[7] = Rupiah.format(connection.result.getInt("Angsuran"));
                Angsuran = Rupiah.format(connection.result.getInt("Angsuran"));
                if (connection.result.getString("Status").equals("1")){
                    obj[8] = "Lunas";
                } else {
                    obj[8] = "Belum Lunas";
                }
                model1.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception ex) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + ex);
        }
        lblPinjaman.setText(IdPinjaman);
        lblJmlAngsuran.setText(Angsuran);
    }

    public void loadDataTrs() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT trs.IdTrsAngsuran, tm.NamaAnggota, trp.IdTrsPinjaman, trs.TanggalTransaksi," +
                    "trs.Angsuran, trs.Denda, Trs.TotalAngsuran, ta.Nama, trs.Status\n" +
                    "FROM tbTrsAngsuran trs " +
                    "JOIN tbMember tm ON trs.IdAnggota = tm.IdAnggota " +
                    "JOIN tbAdmin ta ON trs.IdAdmin = ta.IdAdmin " +
                    "JOIN tbTrsPinjaman trp ON trp.IdTrsPinjaman = trs.IdTrsPinjaman";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableAngsuran.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
            tableAngsuran.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
            tableAngsuran.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdTrsAngsuran");
                obj[1] = connection.result.getString("NamaAnggota");
                obj[2] = connection.result.getString("IdTrsPinjaman");
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
                obj[4] = Rupiah.format(connection.result.getInt("Angsuran"));
                obj[5] = Rupiah.format(connection.result.getInt("Denda"));
                obj[6] = Rupiah.format(connection.result.getInt("TotalAngsuran"));
                obj[7] = connection.result.getString("Nama");
                if (connection.result.getString("Status").equals("1")){
                    obj[8] = "Lunas";
                } else {
                    obj[8] = "Belum Lunas";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Transaksi Simpanan : " + e);
        }
    }

    public void addColumnTemp() {
        model1.addColumn("ID Pinjaman");
        model1.addColumn("Nama Anggota");
        model1.addColumn("Nama Kategori");
        model1.addColumn("Nama Admin");
        model1.addColumn("Tanggal Transaksi");
        model1.addColumn("Batas Pengembalian");
        model1.addColumn("Total Pinjaman");
        model1.addColumn("Angsuran");
        model1.addColumn("Status");
    }

    public void addColumnTrs() {
        model.addColumn("ID Transaksi");
        model.addColumn("Nama Anggota");
        model.addColumn("ID Pinjaman");
        model.addColumn("Tanggal Transaksi");
        model.addColumn("Angsuran");
        model.addColumn("Denda");
        model.addColumn("Total Angsuran");
        model.addColumn("Nama Admin");
        model.addColumn("Status");
    }

    public void Clear(){
        autokode();
        lblPinjaman.setText("");
        lblJmlAngsuran.setText("");
        txtTotal.setText("");
        lblDenda.setText("");
        cbAnggota.setSelectedIndex(-1);
    }

    public boolean Validasi(){
        if (cbAnggota.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Anggota Belum Dipilih");
            return false;
        }
        if (txtTotal.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Pilih angsuran terlebih dahulu");
            return false;
        }
        return true;
    }
}
