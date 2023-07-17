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

public class Simpanan extends JFrame {
    public JPanel Simpanan;
    private JTable tableSimpanan;
    private JTextField txtID;
    private JTextField txtBunga;
    private JComboBox cbJenisSimpanan;
    private JComboBox cbAnggota;
    private JTextField txtJumlah;
    private JTextField txtLama;
    private JButton batalButton;
    private JLabel lblAdmin;
    private JButton simpanButton;
    private JLabel lblTanggal;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public Simpanan(String data, String tanggal) {
        setContentPane(Simpanan);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        lblAdmin.setText(data);
        lblTanggal.setText(tanggal);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableSimpanan.setModel(model);
        addColumn();
        showMember();
        showJenis();
        loadData();
        autokode();
        cbJenisSimpanan.setSelectedIndex(-1);
        cbAnggota.setSelectedIndex(-1);
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                String IdAnggota = "";
                String IdJenis = "";
                String IdAdmin = "";
                String Bunga = "";
                String jenis = "";
                String Berlaku = "";
                jenis = cbJenisSimpanan.getSelectedItem().toString();
                String harga = txtJumlah.getText();
                String harga_ribuan = harga;
                harga =harga_ribuan.replace(",","");
                if (jenis.equals("Simpanan Wajib")) {
                    try {
                        connection.stat = connection.conn.createStatement();
                        String syntax = "SELECT IdAnggota,MasaBerlaku FROM tbMember WHERE NamaAnggota='" + cbAnggota.getSelectedItem() + "'";
                        connection.result = connection.stat.executeQuery(syntax);
                        while (connection.result.next()) {
                            IdAnggota = (String) connection.result.getString("IdAnggota");
                            Berlaku = (String) connection.result.getString("MasaBerlaku");
                        }
                    } catch (Exception ex) {
                        System.out.println("Gagal ambil data anggota " + ex.getMessage());
                    }

                    try {
                        connection.stat = connection.conn.createStatement();
                        String syntax = "SELECT IdJenisSimpanan,Bunga FROM tbJenisSimpanan WHERE NamaJenis ='" + cbJenisSimpanan.getSelectedItem() + "'";
                        connection.result = connection.stat.executeQuery(syntax);
                        while (connection.result.next()) {
                            IdJenis = (String) connection.result.getString("IdJenisSimpanan");
                        }
                    } catch (Exception ex) {
                        System.out.println("Gagal ambil data jenis " + ex.getMessage());
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

                    try {
                        String query = "INSERT INTO tbTrsSimpanan VALUES" +
                                "(?,?,?,?,?,?,?,?)";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, IdJenis);
                        connection.pstat.setString(3, IdAnggota);
                        connection.pstat.setString(4, tanggal);
                        connection.pstat.setString(5,harga);
                        connection.pstat.setInt(6, Integer.parseInt(txtLama.getText()));
                        connection.pstat.setString(7, IdAdmin);
                        connection.pstat.setInt(8, 1);
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Transaksi Simpanan Pokok Berhasil");
                        loadData();
                        Clear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Transaksi Simpanan Pokok Gagal " + ex);
                    }
                    //String formattedDate = "";
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = formatter.parse(Berlaku);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.MONTH, 1);
                        calendar.add(Calendar.DAY_OF_YEAR, 2);
                        Format format = new SimpleDateFormat("yyyy-MM-dd");
                        Date datee = calendar.getTime();
                        Berlaku = format.format(datee);
                    } catch (Exception ex) {
                        System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                    }
                    try {
                        String query = "UPDATE tbMember SET MasaBerlaku = ? WHERE IdAnggota = ?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, Berlaku);
                        connection.pstat.setString(2, IdAnggota);
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Transaksi Pokok");
                    }
                } else {
                    if (txtLama.getText().equals("0")){
                        JOptionPane.showMessageDialog(null,"Lama Simpanan Tidak boleh 0!");
                        txtLama.requestFocus();
                        return;
                    }
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
                        String syntax = "SELECT IdJenisSimpanan,Bunga FROM tbJenisSimpanan WHERE NamaJenis ='" + cbJenisSimpanan.getSelectedItem() + "'";
                        connection.result = connection.stat.executeQuery(syntax);
                        while (connection.result.next()) {
                            IdJenis = (String) connection.result.getString("IdJenisSimpanan");
                            Bunga = (String) connection.result.getString("Bunga");
                            txtBunga.setText(Bunga);
                        }
                    } catch (Exception ex) {
                        System.out.println("Gagal ambil data jenis " + ex.getMessage());
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

                    try {
                        String query = "INSERT INTO tbTrsSimpanan VALUES" +
                                "(?,?,?,?,?,?,?,?)";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, IdJenis);
                        connection.pstat.setString(3, IdAnggota);
                        connection.pstat.setString(4, tanggal);
                        connection.pstat.setString(5,harga);
                        connection.pstat.setInt(6, Integer.parseInt(txtLama.getText()));
                        connection.pstat.setString(7,IdAdmin);
                        connection.pstat.setInt(8, 1);
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Transaksi Berhasil");
                        loadData();
                        Clear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Transaksi Gagal " + ex);
                    }
                }
            }
        });
        cbJenisSimpanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Bunga = "";
                try {
                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdJenisSimpanan,Bunga,NamaJenis FROM tbJenisSimpanan WHERE NamaJenis ='" + cbJenisSimpanan.getSelectedItem() + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        Bunga = (String) connection.result.getString("Bunga");
                        txtBunga.setText(Bunga);
                        if (connection.result.getString("NamaJenis").equals("Simpanan Wajib")){
                            int pokok = 1000000;
                            txtJumlah.setText(String.valueOf(pokok));
                            try{
                                String sbyr = txtJumlah.getText().replaceAll("\\,", "");
                                double dblByr = Double.parseDouble(sbyr);
                                DecimalFormat df = new DecimalFormat("#,###,###");
                                if (dblByr > 999) {
                                    txtJumlah.setText(df.format(dblByr));
                                } else {
                                    txtJumlah.setText(sbyr);
                                }
                            } catch (Exception ex){

                            }
                            txtLama.setText(String.valueOf(0));
                        } else {
                            txtJumlah.setText("");
                            txtLama.setText("");
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Gagal ambil data jenis " + ex.getMessage());
                }
            }
        });
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
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
        txtLama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') && txtLama.getText().length() < 2
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

    public void showMember() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdAnggota, NamaAnggota, Status FROM tbMember";
            connection.result = connection.stat.executeQuery(sql);

            while (connection.result.next()) {
                //if (connection.result.getString("Status").equals("1"))
                cbAnggota.addItem(connection.result.getString("NamaAnggota"));
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException e) {
            System.out.println("Terjadi error saat load data Anggota " + e);
        }
    }

    public void showJenis() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdJenisSimpanan, NamaJenis, Status FROM tbJenisSimpanan";
            connection.result = connection.stat.executeQuery(sql);

            while (connection.result.next()) {
                if (connection.result.getString("Status").equals("1"))
                    cbJenisSimpanan.addItem(connection.result.getString("NamaJenis"));
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException e) {
            System.out.println("Terjadi error saat load data Jenis Simpanan " + e);
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
                    "JOIN tbAdmin ON t.IdAdmin = tbAdmin.IdAdmin";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableSimpanan.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);

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
                obj[4] = connection.result.getInt("JumlahSimpanan");
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

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbTrsSimpanan ORDER BY IdTrsSimpanan desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdTrsSimpanan").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("SIM" + nol + AN);
                cbAnggota.requestFocus();

            } else {
                txtID.setText("SIM001");
                cbAnggota.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Transaksi Simpanan: " + e1);
        }
    }

    public void Clear() {
        autokode();
        txtBunga.setText("");
        txtJumlah.setText("");
        txtLama.setText("");
        cbAnggota.setSelectedIndex(-1);
        cbJenisSimpanan.setSelectedIndex(-1);
    }

    public boolean Validasi(){
        if (cbAnggota.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Anggota Belum Diisi");
            return false;
        }
        if (cbJenisSimpanan.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Jenis Simpanan Belum Diisi");
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
        if (txtLama.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Lama Peminjaman Tidak Boleh Kosong");
            return false;
        }
        return true;
    }
}
