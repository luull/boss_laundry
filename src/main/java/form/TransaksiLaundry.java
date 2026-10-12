package form;

import koneksi.Koneksi;
import koneksi.Session;

import service.TransaksiService;
import service.TransaksiService.Addon;
import service.StrukPrinter;

import ui.Async;
import ui.CardPanel;
import ui.ComboItem;
import ui.DialogHelper;
import ui.DocListener;
import ui.Icons;
import ui.TableDownloader;
import ui.UIFactory;
import ui.UITheme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman Transaksi Laundry (admin/owner): daftar transaksi, cari, filter
 * status, tambah transaksi, ubah status, cetak struk, dan hapus. Perhitungan
 * harga dan penyimpanan memakai {@link TransaksiService} agar tidak ada
 * duplikasi logika bisnis.
 */
public class TransaksiLaundry extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Invoice", "Pelanggan", "Layanan", "Qty",
                    "Add Ons", "Total", "Metode", "Status", "Cabang", "Masuk",
                    "Selesai"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch =
            UIFactory.searchField("Cari invoice / pelanggan / layanan...");
    private final JComboBox<String> cbStatusFilter = new JComboBox<>(
            new String[]{"Semua Status", "proses", "dicuci", "disetrika",
                    "selesai", "diambil"});
    private final JButton btnStatus = UIFactory.warningButton("Ubah Status");
    private final JButton btnCetak = UIFactory.infoButton("Cetak Struk");
    private final JButton btnHapus = UIFactory.dangerButton("Hapus");
    private final JLabel statusLabel = UIFactory.muted("");

    private int selectedId = 0;
    private String selectedInvoice = null;

    public TransaksiLaundry() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        wireEvents();
        loadData();
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = UIFactory.h2("Transaksi Laundry");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = UIFactory.muted("Catat dan kelola transaksi laundry.");
        desc.setFont(UITheme.small());
        desc.setAlignmentX(LEFT_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(desc);
        header.add(titleBox, BorderLayout.WEST);

        JButton btnDownload = UIFactory.successButton("Download PDF");
        UIFactory.withIcon(btnDownload, Icons.PDF);
        btnDownload.addActionListener(e -> TableDownloader.download(this, table,
                "Daftar Transaksi", "Daftar_Transaksi"));
        JButton btnTambah = UIFactory.primaryButton("Transaksi Baru");
        UIFactory.withIcon(btnTambah, Icons.PLUS);
        btnTambah.addActionListener(e -> openForm());
        JPanel hr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        hr.setOpaque(false);
        hr.add(btnDownload);
        hr.add(btnTambah);
        header.add(hr, BorderLayout.EAST);
        top.add(header);
        top.add(Box.createVerticalStrut(14));

        CardPanel toolbar = new CardPanel(new BorderLayout(12, 0));
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(300, 38));
        cbStatusFilter.setPreferredSize(new Dimension(150, 38));
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbStatusFilter.setSelectedIndex(0);
            loadData();
        });
        left.add(txtSearch);
        left.add(cbStatusFilter);
        left.add(btnReset);
        toolbar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        btnStatus.setEnabled(false);
        btnCetak.setEnabled(false);
        btnHapus.setEnabled(false);
        UIFactory.withIcon(btnStatus, Icons.REFRESH);
        UIFactory.withIcon(btnCetak, Icons.PRINT);
        UIFactory.withIcon(btnHapus, Icons.TRASH);
        right.add(btnStatus);
        right.add(btnCetak);
        right.add(btnHapus);
        toolbar.add(right, BorderLayout.EAST);

        top.add(toolbar);
        return top;
    }

    private JPanel buildTable() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(55);
        table.getColumnModel().getColumn(0)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(4).setMaxWidth(70);
        table.getColumnModel().getColumn(4)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(6)
                .setCellRenderer(UIFactory.currencyRenderer());
        table.getColumnModel().getColumn(7).setMaxWidth(100);
        table.getColumnModel().getColumn(7)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(8).setMaxWidth(110);
        table.getColumnModel().getColumn(8)
                .setCellRenderer(UIFactory.statusRenderer());
        card.add(UIFactory.tableScroll(table), BorderLayout.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 0));
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private void wireEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            boolean sel = r >= 0;
            selectedId = sel ? Integer.parseInt(model.getValueAt(r, 0).toString()) : 0;
            selectedInvoice = sel ? model.getValueAt(r, 1).toString() : null;
            btnStatus.setEnabled(sel);
            btnCetak.setEnabled(sel);
            btnHapus.setEnabled(sel);
        });
        btnStatus.addActionListener(e -> ubahStatus());
        btnCetak.addActionListener(e -> cetakStruk());
        btnHapus.addActionListener(e -> hapus());
        txtSearch.addActionListener(e -> loadData());
        txtSearch.getDocument().addDocumentListener(new DocListener(this::loadData));
        cbStatusFilter.addActionListener(e -> loadData());
    }

    private boolean isPrivileged() {
        // Hanya admin yang melihat transaksi seluruh cabang;
        // owner/kasir dibatasi pada cabangnya sendiri.
        return Session.canAccessAllCabang();
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        final String status = cbStatusFilter.getSelectedIndex() == 0
                ? null : (String) cbStatusFilter.getSelectedItem();
        Async.run(() -> query(keyword, status),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    selectedId = 0;
                    selectedInvoice = null;
                    btnStatus.setEnabled(false);
                    btnCetak.setEnabled(false);
                    btnHapus.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Tidak ada transaksi."
                            : rows.size() + " transaksi ditemukan.");
                },
                err -> statusLabel.setText("Gagal memuat data transaksi."));
    }

    private List<Object[]> query(String keyword, String status)
            throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT t.id_transaksi, t.kode_invoice, p.nama_pelanggan, "
                + "l.nama_layanan, t.berat, t.addons, t.total_harga, "
                + "t.metode_bayar, t.status, c.nama_cabang, t.tanggal_masuk, "
                + "t.tanggal_selesai FROM transaksi_laundry t "
                + "JOIN master_pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "JOIN master_layanan l ON t.id_layanan = l.id_layanan "
                + "JOIN master_cabang c ON t.id_cabang = c.id_cabang WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (!isPrivileged()) {
            sql.append(" AND t.id_cabang = ?");
            params.add(Session.idCabang);
        }
        if (!keyword.isEmpty()) {
            sql.append(" AND (t.kode_invoice ILIKE ? OR p.nama_pelanggan ILIKE ? "
                    + "OR l.nama_layanan ILIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (status != null) {
            sql.append(" AND t.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY t.id_transaksi DESC");

        List<Object[]> rows = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id_transaksi"),
                            rs.getString("kode_invoice"),
                            rs.getString("nama_pelanggan"),
                            rs.getString("nama_layanan"),
                            (int) rs.getDouble("berat"),
                            rs.getString("addons"),
                            rs.getDouble("total_harga"),
                            rs.getString("metode_bayar"),
                            rs.getString("status"),
                            rs.getString("nama_cabang"),
                            rs.getString("tanggal_masuk"),
                            rs.getString("tanggal_selesai")});
                }
            }
        }
        return rows;
    }

    // ================= UBAH STATUS =================
    private void ubahStatus() {
        if (selectedInvoice == null) {
            return;
        }
        String[] list = TransaksiService.statusList();
        String status = (String) javax.swing.JOptionPane.showInputDialog(this,
                "Pilih status baru untuk invoice " + selectedInvoice,
                "Ubah Status", javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, list, list[0]);
        if (status == null) {
            return;
        }
        final String invoice = selectedInvoice;
        Async.run(() -> {
            TransaksiService.updateStatus(invoice, status);
            return null;
        }, ok -> {
            DialogHelper.success(this, "Status transaksi berhasil diperbarui.");
            loadData();
        }, err -> DialogHelper.error(this, "Gagal memperbarui status."));
    }

    // ================= CETAK STRUK =================
    private void cetakStruk() {
        if (selectedInvoice == null) {
            return;
        }
        final String invoice = selectedInvoice;
        btnCetak.setEnabled(false);
        Async.run(() -> StrukPrinter.cetak(invoice),
                file -> {
                    btnCetak.setEnabled(true);
                    try {
                        java.awt.Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        DialogHelper.info(this,
                                "Struk tersimpan di:\n" + file.getAbsolutePath());
                    }
                },
                err -> {
                    btnCetak.setEnabled(true);
                    DialogHelper.error(this, "Gagal mencetak struk.");
                });
    }

    // ================= HAPUS =================
    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        if (!DialogHelper.confirmDanger(this,
                "Hapus transaksi " + selectedInvoice + " secara permanen?")) {
            return;
        }
        final int id = selectedId;
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "DELETE FROM transaksi_laundry WHERE id_transaksi=?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }
            return null;
        }, ok -> {
            DialogHelper.success(this, "Transaksi berhasil dihapus.");
            loadData();
        }, err -> DialogHelper.error(this, "Gagal menghapus transaksi."));
    }

    // ================= FORM TRANSAKSI BARU =================
    private void openForm() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Transaksi Baru",
                JDialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel();
        content.setBackground(UITheme.SURFACE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        JLabel title = UIFactory.h2("Transaksi Baru");
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel errorBanner = new JLabel(" ");
        errorBanner.setFont(UITheme.small());
        errorBanner.setForeground(UITheme.DANGER);
        errorBanner.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<ComboItem> cbPelanggan = new JComboBox<>();
        JComboBox<ComboItem> cbLayanan = new JComboBox<>();
        JComboBox<ComboItem> cbCabang = new JComboBox<>();
        JTextField txtQty = UIFactory.textField();
        JTextField txtHarga = UIFactory.textField();
        JComboBox<String> cbStatus =
                new JComboBox<>(TransaksiService.statusList());
        JComboBox<String> cbMetode =
                new JComboBox<>(TransaksiService.metodeList());
        JTextField txtBayar = UIFactory.textField();
        JLabel lblKembalian = UIFactory.h3("Kembalian: Rp 0");
        lblKembalian.setForeground(UITheme.PRIMARY_PRESSED);
        lblKembalian.setAlignmentX(LEFT_ALIGNMENT);

        sizeField(cbPelanggan, cbLayanan, cbCabang, txtQty, txtHarga, cbStatus,
                cbMetode, txtBayar);

        // Add-ons grid
        Map<Addon, JCheckBox> checks = new EnumMap<>(Addon.class);
        Map<Addon, JTextField> qtys = new EnumMap<>(Addon.class);
        JPanel addonsPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        addonsPanel.setOpaque(false);
        for (Addon a : Addon.values()) {
            JPanel rowp = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            rowp.setOpaque(false);
            JCheckBox cb = new JCheckBox(a.getLabel() + " ("
                    + UIFactory.rupiah(a.getHarga()) + ")");
            cb.setOpaque(false);
            cb.setFont(UITheme.body());
            JTextField q = UIFactory.textField();
            q.setText("1");
            q.setPreferredSize(new Dimension(48, 34));
            q.setHorizontalAlignment(SwingConstants.CENTER);
            q.setEnabled(false);
            cb.addActionListener(e -> q.setEnabled(cb.isSelected()));
            checks.put(a, cb);
            qtys.put(a, q);
            rowp.add(cb);
            rowp.add(q);
            addonsPanel.add(rowp);
        }

        JLabel lblTotal = UIFactory.h2("Total: Rp 0");
        lblTotal.setForeground(UITheme.SUCCESS_HOVER);
        JButton btnHitung = UIFactory.secondaryButton("Hitung");

        // Layout rows
        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(errorBanner);
        content.add(Box.createVerticalStrut(10));
        content.add(formRow("Pelanggan", cbPelanggan));
        content.add(formRow("Layanan (Kiloan)", cbLayanan));
        content.add(labelFor("Add Ons"));
        addonsPanel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(addonsPanel);
        content.add(Box.createVerticalStrut(12));
        content.add(formRow("Qty / Kg", txtQty));
        content.add(formRow("Harga Satuan", txtHarga));
        content.add(formRow("Cabang", cbCabang));
        content.add(formRow("Status Awal", cbStatus));
        content.add(Box.createVerticalStrut(10));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        totalRow.add(lblTotal, BorderLayout.WEST);
        totalRow.add(btnHitung, BorderLayout.EAST);
        content.add(totalRow);
        content.add(Box.createVerticalStrut(14));

        // ================= PEMBAYARAN =================
        content.add(formRow("Metode Pembayaran", cbMetode));
        JPanel cashPanel = new JPanel();
        cashPanel.setOpaque(false);
        cashPanel.setLayout(new BoxLayout(cashPanel, BoxLayout.Y_AXIS));
        cashPanel.setAlignmentX(LEFT_ALIGNMENT);
        cashPanel.add(formRow("Uang Diterima", txtBayar));
        cashPanel.add(lblKembalian);
        content.add(cashPanel);
        content.add(Box.createVerticalStrut(16));

        JButton btnSimpan = UIFactory.primaryButton("Simpan Transaksi");
        JButton btnBatal = UIFactory.secondaryButton("Batal");
        btnBatal.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        footer.add(btnBatal);
        footer.add(btnSimpan);
        content.add(footer);

        // Data loading
        loadPelanggan(cbPelanggan);
        loadCabang(cbCabang);
        loadLayanan(cbLayanan, txtHarga);

        java.util.function.DoubleSupplier totalCalc = () -> {
            try {
                double harga = Double.parseDouble(txtHarga.getText().trim());
                double qty = Double.parseDouble(txtQty.getText().trim());
                return TransaksiService.hitungTotal(harga, qty,
                        collectAddons(checks, qtys));
            } catch (Exception ex) {
                return -1;
            }
        };
        Runnable refreshKembalian = () -> {
            if (!TransaksiService.isTunai((String) cbMetode.getSelectedItem())) {
                lblKembalian.setText("Kembalian: Rp 0");
                return;
            }
            double total = totalCalc.getAsDouble();
            double bayar;
            try {
                bayar = Double.parseDouble(txtBayar.getText().trim());
            } catch (Exception ex) {
                lblKembalian.setText("Kembalian: Rp 0");
                return;
            }
            lblKembalian.setText("Kembalian: " + (total < 0 ? "Rp 0"
                    : UIFactory.rupiah(Math.max(0, bayar - total))));
        };
        Runnable collectAndCompute = () -> {
            double total = totalCalc.getAsDouble();
            lblTotal.setText("Total: "
                    + (total < 0 ? "Rp 0" : UIFactory.rupiah(total)));
            if (total >= 0) {
                errorBanner.setText(" ");
            }
            refreshKembalian.run();
        };
        Runnable updatePayment = () -> {
            boolean tunai = TransaksiService.isTunai(
                    (String) cbMetode.getSelectedItem());
            cashPanel.setVisible(tunai);
            refreshKembalian.run();
            cashPanel.revalidate();
            cashPanel.repaint();
        };
        btnHitung.addActionListener(e -> collectAndCompute.run());
        cbMetode.addActionListener(e -> updatePayment.run());
        txtBayar.getDocument().addDocumentListener(
                new DocListener(refreshKembalian));
        updatePayment.run();

        final boolean[] saving = {false};
        btnSimpan.addActionListener(e -> {
            if (saving[0]) {
                return;
            }
            errorBanner.setText(" ");
            if (cbPelanggan.getSelectedItem() == null) {
                errorBanner.setText("Pelanggan wajib dipilih.");
                return;
            }
            if (cbLayanan.getSelectedItem() == null) {
                errorBanner.setText("Layanan wajib dipilih.");
                return;
            }
            if (cbCabang.getSelectedItem() == null) {
                errorBanner.setText("Cabang wajib dipilih.");
                return;
            }
            double qty;
            double harga;
            try {
                qty = Double.parseDouble(txtQty.getText().trim());
                harga = Double.parseDouble(txtHarga.getText().trim());
            } catch (NumberFormatException ex) {
                errorBanner.setText("Qty dan harga harus berupa angka.");
                return;
            }
            if (qty <= 0) {
                errorBanner.setText("Qty / Kg harus lebih dari 0.");
                return;
            }
            Map<Addon, Integer> addons;
            try {
                addons = collectAddons(checks, qtys);
            } catch (NumberFormatException ex) {
                errorBanner.setText("Jumlah add-ons harus berupa angka.");
                return;
            }

            int idPelanggan = ((ComboItem) cbPelanggan.getSelectedItem()).getId();
            int idLayanan = ((ComboItem) cbLayanan.getSelectedItem()).getId();
            int idCabang = ((ComboItem) cbCabang.getSelectedItem()).getId();
            String status = (String) cbStatus.getSelectedItem();

            // ---- Pembayaran ----
            String metode = (String) cbMetode.getSelectedItem();
            double uangDiterima = 0;
            if (TransaksiService.isTunai(metode)) {
                double total = TransaksiService.hitungTotal(harga, qty, addons);
                try {
                    uangDiterima = Double.parseDouble(txtBayar.getText().trim());
                } catch (NumberFormatException ex) {
                    errorBanner.setText("Masukkan jumlah uang yang diterima.");
                    return;
                }
                if (uangDiterima < total) {
                    errorBanner.setText("Uang diterima kurang dari total ("
                            + UIFactory.rupiah(total) + ").");
                    return;
                }
            }
            final double uang = uangDiterima;

            saving[0] = true;
            btnSimpan.setEnabled(false);
            btnSimpan.setText("Menyimpan...");
            Async.run(() -> TransaksiService.simpan(idPelanggan, idLayanan, qty,
                    harga, addons, idCabang, status, metode, uang),
                    invoice -> {
                        dialog.dispose();
                        DialogHelper.success(this,
                                "Transaksi berhasil disimpan.\nInvoice: " + invoice);
                        loadData();
                    },
                    err -> {
                        saving[0] = false;
                        btnSimpan.setEnabled(true);
                        btnSimpan.setText("Simpan Transaksi");
                        errorBanner.setText("Gagal menyimpan transaksi. Coba lagi.");
                    });
        });

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        dialog.setContentPane(scroll);
        dialog.getRootPane().setDefaultButton(btnSimpan);
        dialog.setSize(560, Math.min(820, owner != null
                ? owner.getHeight() - 60 : 820));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private Map<Addon, Integer> collectAddons(Map<Addon, JCheckBox> checks,
            Map<Addon, JTextField> qtys) {
        Map<Addon, Integer> map = new LinkedHashMap<>();
        for (Addon a : Addon.values()) {
            if (checks.get(a).isSelected()) {
                int q = Integer.parseInt(qtys.get(a).getText().trim());
                map.put(a, q);
            }
        }
        return map;
    }

    private void sizeField(javax.swing.JComponent... fields) {
        for (javax.swing.JComponent f : fields) {
            f.setPreferredSize(new Dimension(300, 40));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        }
    }

    private JPanel labelFor(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.add(UIFactory.fieldLabel(text));
        return p;
    }

    private JPanel formRow(String label, javax.swing.JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        p.add(UIFactory.fieldLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return p;
    }

    // ================= COMBO LOADERS =================
    private void loadPelanggan(JComboBox<ComboItem> combo) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            String sql = isPrivileged()
                    ? "SELECT id_pelanggan, nama_pelanggan FROM master_pelanggan "
                      + "ORDER BY nama_pelanggan"
                    : "SELECT id_pelanggan, nama_pelanggan FROM master_pelanggan "
                      + "WHERE id_cabang=? ORDER BY nama_pelanggan";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!isPrivileged()) {
                    pst.setInt(1, Session.idCabang);
                }
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        items.add(new ComboItem(rs.getInt("id_pelanggan"),
                                rs.getString("nama_pelanggan")));
                    }
                }
            }
            return items;
        }, items -> items.forEach(combo::addItem), err -> { });
    }

    private void loadCabang(JComboBox<ComboItem> combo) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            // Owner/kasir hanya boleh membuat transaksi untuk cabangnya.
            StringBuilder sql = new StringBuilder(
                    "SELECT id_cabang, nama_cabang FROM master_cabang "
                    + "WHERE status='aktif'");
            if (!isPrivileged()) {
                sql.append(" AND id_cabang = ").append(Session.idCabang);
            }
            sql.append(" ORDER BY nama_cabang");
            try (PreparedStatement pst = conn.prepareStatement(sql.toString());
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    items.add(new ComboItem(rs.getInt("id_cabang"),
                            rs.getString("nama_cabang")));
                }
            }
            return items;
        }, items -> {
            items.forEach(combo::addItem);
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).getId() == Session.idCabang) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }, err -> { });
    }

    private void loadLayanan(JComboBox<ComboItem> combo, JTextField txtHarga) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<Object[]> items = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT id_layanan, nama_layanan, harga_min, harga_max "
                    + "FROM master_layanan WHERE kategori='kiloan' "
                    + "AND status='aktif' ORDER BY nama_layanan");
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    items.add(new Object[]{rs.getInt("id_layanan"),
                            rs.getString("nama_layanan"),
                            rs.getDouble("harga_min"),
                            rs.getDouble("harga_max")});
                }
            }
            return items;
        }, items -> {
            Map<Integer, double[]> hargaMap = new LinkedHashMap<>();
            combo.removeAllItems();
            for (Object[] it : items) {
                int id = (int) it[0];
                combo.addItem(new ComboItem(id, (String) it[1]));
                hargaMap.put(id, new double[]{(double) it[2], (double) it[3]});
            }
            Runnable applyHarga = () -> {
                ComboItem sel = (ComboItem) combo.getSelectedItem();
                if (sel == null) {
                    return;
                }
                double[] h = hargaMap.get(sel.getId());
                if (h != null) {
                    txtHarga.setText(String.valueOf((int) h[0]));
                    txtHarga.setEnabled(h[0] != h[1]);
                }
            };
            combo.addActionListener(e -> applyHarga.run());
            applyHarga.run();
        }, err -> { });
    }
}
