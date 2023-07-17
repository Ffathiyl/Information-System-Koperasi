package Dashboard;

import DBConnection.DBConnect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JPanel DashboardAwal;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton loginButton;
    private JButton exitButton;
    private JCheckBox lihatPasswordCheckBox;

    public Login() {
        setUndecorated(true);
        setContentPane(DashboardAwal);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        CheckMasaBerlaku();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtUsername.getText().isEmpty() && txtPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Masukkan Username & Password");
                } else if (txtUsername.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Masukkan Username");
                } else if (txtPassword.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Masukkan Password");
                } else {
                    try {
                        DBConnect connection = new DBConnect();
                        connection.stat = connection.conn.createStatement();
                        String query = "SELECT * FROM tbAdmin WHERE Username COLLATE Latin1_General_CS_AS = '" + txtUsername.getText() + "' AND Password COLLATE Latin1_General_CS_AS = '" + txtPassword.getText() + "'";
                        connection.result = connection.stat.executeQuery(query);

                        if (connection.result.next()) {
                            String role = connection.result.getString("Role");
                            int status = connection.result.getInt("Status");
                            if (status == 1) {
                                if (role.equals("1")) {
                                    JOptionPane.showMessageDialog(null, "Berhasil Login Sebagai Admin");
                                    DashboardAwalAdmin awal = new DashboardAwalAdmin(connection.result.getString("Nama"));
                                    awal.setVisible(true);
                                    dispose();
                                } else if (role.equals("2")) {
                                    JOptionPane.showMessageDialog(null, "Berhasil Login Sebagai Ketua");
                                    DashboardKetua ketua = new DashboardKetua(connection.result.getString("Nama"));
                                    ketua.setVisible(true);
                                    dispose();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Login Gagal!\n" +
                                            "Jabatan Tidak Tersedia");
                                    Clear();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Login Gagal\n" +
                                        "Akun Sudah Tidak Aktif");
                                Clear();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Login Gagal\n" +
                                    "Masukkan Username dan Password dengan benar");
                            Clear();
                        }
                    }
                    catch(Exception ex){
                            System.out.println();
                        }
                    }
                }
            });
        exitButton.addActionListener(new

            ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){
                    dispose();
                }
            });
        lihatPasswordCheckBox.addActionListener(new

            ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){
                    if (lihatPasswordCheckBox.isSelected() == true) {
                        txtPassword.setEchoChar('\0');
                    } else {
                        txtPassword.setEchoChar('●');
                    }
                }
            });
        }

        public void CheckMasaBerlaku () {
            DBConnect connection = new DBConnect();
            try {
                String exec = "EXEC sp_SetStatus";
                connection.pstat = connection.conn.prepareStatement(exec);
                connection.pstat.executeUpdate();
                connection.pstat.close();
                System.out.println("Berhasil Mengecek Status");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Gagal Mengecek Status" + ex);
            }
        }

        public void Clear () {
            txtPassword.setText("");
            txtUsername.setText("");
            txtUsername.requestFocus();
        }
    }
