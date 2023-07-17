package Master;

import DBConnection.DBConnect;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Member extends JFrame {
    public JPanel Member;
    private JTextField txtIdAnggota;
    private JTextField txtNamaAnggota;
    private JTextField txtAlamat;
    private JTextField txtStatus;
    private JRadioButton rbLaki;
    private JRadioButton rbPerempuan;
    private JPanel jpTanggalLahir;
    private JComboBox cbGaji;
    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnBatal;
    private JTable tableMember;
    private JComboBox cbStatus;
    private JTextField textField1;
    private JButton cariButton;
    private JButton muatUlangButton;
    private JDateChooser datechose = new JDateChooser();
    DBConnect connection = new DBConnect();
    private DefaultTableModel model;
    Calendar calendar = Calendar.getInstance();

    public Member() {
        setContentPane(Member);
        jpTanggalLahir.add(datechose);
        showGaji();
        autokode();
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mengembalikan false untuk semua sel
            }
        };
        tableMember.setModel(model);
        FrameKelamin();
        addColumn();
        loadData();
        cbGaji.setSelectedIndex(-1);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()) {
                    return;
                }
                String formattedDate = "";
                String JmlGaji = cbGaji.getSelectedItem().toString();
                String[] bagi = JmlGaji.split(" - ");
                String bagi1 = bagi[0];
                String awal = bagi1.replace("Rp. ","");
                awal = awal.replace(",","");
                Long bag1 = Long.parseLong(awal)/100;
                String bagi2 = bagi[1];
                String akhir = bagi2.replace("Rp. ","");
                akhir = akhir.replace(",","");
                Long bag2 = Long.parseLong(akhir)/100;
                String G = bag1 + " - " + bag2;
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, 1); // Tambah 1 bulan ke calendar
                    Date date = calendar.getTime();
                    formattedDate = formatter.format(date);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }
                String Gaji = "";
                String kelamin = "";
                Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String tanggal = formatter.format(datechose.getDate());
                if (rbLaki.isSelected()) {
                    kelamin = "Laki - Laki";
                } else {
                    kelamin = "Perempuan";
                }
                try {
                    connection.stat = connection.conn.createStatement();
                    String syntax = "SELECT IdGaji FROM tbGaji WHERE JumlahGaji='" + G + "'";
                    connection.result = connection.stat.executeQuery(syntax);
                    while (connection.result.next()) {
                        Gaji = (String) connection.result.getString("IdGaji");
                    }

                    String query = "EXEC sp_InsertMember @IdAnggota=?, @IdGaji=?, @NamaAnggota=? , @TanggalLahir=?," +
                            "@JenisKelamin=?, @Alamat=?, @TotalSimpanan=?, @TotalPinjaman=?,@Today=?, @Status=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1, txtIdAnggota.getText());
                    connection.pstat.setString(2, Gaji);
                    connection.pstat.setString(3, txtNamaAnggota.getText());
                    connection.pstat.setString(4, tanggal);
                    connection.pstat.setString(5, kelamin);
                    connection.pstat.setString(6, txtAlamat.getText());
                    connection.pstat.setInt(7, 0);
                    connection.pstat.setInt(8, 0);
                    connection.pstat.setString(9, formattedDate);
                    if (cbStatus.getSelectedItem().equals("Aktif")) {
                        connection.pstat.setString(10, "1");
                    } else {
                        connection.pstat.setString(10, "0");
                    }
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                    JOptionPane.showMessageDialog(null, "Member berhasil ditambahkan");
                    loadData();
                    Clear();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Gagal input member " + ex);
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Validasi()) {
                    return;
                }

                String JmlGaji = cbGaji.getSelectedItem().toString();
                String[] bagi = JmlGaji.split(" - ");
                String bagi1 = bagi[0];
                String awal = bagi1.replace("Rp. ","");
                awal = awal.replace(",","");
                Long bag1 = Long.parseLong(awal)/100;
                String bagi2 = bagi[1];
                String akhir = bagi2.replace("Rp. ","");
                akhir = akhir.replace(",","");
                Long bag2 = Long.parseLong(akhir)/100;
                String G = bag1 + " - " + bag2;

                String Gaji = "";
                String kelamin = "";
                Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String tanggal = formatter.format(datechose.getDate());
                if (rbLaki.isSelected()) {
                    kelamin = "Laki - Laki";
                } else {
                    kelamin = "Perempuan";
                }
                int result = JOptionPane.showConfirmDialog(null, "Apakah yakin akan perbarui?");
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        connection.stat = connection.conn.createStatement();
                        String syntax = "SELECT IdGaji FROM tbGaji WHERE JumlahGaji='" + G + "'";
                        connection.result = connection.stat.executeQuery(syntax);
                        while (connection.result.next()) {
                            Gaji = (String) connection.result.getString("IdGaji");
                        }

                        String query = "EXEC sp_UpdateMember @NamaAnggota=?,@IdGaji=?,@TanggalLahir=?," +
                                "@JenisKelamin=?,@Alamat=?,@Status=?,@IdAnggota=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtNamaAnggota.getText());
                        connection.pstat.setString(2, Gaji);
                        connection.pstat.setString(3, tanggal);
                        connection.pstat.setString(4, kelamin);
                        connection.pstat.setString(5, txtAlamat.getText());
                        if (cbStatus.getSelectedItem().equals("Aktif")) {
                            connection.pstat.setInt(6, 1);
                        } else {
                            connection.pstat.setInt(6, 1);
                        }
                        connection.pstat.setString(7, txtIdAnggota.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Data berhasil diupdate");
                        loadData();
                        Clear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal update data " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });

        tableMember.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                String kelamin = "";
                int i = tableMember.getSelectedRow();
                if (i == -1) {
                    return;
                }
                String gaji = model.getValueAt(i, 1).toString();
                int row = 0;
                try {
                    connection.stat = connection.conn.createStatement();
                    String search = "SELECT IdGaji,JumlahGaji FROM tbGaji";
                    connection.result = connection.stat.executeQuery(search);
                    while (connection.result.next()) {
                        String IdGaji = connection.result.getString("JumlahGaji");
                        if (IdGaji.equals(gaji)) {
                            row++;
                            break;
                        }
                        row++;
                    }
                } catch (Exception ex) {
                    System.out.println("Tidak ada id gaji");
                }

                String tanggalLahir = (String) model.getValueAt(i, 3);
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(tanggalLahir);
                    datechose.setDate(date);
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                cbGaji.setSelectedIndex(row - 1);
                txtIdAnggota.setText((String) model.getValueAt(i, 0));
                txtNamaAnggota.setText(model.getValueAt(i, 2).toString());
                kelamin = (String) model.getValueAt(i, 4);
                if (kelamin.equals("Laki - Laki")) {
                    rbLaki.setSelected(true);
                } else if (kelamin.equals("Perempuan")) {
                    rbPerempuan.setSelected(true);
                }
                txtAlamat.setText(model.getValueAt(i, 5).toString());
                if (model.getValueAt(i, 9).equals("Aktif")) {
                    cbStatus.setSelectedIndex(0);
                } else {
                    cbStatus.setSelectedIndex(1);
                }
                btnSimpan.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
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
                if (!Validasi()) {
                    return;
                }
                int result = JOptionPane.showOptionDialog(null, "Apakah yakin akan dihapus?",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        String query = "EXEC sp_DeleteMember ?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, txtIdAnggota.getText());
                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        JOptionPane.showMessageDialog(null, "Member berhasil dihapus");
                        loadData();
                        Clear();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal saat menghapus data Member " + ex);
                    }
                } else if (result == JOptionPane.NO_OPTION) {
                    Clear();
                }
            }
        });
        txtNamaAnggota.addKeyListener(new KeyAdapter() {
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
        muatUlangButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
                textField1.setText("");
            }
        });
        cariButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataNama();
            }
        });
    }

    public void autokode() {
        try {
            String sql = "SELECT * FROM tbMember ORDER BY IdAnggota desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                String ID = connection.result.getString("IdAnggota").substring(4);
                String AN = "" + (Integer.parseInt(ID) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtIdAnggota.setText("MEM" + nol + AN);
                txtNamaAnggota.requestFocus();

            } else {
                txtIdAnggota.setText("MEM001");
                txtNamaAnggota.requestFocus();
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Terjadi error pada kode ID MEMBER: " + e1);
        }
    }

    public void showGaji() {
        try {
            connection.stat = connection.conn.createStatement();
            String sql = "SELECT IdGaji, JumlahGaji, Status FROM tbGaji";
            connection.result = connection.stat.executeQuery(sql);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            while (connection.result.next()) {
                if (connection.result.getString("Status").equals("1")) {
                    String Test = connection.result.getString("JumlahGaji");
                    String[] bagi = Test.split(" - ");
                    int bagi1 = Integer.parseInt(bagi[0]);
                    int bagi2 = Integer.parseInt(bagi[1]);
                    String gaji = Rupiah.format(bagi1) + " - " + Rupiah.format(bagi2);
                    cbGaji.addItem(gaji);
                }
            }
            connection.stat.close();
            connection.result.close();
        } catch (SQLException e) {
            System.out.println("Terjadi error saat load data Gaji " + e);
        }
    }

    public void addColumn() {
        model.addColumn("ID Anggota");
        model.addColumn("Gaji Anggota");
        model.addColumn("Nama Anggota");
        model.addColumn("Tanggal Lahir");
        model.addColumn("Jenis Kelamin");
        model.addColumn("Alamat");
        model.addColumn("Total Simpanan");
        model.addColumn("Total Pinjaman");
        model.addColumn("Masa Berlaku");
        model.addColumn("Status");
    }

    public void FrameKelamin() {
        ButtonGroup group = new ButtonGroup();
        group.add(rbLaki);
        group.add(rbPerempuan);
    }

    public void loadData() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT tm.IdAnggota, tg.JumlahGaji, tm.NamaAnggota, tm.TanggalLahir, " +
                    "tm.JenisKelamin, tm.Alamat,\n" +
                    "\t\ttm.TotalSimpanan, tm.TotalPinjaman,tm.MasaBerlaku,tm.Status\n" +
                    "FROM tbMember tm JOIN tbGaji tg ON tm.IdGaji = tg.IdGaji " +
                    "ORDER BY tm.IdAnggota";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableMember.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
            tableMember.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdAnggota");
                obj[1] = connection.result.getString("JumlahGaji");
                obj[2] = connection.result.getString("NamaAnggota");
                String tanggalLahir = connection.result.getString("TanggalLahir");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(tanggalLahir);
                    //Date selected = datechose.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    datechose.setDate(calendar.getTime());
                    Format formatterr = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalLahir = formatterr.format(datechose.getDate());
                    //datechose.setDate(date); // Set tanggal pada JDateChooser
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[3] = tanggalLahir;
                obj[4] = connection.result.getString("JenisKelamin");
                obj[5] = connection.result.getString("Alamat");
                obj[6] = Rupiah.format(connection.result.getInt("TotalSimpanan"));
                obj[7] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
                String masaBerlaku = connection.result.getString("MasaBerlaku");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(masaBerlaku);
                    //Date selected = datechose.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    datechose.setDate(calendar.getTime());
                    Format formatterr = new SimpleDateFormat("yyyy-MM-dd");
                    masaBerlaku = formatterr.format(datechose.getDate());
                    //datechose.setDate(date); // Set tanggal pada JDateChooser
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }
                obj[8] = masaBerlaku;
                if (connection.result.getString("Status").equals("1")) {
                    obj[9] = "Aktif";
                } else {
                    obj[9] = "Tidak Aktif";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Member : " + e);
        }
    }

    public void loadDataNama() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT tm.IdAnggota, tg.JumlahGaji, tm.NamaAnggota, tm.TanggalLahir, " +
                    "tm.JenisKelamin, tm.Alamat,\n" +
                    "\t\ttm.TotalSimpanan, tm.TotalPinjaman,tm.MasaBerlaku,tm.Status\n" +
                    "FROM tbMember tm JOIN tbGaji tg ON tm.IdGaji = tg.IdGaji " +
                    "WHERE tm.NamaAnggota LIKE '%"+textField1.getText()+"%'";
            connection.result = connection.stat.executeQuery(query);

            DecimalFormat Rupiah = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            Rupiah.setDecimalFormatSymbols(formatRp);

            DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
            currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tableMember.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
            tableMember.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("IdAnggota");
                obj[1] = connection.result.getString("JumlahGaji");
                obj[2] = connection.result.getString("NamaAnggota");
                String tanggalLahir = connection.result.getString("TanggalLahir");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(tanggalLahir);
                    //Date selected = datechose.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    datechose.setDate(calendar.getTime());
                    Format formatterr = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalLahir = formatterr.format(datechose.getDate());
                    //datechose.setDate(date); // Set tanggal pada JDateChooser
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }

                obj[3] = tanggalLahir;
                obj[4] = connection.result.getString("JenisKelamin");
                obj[5] = connection.result.getString("Alamat");
                obj[6] = Rupiah.format(connection.result.getInt("TotalSimpanan"));
                obj[7] = Rupiah.format(connection.result.getInt("TotalPinjaman"));
                String masaBerlaku = connection.result.getString("MasaBerlaku");
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(masaBerlaku);
                    //Date selected = datechose.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    datechose.setDate(calendar.getTime());
                    Format formatterr = new SimpleDateFormat("yyyy-MM-dd");
                    masaBerlaku = formatterr.format(datechose.getDate());
                    //datechose.setDate(date); // Set tanggal pada JDateChooser
                } catch (Exception ex) {
                    System.out.println("Terjadi Error pada saat mengambil data date dari kolom" + ex);
                }
                obj[8] = masaBerlaku;
                if (connection.result.getString("Status").equals("1")) {
                    obj[9] = "Aktif";
                } else {
                    obj[9] = "Tidak Aktif";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat meload data Member : " + e);
        }
    }

    public void Clear() {
        autokode();
        txtNamaAnggota.setText("");
        txtAlamat.setText("");
        cbStatus.setSelectedIndex(-1);
        cbGaji.setSelectedIndex(-1);
        datechose.setDate(null);
        FrameKelamin();
        rbLaki.setSelected(false);
        rbPerempuan.setSelected(false);
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnSimpan.setEnabled(true);
    }

    public boolean Validasi() {
        if (txtIdAnggota.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID Anggota tidak boleh kosong");
            return false;
        }
        if (txtNamaAnggota.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama Anggota tidak boleh kosong");
            return false;
        }
        if (datechose == null) {
            JOptionPane.showMessageDialog(null, "Tanggal Lahir tidak boleh kosong");
            return false;
        }
        if (!rbLaki.isSelected() && !rbPerempuan.isSelected()) {
            JOptionPane.showMessageDialog(null, "Pilih jenis kelamin");
            return false;
        }
        if (cbGaji.getSelectedItem().equals("")) {
            JOptionPane.showMessageDialog(null, "Pilih Gaji Anggota");
            return false;
        }
        if (txtAlamat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alamat tidak boleh kosong");
            return false;
        }
        if (cbStatus.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null, "Pilih Status");
            return false;
        }
        return true;
    }
}
