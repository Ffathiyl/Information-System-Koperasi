package Master;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JenisSimpanan extends JFrame{
    public JPanel JenisSimpanan;
    private JTextField txtID;
    private JTextField txtNama;
    private JTextField txtBunga;
    private JComboBox cbStatus;
    private JTable tableJenisSImpanan;
    private JButton btnSImpan;
    private JButton btnUpdate;
    private JButton btnBatal;
    private JButton btnHapus;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public JenisSimpanan(){
        setContentPane(JenisSimpanan);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableJenisSImpanan.setModel(model);
        autokode();
        addColumn();
        loadData();
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnSImpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Validasi()){
                    return;
                }
                try{
                    String query = "EXEC sp_InsertJenisSimpanan @IdJenisSimpanan=?,@NamaJenis=?,@Bunga=?,@Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,txtNama.getText());
                    connection.pstat.setInt(3,Integer.parseInt(txtBunga.getText()));
                    if(cbStatus.getSelectedItem().equals("Aktif")){
                        connection.pstat.setInt(4,1);
                    } else if (cbStatus.getSelectedItem().equals("Tidak Aktif")){
                        connection.pstat.setInt(4,0);
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Data Jenis Berhasil Ditambahkan");
                    loadData();
                    Clear();
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Error pada saat menambahkan data Jenis Simpanan "+ex);
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Validasi()){
                    return;
                }
                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_UpdateJenisSimpanan @IdJenisSimpanan=?, @NamaJenis=?, @Bunga=?, @Status=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, txtNama.getText());
                        connection.pstat.setInt(3, Integer.parseInt(txtBunga.getText()));
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(4, 1);
                        } else {
                            connection.pstat.setInt(4, 0);
                        }
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Update Data Jenis Simpanan Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Saat Update Jenis Simpanan " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        tableJenisSImpanan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableJenisSImpanan.getSelectedRow();
                if (i == -1) {
                    return;
                }

                String persen = model.getValueAt(i,2).toString();
                String bunga = persen.replace("%","");
                bunga = bunga.replace(",","");
                int bung = Integer.parseInt(bunga)/100;

                txtID.setText(model.getValueAt(i,0).toString());
                txtNama.setText(model.getValueAt(i,1).toString());
                txtBunga.setText(String.valueOf(bung));
                if(model.getValueAt(i,3).equals("Aktif")){
                    cbStatus.setSelectedIndex(0);
                } else if (model.getValueAt(i,3).equals("Tidak Aktif")){
                    cbStatus.setSelectedIndex(1);
                }
                btnSImpan.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnHapus.setEnabled(true);
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                int result = JOptionPane.showOptionDialog(null, "Apakah yakin akan dihapus?",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteJenisSimpanan @IdJenisSimpanan=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
                        loadData();
                        Clear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal saat menghapus data Jenis Simpanan " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
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
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
    }


    public void autokode() {
        try {
            String sql = "SELECT * FROM tbJenisSimpanan ORDER BY IdJenisSimpanan desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdJenisSimpanan").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("JNS" + nol + AN);
                txtNama.requestFocus();

            } else {
                txtID.setText("JNS001");
                txtNama.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Jenis Simpanan: " + e1);
        }
    }

    public void addColumn(){
        model.addColumn("ID Jenis");
        model.addColumn("Nama Jenis");
        model.addColumn("Bunga");
        model.addColumn("Status");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbJenisSimpanan";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setPercent('%');
            DecimalFormat df = new DecimalFormat("0,00%",dfs);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableJenisSImpanan.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[5];
                obj[0] = connection.result.getString("IdJenisSimpanan");
                obj[1] = connection.result.getString("NamaJenis");
                obj[2] = df.format(connection.result.getInt("Bunga"));
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
            System.out.println("Terjadi error saat meload data Jenis Simpanan : " + e);
        }
    }

    public void Clear(){
        autokode();
        txtBunga.setText("");
        txtNama.setText("");
        cbStatus.setSelectedItem("");
        btnSImpan.setEnabled(true);
        btnHapus.setEnabled(false);
        btnUpdate.setEnabled(false);
    }

    public boolean Validasi(){
        if (txtID.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"ID Jenis tidak boleh kosong");
            return false;
        }
        if (txtNama.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Nama Jenis tidak boleh kosong");
            return false;
        }
        if (txtBunga.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Bunga tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"ID Jenis tidak boleh kosong");
            return false;
        }
        return true;
    }
}
