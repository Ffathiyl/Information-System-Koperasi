package Master;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Denda extends JFrame{
    public JPanel Denda;
    private JComboBox cbStatus;
    private JTextField txtID;
    private JTextField txtDeskripsi;
    private JTextField txtJumlahDenda;
    private JTable tableDenda;
    private JButton btnSimpan;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JButton btnBatal;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public Denda() {
        setContentPane(Denda);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableDenda.setModel(model);
        addColumn();
        loadData();
        autokode();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                String denda = txtJumlahDenda.getText();
                String denda_ribuan = denda;
                denda =denda_ribuan.replace(",","");
                try{
                    String query = "EXEC sp_InsertDenda @IdDenda=?, @Deskripsi=?, @JumlahDenda=?, @Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,txtDeskripsi.getText());
                    connection.pstat.setString(3,denda);
                    if(cbStatus.getSelectedItem().equals("Aktif")){
                        connection.pstat.setInt(4,1);
                    } else {
                        connection.pstat.setInt(4,0);
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Data Denda berhasil ditambahkan");
                    Clear();
                    loadData();
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Gagal menginput data denda "+ex);
                }
            }
        });
        tableDenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableDenda.getSelectedRow();
                if(i == -1){
                    return;
                }

                String max = (String) model.getValueAt(i,2);
                String angka = max.replace("Rp. ","");
                angka = angka.replace(",","");
                long denda = Long.parseLong(angka) / 100;
                try{
                    String Denda = String.valueOf(denda).replaceAll("\\,","");
                    double dblMax = Double.parseDouble(Denda);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblMax > 999) {
                        txtJumlahDenda.setText(df.format(dblMax));
                    } else {
                        txtJumlahDenda.setText(Denda);
                    }
                } catch (Exception ex){

                }

                txtID.setText(model.getValueAt(i,0).toString());
                txtDeskripsi.setText(model.getValueAt(i,1).toString());
                //txtJumlahDenda.setText(model.getValueAt(i,2).toString());
                if(model.getValueAt(i,3).equals("Aktif")){
                    cbStatus.setSelectedIndex(0);
                } else if (model.getValueAt(i,3).equals("Tidak Aktif")){
                    cbStatus.setSelectedIndex(1);
                }
                btnSimpan.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                String denda = txtJumlahDenda.getText();
                String denda_ribuan = denda;
                denda =denda_ribuan.replace(",","");
                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_UpdateDenda @IdDenda=?, @Deskripsi=?, @JumlahDenda=?, @Status=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, txtDeskripsi.getText());
                        connection.pstat.setString(3,denda);
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(4, 1);
                        } else {
                            connection.pstat.setInt(4, 0);
                        }
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Update Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Update Data Denda");
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                int result = JOptionPane.showOptionDialog(null, "Apakah yakin akan dihapus?",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteDenda @IdDenda=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Hapus Denda Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Hapus Data Denda " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        txtDeskripsi.addKeyListener(new KeyAdapter() {
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
        txtJumlahDenda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c)
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }
        });
        txtJumlahDenda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                try{
                    String sbyr = txtJumlahDenda.getText().replaceAll("\\,", "");
                    double dblByr = Double.parseDouble(sbyr);
                    DecimalFormat df = new DecimalFormat("#,###,###");
                    if (dblByr > 999) {
                        txtJumlahDenda.setText(df.format(dblByr));
                    } else {
                        txtJumlahDenda.setText(sbyr);
                    }
                }catch (Exception ex){

                }
            }
        });
    }

    public void Denda(){
        setContentPane(Denda);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model = new DefaultTableModel();
        tableDenda.setModel(model);
        autokode();
        addColumn();
        loadData();
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbDenda ORDER BY IdDenda desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdDenda").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("DND" + nol + AN);
                txtDeskripsi.requestFocus();

            } else {
                txtID.setText("DND001");
                txtDeskripsi.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Denda: " + e1);
        }
    }

    public void addColumn(){
        model.addColumn("ID Denda");
        model.addColumn("Deskripsi Denda");
        model.addColumn("Jumlah Denda");
        model.addColumn("Status");
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbDenda";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            center.setHorizontalAlignment(SwingConstants.CENTER);
            tableDenda.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
            tableDenda.getColumnModel().getColumn(0).setCellRenderer(center);
            tableDenda.getColumnModel().getColumn(1).setCellRenderer(center);
            tableDenda.getColumnModel().getColumn(3).setCellRenderer(center);

            while (connection.result.next()) {
                Object[] obj = new Object[5];
                obj[0] = connection.result.getString("IdDenda");
                obj[1] = connection.result.getString("Deskripsi");
                obj[2] = Rupiah.format(connection.result.getInt("JumlahDenda"));
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
            System.out.println("Terjadi error saat meload data Denda : " + e);
        }
    }

    public void Clear(){
        autokode();
        txtDeskripsi.setText("");
        txtJumlahDenda.setText("");
        cbStatus.setSelectedItem("");
        btnSimpan.setEnabled(true);
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);
    }

    public boolean Validasi(){
        if (txtID.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"ID Denda tidak boleh kosong");
            return false;
        }
        if (txtDeskripsi.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Deskripsi tidak boleh kosong");
            return false;
        }
        if (txtJumlahDenda.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Jumlah Denda tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"ID Denda tidak boleh kosong");
            return false;
        }
        return true;
    }
}
