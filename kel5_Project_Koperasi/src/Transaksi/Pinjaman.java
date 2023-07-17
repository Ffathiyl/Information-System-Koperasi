package Transaksi;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Pinjaman extends JFrame{
    public JPanel Pinjaman;
    private JTextField txtID;
    private JTextField txtBunga;
    private JTextField txtJumlah;
    private JTextField txtWaktu;
    private JTable tablePinjaman;
    private JButton batalButton;
    private JButton simpanButton;
    private JComboBox cbKategori;
    private JComboBox cbAnggota;
    private JLabel lblAdmin;
    private JLabel lblTanggal;
    private JLabel lblMax;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();
    private int p = 0;

    public Pinjaman(String data,String today){
        setContentPane(Pinjaman);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tablePinjaman.setModel(model);
        lblAdmin.setText(data);
        lblTanggal.setText(today);
        showKategori();
        showMember();
        addColumn();
        autokode();
        loadData();
        cbAnggota.setSelectedIndex(-1);
        cbKategori.setSelectedIndex(-1);
        cbKategori.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Bunga = "";
                try {
                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdKategori,Bunga,NamaKategori FROM tbKategoriPinjaman WHERE NamaKategori = '" + cbKategori.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        Bunga = (String) connection.result.getString("Bunga");
                        txtBunga.setText(Bunga);
                    }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data Bunga " + ex.getMessage());
                }
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Validasi()){
                    return;
                }
                if (txtWaktu.getText().equals("0")){
                    JOptionPane.showMessageDialog(null,"Lama Pinjaman Tidak boleh 0!");
                    txtWaktu.requestFocus();
                    return;
                }
                //double max = Double.parseDouble(lblMax.getText());
                String max = lblMax.getText();
                String maks = max.replace("Rp. ","");
                maks = maks.replace(",","");
                String harga = txtJumlah.getText();
                String harga_ribuan = harga;
                harga =harga_ribuan.replace(",","");
                long Max = Long.parseLong(maks) / 100;
                long jml = Long.parseLong(harga);

                if (jml > Max){
                    JOptionPane.showMessageDialog(null,"Jumlah pinjaman melebihi batas maksimal!");
                    txtJumlah.requestFocus();
                    return;
                }
                String IdAnggota = "";
                String IdKategori = "";
                String IdAdmin = "";
                String formattedDate = "";
                String formattedDatee = "";

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, Integer.parseInt(txtWaktu.getText())); // Menambahkan bulan ke tanggal saat ini
                Date current = calendar.getTime();
                formattedDate = formatter.format(current);
                formattedDate = formatter.format(current);

                double TotalPinjaman = Double.valueOf(jml) + (Double.valueOf(jml) * Double.parseDouble(txtBunga.getText())/100);
                double Angsuran = TotalPinjaman / Double.parseDouble(txtWaktu.getText());
                System.out.println(Angsuran+" <<<<< Jumlah Angsuran ");

                try {
                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdAnggota FROM tbMember WHERE NamaAnggota='" + cbAnggota.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        IdAnggota = (String) connection.result.getString("IdAnggota");
                    }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data anggota " + ex.getMessage());
                }

                try {
                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdKategori,NamaKategori FROM tbKategoriPinjaman WHERE NamaKategori ='" + cbKategori.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        IdKategori = (String) connection.result.getString("IdKategori");
                    }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data Kategori " + ex.getMessage());
                }

                try {
                        connection.stat = connection.conn.createStatement();
                        String syntax = "SELECT IdAdmin FROM tbAdmin WHERE Nama='" + lblAdmin.getText() + "'";
                        connection.result = connection.stat.executeQuery(syntax);
                        while (connection.result.next()) {
                            IdAdmin = (String) connection.result.getString("IdAdmin");
                        }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data Admin " + ex.getMessage());
                }

                try{
                    String query = "INSERT INTO tbTrsPinjaman VALUES (?,?,?,?,?,?,?,?,?,?)";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,IdAnggota);
                    connection.pstat.setString(3,IdKategori);
                    connection.pstat.setString(4,IdAdmin);
                    connection.pstat.setString(5,today);
                    connection.pstat.setString(6,harga);
                    connection.pstat.setString(7,txtWaktu.getText());
                    connection.pstat.setDouble(8,TotalPinjaman);
                    connection.pstat.setString(9,formattedDate);
                    connection.pstat.setInt(10,0);
                    connection.pstat.executeUpdate();
                    //Memasukkan ke dalam table sementara dan menambahkan bulannya
                    for (int i = 0; i < Integer.parseInt(txtWaktu.getText());i++){
                        SimpleDateFormat formatterr = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendarr = Calendar.getInstance();
                        calendarr.add(Calendar.MONTH, i+1); // Menambahkan tiap 1 bulan ke tanggal saat ini
                        Date currentt = calendarr.getTime();
                        formattedDatee = formatterr.format(currentt);
                        String detail = "INSERT INTO tbAngsuran VALUES (?,?,?,?,?,?,?,?,?,?)";
                        connection.pstat = connection.conn.prepareStatement(detail);
                        connection.pstat.setString(1,txtID.getText());
                        connection.pstat.setString(2,IdAnggota);
                        connection.pstat.setString(3,IdKategori);
                        connection.pstat.setString(4,IdAdmin);
                        connection.pstat.setString(5,today);
                        connection.pstat.setString(6,harga);
                        connection.pstat.setInt(7,Integer.parseInt(txtWaktu.getText()));
                        connection.pstat.setString(8,formattedDatee);
                        connection.pstat.setDouble(9,Angsuran);
                        connection.pstat.setInt(10,0);
                        connection.pstat.executeUpdate();
                    }
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null, "Transaksi Pinjaman Berhasil");
                    loadData();
                    Clear();
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Transaksi Pinjaman Gagal "+ex);
                }
            }
        });
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        cbAnggota.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Max = "";
                String id = "";
                p = 0;
                String IdAnggota = "";
                try {

                    DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                    formatRp.setCurrencySymbol("Rp. ");
                    formatRp.setMonetaryDecimalSeparator(',');
                    formatRp.setGroupingSeparator('.');
                    Rupiah.setDecimalFormatSymbols(formatRp);

                    DBConnect connect = new DBConnect();

                    connection.stat = connection.conn.createStatement();
                    String syntaxx = "SELECT IdAnggota FROM tbMember WHERE NamaAnggota='" + cbAnggota.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntaxx);
                    while (connection.result.next()) {
                        IdAnggota = (String) connection.result.getString("IdAnggota");
                    }

                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT * FROM tbTrsPinjaman WHERE Status = 0";
                    connection.result = connection.stat.executeQuery(query);
                    while (connection.result.next()){
                        id = (String) connection.result.getString("IdTrsPinjaman");
                        connect.stat = connect.conn.createStatement();
                        String query1 = "SELECT dbo.Status_Pinjaman ('" + IdAnggota+"', "+"'"+id+"') as jml";
                        connect.result = connect.stat.executeQuery(query1);
                        while (connect.result.next()) {
                            p = connect.result.getInt("jml");
                            break;
                        }
                        if (p == 1){
                            JOptionPane.showMessageDialog(null,"Masih memiliki pinjaman");
                            Clear();
                            break;
                        }
                        System.out.println(p+" <<<<<< Hasil UDF" + cbAnggota.getSelectedItem());
                    }


                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT tbMember.IdAnggota,tbGaji.MaksimalPinjaman FROM tbMember " +
                            "JOIN tbGaji ON tbMember.IdGaji = tbGaji.IdGaji " +
                            "WHERE NamaAnggota = '" + cbAnggota.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        long max = connection.result.getLong("MaksimalPinjaman");
                        Max = Rupiah.format(max);
                        lblMax.setText(Max);
                        try{
                            String sbyr = lblMax.getText().replaceAll("\\,", "");
                            double dblByr = Double.parseDouble(sbyr);
                            DecimalFormat df = new DecimalFormat("#,###,###");
                            if (dblByr > 999) {
                                txtJumlah.setText(df.format(dblByr));
                            } else {
                                txtJumlah.setText(sbyr);
                            }
                        }catch (Exception ex){

                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data Max " + ex.getMessage());
                }
            }
        });

        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9')
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });

        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try{
                    String sbyr = txtJumlah.getText().replaceAll("\\,", "");
                    double dblByr = Double.parseDouble(sbyr);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblByr > 999) {
                        txtJumlah.setText(df.format(dblByr));
                    } else {
                        txtJumlah.setText(sbyr);
                    }
                }catch (Exception ex){

                }
            }
        });

        txtWaktu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') && txtWaktu.getText().length() < 2
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
    }

    public void showMember() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdAnggota, NamaAnggota, Status FROM tbMember ORDER BY NamaAnggota";
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

    public void showKategori() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdKategori, NamaKategori, Status FROM tbKategoriPinjaman";
            connection.result = connection.stat.executeQuery(sql);

            while (connection.result.next()) {
                if (connection.result.getString("Status").equals("1"))
                    cbKategori.addItem(connection.result.getString("NamaKategori"));
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException e) {
            System.out.println("Terjadi error saat load data Kategori " + e);
        }
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbTrsPinjaman ORDER BY IdTrsPinjaman desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdTrsPinjaman").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("PIJ" + nol + AN);
                cbAnggota.requestFocus();

            } else {
                txtID.setText("PIJ001");
                cbAnggota.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Transaksi Pinjaman: " + e1);
        }
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

    public void Clear(){
        autokode();
        txtBunga.setText("");
        txtJumlah.setText("");
        txtWaktu.setText("");
        lblMax.setText("");
        cbAnggota.setSelectedIndex(-1);
        cbKategori.setSelectedIndex(-1);
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

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tablePinjaman.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
            tablePinjaman.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

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
                obj[5] = Rupiah.format(connection.result.getInt("JumlahPinjaman"));

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
                obj[7] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
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

    public boolean Validasi(){
        if (cbAnggota.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Anggota Belum Diisi");
            return false;
        }
        if (cbKategori.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Kategori Belum Diisi");
            return false;
        }
        if (txtBunga.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Bunga Tidak Boleh Kosong");
            return false;
        }
        if (txtJumlah.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Jumlah Tidak Boleh Kosong");
            return false;
        }
        if (txtWaktu.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Lama Peminjaman Tidak Boleh Kosong");
            return false;
        }
        return true;
    }
}
