package Master;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class KategoriPinjaman extends JFrame{
    public JPanel KategoriPinjaman;
    private JComboBox cbStatus;
    private JTextField txtID;
    private JTextField txtNama;
    private JTextField txtDeskripsi;
    private JTextField txtBunga;
    private JButton simpanButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton batalButton;
    private JTable tableKategori;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public KategoriPinjaman(){
        setContentPane(KategoriPinjaman);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableKategori.setModel(model);
        autokode();
        addColumn();
        loadData();
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        tableKategori.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableKategori.getSelectedRow();
                if(i == -1){
                    return;
                }

                String persen = model.getValueAt(i,3).toString();
                String bunga = persen.replace("%","");
                bunga = bunga.replace(",","");
                int bung = Integer.parseInt(bunga)/100;

                txtID.setText(model.getValueAt(i,0).toString());
                txtNama.setText(model.getValueAt(i,1).toString());
                txtDeskripsi.setText(model.getValueAt(i,2).toString());
                txtBunga.setText(String.valueOf(bung));
                if (model.getValueAt(i,4).equals("Aktif")){
                    cbStatus.setSelectedIndex(0);
                } else if (model.getValueAt(i,4).equals("Tidak Aktif")){
                    cbStatus.setSelectedIndex(1);
                }
                simpanButton.setEnabled(false);
                deleteButton.setEnabled(true);
                updateButton.setEnabled(true);
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                try{
                    String query = "EXEC sp_InsertKategoriPinjaman @IdKategori=?, @NamaKategori=?, @Deskripsi=?," +
                            "@Bunga=?, @Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,txtNama.getText());
                    connection.pstat.setString(3,txtDeskripsi.getText());
                    connection.pstat.setInt(4,Integer.parseInt(txtBunga.getText()));
                    if(cbStatus.getSelectedItem().equals("Aktif")){
                        connection.pstat.setInt(5,1);
                    } else {
                        connection.pstat.setInt(5,0);
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Data Kategori Pinjaman berhasil ditambahkan");
                    Clear();
                    loadData();
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Gagal Input Kategori Pinjaman");
                }
            }
        });
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }

                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_UpdateKategoriPinjaman @IdKategori=?, @NamaKategori=?, @Deskripsi=?," +
                                "@Bunga=?, @Status=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, txtNama.getText());
                        connection.pstat.setString(3, txtDeskripsi.getText());
                        connection.pstat.setInt(4, Integer.parseInt(txtBunga.getText()));
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(5, 1);
                        } else {
                            connection.pstat.setInt(5, 0);
                        }
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Update Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal update Kategori Pinjaman " + ex);
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
                int result = JOptionPane.showOptionDialog(null, "Apakah yakin akan dihapus?",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteKategoriPinjaman @IdKategori=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Hapus Kategori Pinjaman Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal menghapus kategori pinjaman "+ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)
                        || (c == KeyEvent.VK_DELETE)
                        || (c == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
        txtBunga.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') && txtBunga.getText().length() < 2
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
        txtDeskripsi.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                char c = e.getKeyChar();
                if (!Character.isAlphabetic(c) && !Character.isWhitespace(c) || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbKategoriPinjaman ORDER BY IdKategori desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdKategori").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("KAT" + nol + AN);
                txtDeskripsi.requestFocus();

            } else {
                txtID.setText("KAT001");
                txtDeskripsi.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Kategori: " + e1);
        }
    }

    public void addColumn(){
        model.addColumn("ID Kategori");
        model.addColumn("Nama Kategori");
        model.addColumn("Deskripsi Kategori");
        model.addColumn("Bunga");
        model.addColumn("Status");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbKategoriPinjaman";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setPercent('%');
            DecimalFormat df = new DecimalFormat("0,00%",dfs);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableKategori.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[6];
                obj[0] = connection.result.getString("IdKategori");
                obj[1] = connection.result.getString("NamaKategori");
                obj[2] = connection.result.getString("Deskripsi");
                obj[3] = df.format(connection.result.getInt("Bunga"));
                if(connection.result.getString("Status").equals("1")){
                    obj[4] = "Aktif";
                } else {
                    obj[4] = "Tidak Aktif";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Kategori Pinjaman : " + e);
        }
    }

    public void Clear(){
        autokode();
        txtNama.setText("");
        txtDeskripsi.setText("");
        txtBunga.setText("");
        cbStatus.setSelectedItem("");
        simpanButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public boolean Validasi(){
        if (txtID.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"ID Kategori Pinjaman tidak boleh kosong");
            return false;
        }
        if (txtNama.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Nama Kategori tidak boleh kosong");
            return false;
        }
        if (txtDeskripsi.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Deskripsi Kategori tidak boleh kosong");
            return false;
        }
        if (txtBunga.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Bunga Kategori tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"ID Kategori tidak boleh kosong");
            return false;
        }
        return true;
    }
}
