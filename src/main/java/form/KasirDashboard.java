package form;

import com.formdev.flatlaf.FlatClientProperties;

import koneksi.Koneksi;
import koneksi.Session;

import service.StrukPrinter;
import service.TransaksiService;
import service.TransaksiService.Addon;

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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard Kasir: layar POS satu halaman yang dirancang untuk alur kerja kasir
 * yang cepat. Entri order di kiri, panel pembayaran di kanan, dan daftar
 * transaksi di bawah. Total dan kembalian terhitung otomatis (tanpa tombol
 * Hitung) dan seluruh form tampil tanpa perlu scroll.
 */
public class KasirDashboard extends JFrame {

    private final JComboBox<ComboItem> cbPelanggan = new JComboBox<>();
    private final JComboBox<ComboItem> cbLayanan = new JComboBox<>();
    private final JComboBox<String> cbQtyType =
            new JComboBox<>(new String[]{"Kg", "Pcs"});
    private final JTextField txtQty = UIFactory.textField();
    private final JTextField txtHarga = UIFactory.textField();
    private final JLabel lblTotal = UIFactory.bigValue("Rp 0");

    private String metodeSelected = "Tunai";
    private JButton[] metodeBtns;
    private final JTextField txtBayar = UIFactory.textField();
    private final JLabel lblKembalian = UIFactory.bigValue("Rp 0");
    private JPanel cashPanel;

    private final Map<Addon, JCheckBox> addonChecks = new EnumMap<>(Addon.class);
    private final Map<Addon, JTextField> addonQty = new EnumMap<>(Addon.class);
    private final Map<Integer, double[]> hargaMap = new LinkedHashMap<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Invoice", "Pelanggan", "Layanan", "Qty", "Add Ons",
                    "Total", "Metode", "Status"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField txtSearch =
            UIFactory.searchField("Cari invoice / pelanggan...");
    private final JButton btnStatus = UIFactory.warningButton("Ubah Status");
    private final JButton btnCetak = UIFactory.infoButton("Cetak Struk");
    private final JLabel statusLabel = UIFactory.muted("");
    private JButton btnSimpan;

    private boolean saving = false;

    public KasirDashboard() {
        UITheme.init();
        setTitle("BOSS Laundry - Kasir");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        setContentPane(root);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1360, 860);
        setLocationRelativeTo(null);

        loadPelanggan();
        loadLayanan();
        loadData();

