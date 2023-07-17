package Master;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Gaji extends JFrame{
    public JPanel Gaji;
    private JTextField txtID;
    private JComboBox cbStatus;
    private JTextField txtGaji;
    private JTable tableGaji;
    private JButton simpanButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton batalButton;
    private JTextField txtMax;
    private JTextField txtGajiAkhir;
    private JFormattedTextField txtMaxFormat;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public Gaji(){
        setContentPane(Gaji);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableGaji.setModel(model);
        autokode();
        addColumn();
        loadData();
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String awal = "";
                String akhir = "";
                if (!Validasi()){
                    return;
                }

                awal = txtGaji.getText();
                String harga_awal = awal;
                awal =harga_awal.replace(",","");

                akhir = txtGajiAkhir.getText();
                String harga_akhir = akhir;
                akhir =harga_akhir.replace(",","");

                String Merge = awal + " - " + akhir;

                String harga = txtMax.getText();
                String harga_ribuan = harga;
                harga =harga_ribuan.replace(",","");
                try{
                    String query = "EXEC sp_InsertGaji @IdGaji=?, @JumlahGaji=?, @JumlahPinjaman=? ,@Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,Merge);
                    connection.pstat.setString(3,harga);
                    if(cbStatus.getSelectedItem().equals("Aktif")){
                        connection.pstat.setInt(4,1);
                    } else {
                        connection.pstat.setInt(4,0);
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Simpan Gaji Berhasil");
                    Clear();
                    loadData();
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Gagal Input Data Gaji");
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String awal = "";
                String akhir = "";
                if (!Validasi()){
                    return;
                }
                String harga = txtMax.getText();
                String harga_ribuan = harga;
                harga =harga_ribuan.replace(",","");

                awal = txtGaji.getText();
                String harga_awal = awal;
                awal =harga_awal.replace(",","");

                akhir = txtGajiAkhir.getText();
                String harga_akhir = akhir;
                akhir =harga_akhir.replace(",","");

                String Merge = awal + " - " + akhir;

                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_UpdateGaji @IdGaji=?, @JumlahGaji=?, @JumlahPinjaman=?,@Status=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, Merge);
                        connection.pstat.setString(3,harga);
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(4, 1);
                        } else {
                            connection.pstat.setInt(4, 0);
                        }
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Update Gaji Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Update Data Gaji");
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan dihapus?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteGaji @IdGaji=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Hapus Gaji Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Hapus Data Gaji " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });

        tableGaji.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableGaji.getSelectedRow();
                if (i == -1){
                    return;
                }

                String Test = model.getValueAt(i,1).toString();
                String[] bagi = Test.split(" - ");

                String bag1 = bagi[0];
                String awal = bag1.replace("Rp. ","");
                awal = awal.replace(",","");
                long bagi1 = Long.parseLong(awal)/100;

                String bag2 = bagi[1];
                String akhir = bag2.replace("Rp. ","");
                akhir = akhir.replace(",","");
                long bagi2 = Long.parseLong(akhir)/100;

                String max = (String) model.getValueAt(i,2);
                String angka = max.replace("Rp. ","");
                angka = angka.replace(",","");
                long maks = Long.parseLong(angka) / 100;
                try{
                    String Max = String.valueOf(maks).replaceAll("\\,","");
                    double dblMax = Double.parseDouble(Max);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblMax > 999) {
                        txtMax.setText(df.format(dblMax));
                    } else {
                        txtMax.setText(Max);
                    }
                } catch (Exception ex){

                }

                try{
                    String Max = String.valueOf(bagi1).replaceAll("\\,","");
                    double dblMax = Double.parseDouble(Max);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblMax > 999) {
                        txtGaji.setText(df.format(dblMax));
                    } else {
                        txtGaji.setText(Max);
                    }
                } catch (Exception ex){

                }

                try{
                    String Max = String.valueOf(bagi2).replaceAll("\\,","");
                    double dblMax = Double.parseDouble(Max);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblMax > 999) {
                        txtGajiAkhir.setText(df.format(dblMax));
                    } else {
                        txtGajiAkhir.setText(Max);
                    }
                } catch (Exception ex){

                }

                txtID.setText(model.getValueAt(i,0).toString());
                //txtGaji.setText(model.getValueAt(i,1).toString());
                //txtMax.setText(model.getValueAt(i,2).toString());
                if (model.getValueAt(i,3).equals("Aktif")){
                    cbStatus.setSelectedIndex(0);
                } else if (model.getValueAt(i,3).equals("Tidak Aktif")) {
                    cbStatus.setSelectedIndex(1);
                }
                simpanButton.setEnabled(false);
                deleteButton.setEnabled(true);
                updateButton.setEnabled(true);
            }
        });
        txtMax.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try{
                    String sbyr = txtMax.getText().replaceAll("\\,", "");
                    double dblByr = Double.parseDouble(sbyr);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblByr > 999) {
                        txtMax.setText(df.format(dblByr));
                    } else {
                        txtMax.setText(sbyr);
                    }
                }catch (Exception ex){

                }
            }
        });
        txtGajiAkhir.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try{
                    String sbyr = txtGajiAkhir.getText().replaceAll("\\,", "");
                    double dblByr = Double.parseDouble(sbyr);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblByr > 999) {
                        txtGajiAkhir.setText(df.format(dblByr));
                    } else {
                        txtGajiAkhir.setText(sbyr);
                    }
                }catch (Exception ex){

                }
            }
        });
        txtGaji.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try{
                    String sbyr = txtGaji.getText().replaceAll("\\,", "");
                    double dblByr = Double.parseDouble(sbyr);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblByr > 999) {
                        txtGaji.setText(df.format(dblByr));
                    } else {
                        txtGaji.setText(sbyr);
                    }
                }catch (Exception ex){

                }
            }
        });

        txtMax.addKeyListener(new KeyAdapter() {
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

        txtGajiAkhir.addKeyListener(new KeyAdapter() {
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

        txtGaji.addKeyListener(new KeyAdapter() {
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

    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbGaji ORDER BY IdGaji desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdGaji").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("GAJ" + nol + AN);
                txtGaji.requestFocus();

            } else {
                txtID.setText("GAJ001");
                txtGaji.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Gaji: " + e1);
        }
    }

    public void addColumn(){
        model.addColumn("ID Gaji");
        model.addColumn("Jumlah Gaji");
        model.addColumn("Maksimal Peminjaman");
        model.addColumn("Status");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbGaji";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableGaji.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {

                String Test = connection.result.getString("JumlahGaji");
                String[] bagi = Test.split(" - ");
                int bagi1 = Integer.parseInt(bagi[0]);
                int bagi2 = Integer.parseInt(bagi[1]);

                Object[] obj = new Object[5];
                obj[0] = connection.result.getString("IdGaji");
                obj[1] = Rupiah.format(bagi1) + " - " + Rupiah.format(bagi2);
                obj[2] = Rupiah.format(connection.result.getInt("MaksimalPinjaman"));
                if(connection.result.getString("Status").equals("1")){
                    obj[3] = "Aktif";
                } else {
                    obj[3] = "Tidak Aktif";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Gaji : " + e);
        }
    }

    public void Clear(){
        autokode();
        txtGaji.setText("");
        txtMax.setText("");
        txtGajiAkhir.setText("");
        cbStatus.setSelectedItem("");
        simpanButton.setEnabled(true);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
    }

    public boolean Validasi(){
        if (txtID.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"ID Gaji tidak boleh kosong");
            return false;
        }
        if (txtGaji.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Jumlah Gaji tidak boleh kosong");
            return false;
        }
        if (txtMax.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Maksimal Peminjaman tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"Status Gaji tidak boleh kosong");
            return false;
        }
        return true;
    }
}
