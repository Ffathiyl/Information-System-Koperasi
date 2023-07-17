package Master;

import DBConnection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Admin extends JFrame{
    public JPanel Admin;
    private JComboBox cbRole;
    private JComboBox cbStatus;
    private JTextField txtUsername;
    private JTextField txtNama;
    private JTextField txtID;
    private JPasswordField txtPassword;
    private JTable tableAdmin;
    private JButton simpanButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton batalButton;
    DBConnect connection = new DBConnect();
    private DefaultTableModel model = new DefaultTableModel();

    public Admin(){
        setContentPane(Admin);
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableAdmin.setModel(model);
        autokode();
        addColumn();
        loadData();
        cbRole.setSelectedIndex(-1);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()){
                    return;
                }
                try{
                    String query = "EXEC sp_InsertAdmin @idAdmin=?, @Nama=?, @Username=?, @Password=?, @Role=?, @Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,txtID.getText());
                    connection.pstat.setString(2,txtNama.getText());
                    connection.pstat.setString(3,txtUsername.getText());
                    connection.pstat.setString(4,txtPassword.getText());
                    if (cbRole.getSelectedItem().equals("Admin")){
                        connection.pstat.setInt(5,1);
                    } else {
                        connection.pstat.setInt(5,2);
                    }
                    if(cbStatus.getSelectedItem().equals("Aktif")){
                        connection.pstat.setInt(6,1);
                    } else {
                        connection.pstat.setInt(6,0);
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null,"Input data admin berhasil");
                    Clear();
                    loadData();
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,"Gagal Input Admin "+ex);
                }
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
                        String query = "EXEC sp_UpdateAdmin @idAdmin=?, @Nama=?, @Username=?, @Password=?," +
                                " @Role=?, @Status=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.setString(2, txtNama.getText());
                        connection.pstat.setString(3, txtUsername.getText());
                        connection.pstat.setString(4, txtPassword.getText());
                        if (cbRole.getSelectedItem().equals("Admin")) {
                            connection.pstat.setInt(5, 1);
                        } else {
                            connection.pstat.setInt(5, 2);
                        }
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(6, 1);
                        } else {
                            connection.pstat.setInt(6, 0);
                        }
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Update Admin Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Update Data Admin");
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
                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteAdmin @IdAdmin=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtID.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Hapus Admin Berhasil");
                        Clear();
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal Hapus Data Admin " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        tableAdmin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tableAdmin.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtID.setText(model.getValueAt(i,0).toString());
                txtNama.setText(model.getValueAt(i,1).toString());
                txtUsername.setText(model.getValueAt(i,2).toString());
                txtPassword.setText(model.getValueAt(i,3).toString());
                if (model.getValueAt(i,4).equals("Admin")){
                    cbRole.setSelectedIndex(0);
                } else if (model.getValueAt(i,4).equals("Ketua")){
                    cbRole.setSelectedIndex(1);
                }
                if (model.getValueAt(i,5).equals("Aktif")){
                    cbStatus.setSelectedIndex(0);
                } else if (model.getValueAt(i,5).equals("Tidak Aktif")){
                    cbStatus.setSelectedIndex(1);
                }
                simpanButton.setEnabled(false);
                deleteButton.setEnabled(true);
                updateButton.setEnabled(true);
            }
        });
        batalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clear();
            }
        });
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbAdmin";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[7];
                obj[0] = connection.result.getString("IdAdmin");
                obj[1] = connection.result.getString("Nama");
                obj[2] = connection.result.getString("Username");
                obj[3] = connection.result.getString("Password");
                if(connection.result.getString("Role").equals("1")){
                    obj[4] = "Admin";
                } else {
                    obj[4] = "Ketua";
                }
                if(connection.result.getString("Status").equals("1")){
                    obj[5] = "Aktif";
                } else {
                    obj[5] = "Tidak Aktif";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Gaji : " + e);
        }
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbAdmin ORDER BY IdAdmin desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdAdmin").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("ADM" + nol + AN);
                txtNama.requestFocus();

            } else {
                txtID.setText("ADM001");
                txtNama.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID Admin: " + e1);
        }
    }

    public void addColumn(){
        model.addColumn("ID Admin");
        model.addColumn("Nama Admin");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Role");
        model.addColumn("Status");
    }

    public void Clear(){
        autokode();
        txtNama.setText("");
        txtUsername.setText("");
        cbStatus.setSelectedItem("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(-1);
        simpanButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    public boolean Validasi(){
        if (txtID.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"ID Gaji tidak boleh kosong");
            return false;
        }
        if (txtNama.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Nama tidak boleh kosong");
            return false;
        }
        if (txtUsername.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Username tidak boleh kosong");
            return false;
        }
        if (txtPassword.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Password tidak boleh kosong");
            return false;
        }
        if (cbRole.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"Role tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"Status Gaji tidak boleh kosong");
            return false;
        }
        return true;
    }
}