        setVisible(true);
    }

    // ================= HEADER =================
    private Header buildHeader() {
        Header bar = new Header();
        bar.setLayout(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        bar.setPreferredSize(new Dimension(10, 66));

        JPanel leftp = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftp.setOpaque(false);
        JLabel logo = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/image/logo.png");
        if (logoUrl != null) {
            logo.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon(logoUrl)
                    .getImage().getScaledInstance(34, 34, java.awt.Image.SCALE_SMOOTH)));
        }
        JLabel brand = new JLabel("BOSS LAUNDRY");
        brand.setForeground(Color.WHITE);
        brand.setFont(UITheme.font(java.awt.Font.BOLD, 20));
        JLabel sub = new JLabel("  Kasir");
        sub.setForeground(new Color(0xBFDBFE));
        sub.setFont(UITheme.font(java.awt.Font.PLAIN, 14));
        leftp.add(logo);
        leftp.add(brand);
        leftp.add(sub);
        bar.add(leftp, BorderLayout.WEST);

        JPanel rightp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightp.setOpaque(false);
        JLabel user = new JLabel(sessionUserLabel());
        user.setForeground(Color.WHITE);
        user.setFont(UITheme.bodyBold());
        JButton logout = UIFactory.secondaryButton("Logout");
        UIFactory.withIcon(logout, Icons.LOGOUT);
        logout.addActionListener(e -> logout());
        rightp.add(user);
        rightp.add(logout);
        bar.add(rightp, BorderLayout.EAST);
        return bar;
    }

    private String sessionUserLabel() {
        String u = Session.username == null ? "-" : Session.username;
        return u + "  |  " + java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy",
                        new java.util.Locale("id", "ID")));
    }

    // ================= BODY =================
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(UITheme.BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel topRow = new JPanel(new BorderLayout(16, 0));
        topRow.setOpaque(false);
        JComponent entry = buildEntryCard();
        JComponent payment = buildPaymentCard();
        entry.setPreferredSize(new Dimension(100, 430));
        payment.setPreferredSize(new Dimension(430, 430));
        topRow.add(entry, BorderLayout.CENTER);
        topRow.add(payment, BorderLayout.EAST);

        body.add(topRow, BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);
        return body;
    }

    // ================= ENTRY (ORDER) =================
    private JComponent buildEntryCard() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel g = new JPanel(new GridBagLayout());
        g.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 12, 0);
        g.add(UIFactory.h3("Detail Order"), c);

        // Baris 1: Pelanggan | Layanan
        JButton btnPelangganBaru = UIFactory.infoButton("+ Baru");
        btnPelangganBaru.addActionListener(e -> tambahPelanggan());
        JPanel pelRow = new JPanel(new BorderLayout(8, 0));
        pelRow.setOpaque(false);
        cbPelanggan.setPreferredSize(new Dimension(10, 40));
        pelRow.add(cbPelanggan, BorderLayout.CENTER);
        pelRow.add(btnPelangganBaru, BorderLayout.EAST);
        cbLayanan.setPreferredSize(new Dimension(10, 40));

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 4, 8);
        g.add(UIFactory.fieldLabel("Pelanggan"), c);
        c.gridx = 1;
        c.weightx = 0.45;
        c.insets = new Insets(0, 0, 4, 0);
        g.add(UIFactory.fieldLabel("Layanan (Kiloan)"), c);

        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 14, 8);
        g.add(pelRow, c);
        c.gridx = 1;
        c.weightx = 0.45;
        c.insets = new Insets(0, 0, 14, 0);
        g.add(cbLayanan, c);

        // Baris 2: Qty/Kg | Harga
        JPanel qtyWrap = new JPanel(new BorderLayout(8, 0));
        qtyWrap.setOpaque(false);
        cbQtyType.setPreferredSize(new Dimension(78, 40));
        txtQty.setPreferredSize(new Dimension(10, 40));
        txtQty.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        qtyWrap.add(cbQtyType, BorderLayout.WEST);
        qtyWrap.add(txtQty, BorderLayout.CENTER);
        txtHarga.setPreferredSize(new Dimension(10, 40));
        txtHarga.setEnabled(false);

        c.gridy = 3;
        c.gridx = 0;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 4, 8);
        g.add(UIFactory.fieldLabel("Qty / Jumlah"), c);
        c.gridx = 1;
        c.weightx = 0.45;
        c.insets = new Insets(0, 0, 4, 0);
        g.add(UIFactory.fieldLabel("Harga Satuan"), c);

        c.gridy = 4;
        c.gridx = 0;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 14, 8);
        g.add(qtyWrap, c);
        c.gridx = 1;
        c.weightx = 0.45;
        c.insets = new Insets(0, 0, 14, 0);
        g.add(txtHarga, c);

        // Add-ons
        c.gridy = 5;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 6, 0);
        g.add(UIFactory.fieldLabel("Add-Ons (opsional)"), c);

        JPanel addonsGrid = new JPanel(new GridLayout(2, 3, 10, 8));
        addonsGrid.setOpaque(false);
        for (Addon a : Addon.values()) {
            addonsGrid.add(buildAddonCell(a));
        }
        c.gridy = 6;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        g.add(addonsGrid, c);

        card.add(g, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildAddonCell(Addon a) {
        JPanel cell = new JPanel(new BorderLayout(6, 0));
        cell.setOpaque(true);
        cell.setBackground(UITheme.SURFACE_ALT);
        cell.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JCheckBox cb = new JCheckBox("<html>" + a.getLabel() + "<br><span style='color:#64748b'>"
                + UIFactory.rupiah(a.getHarga()) + "</span></html>");
        cb.setOpaque(false);
        cb.setFont(UITheme.small());
        JTextField q = UIFactory.textField();
        q.setText("1");
        q.setPreferredSize(new Dimension(42, 30));
        q.setHorizontalAlignment(SwingConstants.CENTER);
        q.setEnabled(false);
        cb.addActionListener(e -> {
            q.setEnabled(cb.isSelected());
            cell.setBackground(cb.isSelected()
                    ? UITheme.PRIMARY_SOFT : UITheme.SURFACE_ALT);
            recompute();
        });
        q.getDocument().addDocumentListener(new DocListener(this::recompute));
        addonChecks.put(a, cb);
        addonQty.put(a, q);
        cell.add(cb, BorderLayout.CENTER);
        cell.add(q, BorderLayout.EAST);
        return cell;
    }

    // ================= PAYMENT =================
    private JComponent buildPaymentCard() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel g = new JPanel(new GridBagLayout());
        g.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 10, 0);
        g.add(UIFactory.h3("Pembayaran"), c);

        // Total card
        CardPanel totalCard = new CardPanel(new BorderLayout());
        totalCard.fill(UITheme.SUCCESS_SOFT).border(UITheme.SUCCESS_SOFT)
                .shadow(false);
        totalCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JPanel tcol = new JPanel();
        tcol.setOpaque(false);
        tcol.setLayout(new BoxLayout(tcol, BoxLayout.Y_AXIS));
        JLabel tcap = UIFactory.muted("TOTAL TAGIHAN");
        tcap.setFont(UITheme.smallBold());
        tcap.setForeground(UITheme.SUCCESS_HOVER);
        lblTotal.setForeground(UITheme.SUCCESS_HOVER);
        tcol.add(tcap);
        tcol.add(lblTotal);
        totalCard.add(tcol, BorderLayout.WEST);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 14, 0);
        g.add(totalCard, c);

        // Metode
        c.gridy = 2;
        c.insets = new Insets(0, 0, 6, 0);
        g.add(UIFactory.fieldLabel("Metode Pembayaran"), c);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 12, 0);
        g.add(buildMetodeSelector(), c);

        // Cash panel
        cashPanel = new JPanel(new GridBagLayout());
        cashPanel.setOpaque(false);
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0;
        cc.gridy = 0;
        cc.weightx = 1;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.insets = new Insets(0, 0, 6, 0);
        cashPanel.add(UIFactory.fieldLabel("Uang Diterima"), cc);
        txtBayar.setPreferredSize(new Dimension(10, 42));
        txtBayar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Masukkan nominal uang");
        txtBayar.getDocument().addDocumentListener(
                new DocListener(this::updateKembalian));
        cc.gridy = 1;
        cc.insets = new Insets(0, 0, 10, 0);
        cashPanel.add(txtBayar, cc);

        CardPanel kembalianCard = new CardPanel(new BorderLayout());
        kembalianCard.fill(UITheme.PRIMARY_SOFT).border(UITheme.PRIMARY_SOFT)
                .shadow(false);
        kembalianCard.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JPanel kcol = new JPanel();
        kcol.setOpaque(false);
        kcol.setLayout(new BoxLayout(kcol, BoxLayout.Y_AXIS));
        JLabel kcap = UIFactory.muted("KEMBALIAN");
        kcap.setFont(UITheme.smallBold());
        kcap.setForeground(UITheme.PRIMARY_PRESSED);
        lblKembalian.setForeground(UITheme.PRIMARY_PRESSED);
        kcol.add(kcap);
        kcol.add(lblKembalian);
        kembalianCard.add(kcol, BorderLayout.WEST);
        cc.gridy = 2;
        cc.insets = new Insets(0, 0, 0, 0);
        cashPanel.add(kembalianCard, cc);

        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        g.add(cashPanel, c);

        // Buttons
        JPanel btns = new JPanel(new GridBagLayout());
        btns.setOpaque(false);
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridy = 0;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(0, 0, 0, 8);
        bc.gridx = 0;
        bc.weightx = 0.35;
        JButton btnReset = UIFactory.secondaryButton("Reset");
        btnReset.setPreferredSize(new Dimension(10, 48));
        btnReset.addActionListener(e -> resetForm());
        btns.add(btnReset, bc);
        bc.gridx = 1;
        bc.weightx = 0.65;
        bc.insets = new Insets(0, 0, 0, 0);
        btnSimpan = UIFactory.successButton("Simpan Transaksi");
        UIFactory.withIcon(btnSimpan, Icons.CHECK);
        btnSimpan.setPreferredSize(new Dimension(10, 48));
        btnSimpan.addActionListener(e -> simpan());
        btns.add(btnSimpan, bc);

        c.gridy = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.insets = new Insets(12, 0, 0, 0);
        g.add(btns, c);

        card.add(g, BorderLayout.CENTER);
        updatePaymentVisibility();
        return card;
    }

    private JComponent buildMetodeSelector() {
        String[] ms = TransaksiService.metodeList();
        metodeBtns = new JButton[ms.length];
        JPanel p = new JPanel(new GridLayout(1, ms.length, 8, 0));
        p.setOpaque(false);
        for (int i = 0; i < ms.length; i++) {
            final String m = ms[i];
            JButton b = new JButton(m);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setFont(UITheme.smallBold());
            b.setPreferredSize(new Dimension(10, 42));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.putClientProperty(FlatClientProperties.STYLE,
                    "arc:10;borderWidth:0;focusWidth:0");
            b.addActionListener(e -> {
                metodeSelected = m;
                restyleMetode();
                updatePaymentVisibility();
            });
            metodeBtns[i] = b;
            p.add(b);
        }
        restyleMetode();
        return p;
    }

    private void restyleMetode() {
        if (metodeBtns == null) {
            return;
        }
        for (JButton b : metodeBtns) {
            boolean sel = b.getText().equals(metodeSelected);
            b.setBackground(sel ? UITheme.PRIMARY : new Color(0xE8EDF3));
            b.setForeground(sel ? Color.WHITE : UITheme.TEXT);
        }
    }

    // ================= TABLE =================
    private JComponent buildTableCard() {
        CardPanel card = new CardPanel(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);
        JPanel tl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tl.setOpaque(false);
        tl.add(UIFactory.h3("Daftar Transaksi Cabang"));
        toolbar.add(tl, BorderLayout.WEST);

        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tr.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(240, 36));
        btnStatus.setEnabled(false);
        btnCetak.setEnabled(false);
        UIFactory.withIcon(btnStatus, Icons.REFRESH);
        UIFactory.withIcon(btnCetak, Icons.PRINT);
        JButton btnDownload = UIFactory.successButton("Download PDF");
        UIFactory.withIcon(btnDownload, Icons.PDF);
        btnDownload.addActionListener(e -> TableDownloader.download(this, table,
                "Daftar Transaksi", "Transaksi_Kasir"));
        JButton btnRefresh = UIFactory.secondaryButton("Refresh");
        UIFactory.withIcon(btnRefresh, Icons.REFRESH);
        btnRefresh.addActionListener(e -> loadData());
        tr.add(txtSearch);
        tr.add(btnStatus);
        tr.add(btnCetak);
        tr.add(btnDownload);
        tr.add(btnRefresh);
        toolbar.add(tr, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(3).setMaxWidth(70);
        table.getColumnModel().getColumn(3)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(5)
                .setCellRenderer(UIFactory.currencyRenderer());
        table.getColumnModel().getColumn(6).setMaxWidth(100);
        table.getColumnModel().getColumn(6)
                .setCellRenderer(UIFactory.centerRenderer());
        table.getColumnModel().getColumn(7).setMaxWidth(110);
        table.getColumnModel().getColumn(7)
                .setCellRenderer(UIFactory.statusRenderer());
        card.add(UIFactory.tableScroll(table), BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));
        card.add(statusLabel, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = table.getSelectedRow() >= 0;
            btnStatus.setEnabled(sel);
            btnCetak.setEnabled(sel);
        });
        btnStatus.addActionListener(e -> ubahStatus());
        btnCetak.addActionListener(e -> cetakStruk());
        txtSearch.getDocument().addDocumentListener(new DocListener(this::loadData));

        // Hitung ulang total saat input order berubah.
        cbLayanan.addActionListener(e -> {
            applyHarga();
            recompute();
        });
        txtQty.getDocument().addDocumentListener(new DocListener(this::recompute));
        txtHarga.getDocument().addDocumentListener(new DocListener(this::recompute));
        return card;
    }

    // ================= PERHITUNGAN =================
    private void recompute() {
        double total = computeTotalSafe();
        lblTotal.setText(total < 0 ? "Rp 0" : UIFactory.rupiah(total));
        updateKembalian();
    }

    private double computeTotalSafe() {
        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            double qty = Double.parseDouble(txtQty.getText().trim());
            return TransaksiService.hitungTotal(harga, qty, collectAddons());
        } catch (Exception ex) {
            return -1;
        }
    }

    private void updatePaymentVisibility() {
        boolean tunai = TransaksiService.isTunai(metodeSelected);
        if (cashPanel != null) {
            cashPanel.setVisible(tunai);
            cashPanel.revalidate();
            cashPanel.repaint();
        }
        updateKembalian();
    }

    private void updateKembalian() {
        if (!TransaksiService.isTunai(metodeSelected)) {
            lblKembalian.setText("Rp 0");
            return;
        }
        double total = computeTotalSafe();
        double bayar;
        try {
            bayar = Double.parseDouble(txtBayar.getText().trim());
        } catch (Exception ex) {
            lblKembalian.setText("Rp 0");
            return;
        }
        lblKembalian.setText(total < 0 ? "Rp 0"
                : UIFactory.rupiah(Math.max(0, bayar - total)));
    }

    // ================= SIMPAN =================
    private void simpan() {
        if (saving) {
            return;
        }
        if (cbPelanggan.getSelectedItem() == null) {
            DialogHelper.warn(this, "Pilih pelanggan terlebih dahulu.");
            return;
        }
        if (cbLayanan.getSelectedItem() == null) {
            DialogHelper.warn(this, "Pilih layanan terlebih dahulu.");
            return;
        }
        double qty;
        double harga;
        try {
            qty = Double.parseDouble(txtQty.getText().trim());
            harga = Double.parseDouble(txtHarga.getText().trim());
        } catch (NumberFormatException ex) {
            DialogHelper.warn(this, "Qty dan harga harus berupa angka.");
            return;
        }
        if (qty <= 0) {
            DialogHelper.warn(this, "Qty harus lebih dari 0.");
            return;
        }
        Map<Addon, Integer> addons;
        try {
            addons = collectAddons();
        } catch (NumberFormatException ex) {
            DialogHelper.warn(this, "Jumlah add-ons harus berupa angka.");
            return;
        }

        int idPelanggan = ((ComboItem) cbPelanggan.getSelectedItem()).getId();
        int idLayanan = ((ComboItem) cbLayanan.getSelectedItem()).getId();

        String metode = metodeSelected;
        double uangDiterima = 0;
        if (TransaksiService.isTunai(metode)) {
            double total = TransaksiService.hitungTotal(harga, qty, addons);
            try {
                uangDiterima = Double.parseDouble(txtBayar.getText().trim());
            } catch (NumberFormatException ex) {
                DialogHelper.warn(this, "Masukkan jumlah uang yang diterima.");
                return;
            }
            if (uangDiterima < total) {
                DialogHelper.warn(this, "Uang diterima kurang dari total ("
                        + UIFactory.rupiah(total) + ").");
                return;
            }
        }
        final double uang = uangDiterima;

        saving = true;
        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");
        Async.run(() -> TransaksiService.simpan(idPelanggan, idLayanan, qty,
                harga, addons, Session.idCabang, "proses", metode, uang),
                invoice -> {
                    saving = false;
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Transaksi");
                    DialogHelper.success(this,
                            "Transaksi berhasil disimpan.\nInvoice: " + invoice);
                    resetForm();
                    loadData();
                },
                err -> {
                    saving = false;
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Transaksi");
                    DialogHelper.error(this, "Gagal menyimpan transaksi. Coba lagi.");
                });
    }

    private Map<Addon, Integer> collectAddons() {
        Map<Addon, Integer> map = new LinkedHashMap<>();
        for (Addon a : Addon.values()) {
            if (addonChecks.get(a).isSelected()) {
                map.put(a, Integer.parseInt(addonQty.get(a).getText().trim()));
            }
        }
        return map;
    }

    private void resetForm() {
        txtQty.setText("");
        txtBayar.setText("");
        lblTotal.setText("Rp 0");
        lblKembalian.setText("Rp 0");
        metodeSelected = "Tunai";
        restyleMetode();
        updatePaymentVisibility();
        for (Addon a : Addon.values()) {
            addonChecks.get(a).setSelected(false);
            addonQty.get(a).setText("1");
            addonQty.get(a).setEnabled(false);
        }
    }

    // ================= AKSI TABEL =================
    private void ubahStatus() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        String invoice = model.getValueAt(r, 0).toString();
        String[] list = TransaksiService.statusList();
        String status = (String) JOptionPane.showInputDialog(this,
                "Pilih status baru untuk " + invoice, "Ubah Status",
                JOptionPane.QUESTION_MESSAGE, null, list, list[0]);
        if (status == null) {
            return;
        }
        Async.run(() -> {
            TransaksiService.updateStatus(invoice, status);
            return null;
        }, ok -> {
            DialogHelper.success(this, "Status berhasil diperbarui.");
            loadData();
        }, err -> DialogHelper.error(this, "Gagal memperbarui status."));
    }

    private void cetakStruk() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        String invoice = model.getValueAt(r, 0).toString();
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

    private void tambahPelanggan() {
        String nama = JOptionPane.showInputDialog(this,
                "Nama pelanggan baru:", "Tambah Pelanggan",
                JOptionPane.PLAIN_MESSAGE);
        if (nama == null || nama.trim().isEmpty()) {
            return;
        }
        final String n = nama.trim();
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO master_pelanggan (nama_pelanggan,id_cabang) "
                    + "VALUES(?,?) RETURNING id_pelanggan")) {
                pst.setString(1, n);
                pst.setInt(2, Session.idCabang);
                try (ResultSet rs = pst.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            }
        }, newId -> {
            DialogHelper.success(this,
                    "Pelanggan \"" + n + "\" berhasil ditambahkan.");
            loadPelanggan(newId);
        }, err -> DialogHelper.error(this, "Gagal menambah pelanggan."));
    }

    private void logout() {
        if (!DialogHelper.confirm(this, "Keluar dari aplikasi sekarang?",
                "Logout")) {
            return;
        }
        Session.idUser = 0;
        Session.username = null;
        Session.role = null;
        Session.idCabang = 0;
        dispose();
        new LoginForm();
    }

    // ================= DATA =================
    private void loadData() {
        statusLabel.setText("Memuat data...");
        final String keyword = txtSearch.getText().trim();
        Async.run(() -> query(keyword),
                rows -> {
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                    btnStatus.setEnabled(false);
                    btnCetak.setEnabled(false);
                    statusLabel.setText(rows.isEmpty()
                            ? "Belum ada transaksi di cabang ini."
                            : rows.size() + " transaksi.");
                },
                err -> statusLabel.setText("Gagal memuat data transaksi."));
    }

    private List<Object[]> query(String keyword) throws Exception {
        Connection conn = Koneksi.getKoneksi();
        StringBuilder sql = new StringBuilder(
                "SELECT t.kode_invoice, p.nama_pelanggan, l.nama_layanan, "
                + "t.berat, t.addons, t.total_harga, t.metode_bayar, t.status "
                + "FROM transaksi_laundry t "
                + "JOIN master_pelanggan p ON t.id_pelanggan=p.id_pelanggan "
                + "JOIN master_layanan l ON t.id_layanan=l.id_layanan "
                + "WHERE t.id_cabang=?");
        List<Object> params = new ArrayList<>();
        params.add(Session.idCabang);
        if (!keyword.isEmpty()) {
            sql.append(" AND (t.kode_invoice ILIKE ? OR p.nama_pelanggan ILIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
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
                            rs.getString("kode_invoice"),
                            rs.getString("nama_pelanggan"),
                            rs.getString("nama_layanan"),
                            (int) rs.getDouble("berat"),
                            rs.getString("addons"),
                            rs.getDouble("total_harga"),
                            rs.getString("metode_bayar"),
                            rs.getString("status")});
                }
            }
        }
        return rows;
    }

    private void loadPelanggan() {
        loadPelanggan(null);
    }

    private void loadPelanggan(Integer selectId) {
        Async.run(() -> {
            Connection conn = Koneksi.getKoneksi();
            List<ComboItem> items = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT id_pelanggan, nama_pelanggan FROM master_pelanggan "
                    + "WHERE id_cabang=? ORDER BY nama_pelanggan")) {
                pst.setInt(1, Session.idCabang);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        items.add(new ComboItem(rs.getInt("id_pelanggan"),
                                rs.getString("nama_pelanggan")));
                    }
                }
            }
            return items;
        }, items -> {
            cbPelanggan.removeAllItems();
            for (ComboItem it : items) {
                cbPelanggan.addItem(it);
            }
            if (selectId != null && selectId > 0) {
                for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
                    if (cbPelanggan.getItemAt(i).getId() == selectId) {
                        cbPelanggan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }, err -> { });
    }

    private void loadLayanan() {
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
                            rs.getDouble("harga_min"), rs.getDouble("harga_max")});
                }
            }
            return items;
        }, items -> {
            hargaMap.clear();
            cbLayanan.removeAllItems();
            for (Object[] it : items) {
                int id = (int) it[0];
                cbLayanan.addItem(new ComboItem(id, (String) it[1]));
                hargaMap.put(id, new double[]{(double) it[2], (double) it[3]});
            }
            applyHarga();
            recompute();
        }, err -> { });
    }

    private void applyHarga() {
        ComboItem sel = (ComboItem) cbLayanan.getSelectedItem();
        if (sel == null) {
            return;
        }
        double[] h = hargaMap.get(sel.getId());
        if (h != null) {
            txtHarga.setText(String.valueOf((int) h[0]));
            txtHarga.setEnabled(h[0] != h[1]);
        }
    }

    /** Header dengan latar gradasi biru profesional. */
    private static final class Header extends JPanel {
        Header() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setPaint(new java.awt.GradientPaint(0, 0, UITheme.PRIMARY,
                    getWidth(), 0, UITheme.PRIMARY_PRESSED));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
