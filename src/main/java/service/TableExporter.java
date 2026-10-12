package service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Pengekspor tabel generik ke PDF menggunakan iText, lengkap dengan KOP SURAT
 * (logo, nama, alamat, telepon) di bagian atas dan FOOTER di setiap halaman
 * (tanggal &amp; waktu cetak serta nama pencetak). Dipakai agar seluruh laporan
 * tampil konsisten dan profesional tanpa bergantung pada font tertentu.
 */
public final class TableExporter {

    // ---- Identitas perusahaan (kop surat) ----
    public static final String COMPANY = "BOSS LAUNDRY";
    public static final String[] ADDRESS = {
        "Jl. Pancasila II, Cicadas, Kec. Gn. Putri",
        "Kabupaten Bogor, Jawa Barat"
    };
    public static final String PHONE = "Telp: 081292873797";

    private static final BaseColor PRIMARY = new BaseColor(0x25, 0x63, 0xEB);
    private static final BaseColor STRIPE = new BaseColor(0xF3, 0xF6, 0xFA);
    private static final BaseColor BORDER = new BaseColor(0xE2, 0xE8, 0xF0);
    private static final BaseColor TEXT = new BaseColor(0x0F, 0x17, 0x2A);
    private static final BaseColor MUTED = new BaseColor(0x64, 0x74, 0x8B);

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss",
                    new Locale("id", "ID"));

    private TableExporter() {
    }

    /**
     * Tulis data tabel ke PDF dengan kop surat dan footer cetak.
     *
     * @param title     judul laporan
     * @param subtitles baris keterangan tambahan di bawah judul (boleh null)
     * @param columns   nama kolom
     * @param data      baris data (teks tampilan)
     * @param printedBy nama pengguna pencetak
     * @param out       berkas tujuan
     */
    public static void writePdf(String title, String[] subtitles,
            String[] columns, String[][] data, String printedBy, File out)
            throws Exception {

        String stamp = LocalDateTime.now().format(STAMP);
        String by = (printedBy == null || printedBy.isBlank())
                ? "-" : printedBy;

        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 40, 54);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(out);
            PdfWriter writer = PdfWriter.getInstance(doc, fos);
            writer.setPageEvent(new Footer(stamp, by));
            doc.open();

            addLetterhead(doc);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 15,
                    Font.BOLD, TEXT);
            Paragraph t = new Paragraph(title, titleFont);
            t.setSpacingBefore(8f);
            doc.add(t);

            Font subFont = new Font(Font.FontFamily.HELVETICA, 9,
                    Font.NORMAL, MUTED);
            if (subtitles != null) {
                for (String s : subtitles) {
                    if (s != null && !s.isBlank()) {
                        doc.add(new Paragraph(s, subFont));
                    }
                }
            }
            doc.add(new Paragraph(" ", subFont));

            doc.add(buildTable(columns, data));
        } finally {
            // Tutup dokumen selagi stream terbuka agar ter-flush dengan benar.
            if (doc.isOpen()) {
                doc.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (java.io.IOException ignore) {
                    // sudah ditutup PdfWriter.
                }
            }
        }
    }

    private static void addLetterhead(Document doc) throws Exception {
        PdfPTable head = new PdfPTable(new float[]{1f, 8f});
        head.setWidthPercentage(100);

        PdfPCell logoCell;
        try {
            java.net.URL url = TableExporter.class.getResource("/image/logo.png");
            Image logo = (url != null)
                    ? Image.getInstance(url)
                    : Image.getInstance("logo.png");
            logo.scaleToFit(58, 58);
            logoCell = new PdfPCell(logo, false);
        } catch (Exception ex) {
            logoCell = new PdfPCell(new Phrase(""));
        }
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        head.addCell(logoCell);

        PdfPCell info = new PdfPCell();
        info.setBorder(Rectangle.NO_BORDER);
        info.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Font nameFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,
                PRIMARY);
        Font addrFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL,
                MUTED);
        Paragraph name = new Paragraph(COMPANY, nameFont);
        info.addElement(name);
        for (String line : ADDRESS) {
            info.addElement(new Paragraph(line, addrFont));
        }
        info.addElement(new Paragraph(PHONE, addrFont));
        head.addCell(info);

        doc.add(head);

        LineSeparator sep = new LineSeparator(1f, 100f, PRIMARY,
                Element.ALIGN_CENTER, -4);
        doc.add(new Chunk(sep));
    }

    private static PdfPTable buildTable(String[] columns, String[][] data) {
        PdfPTable table = new PdfPTable(columns.length);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);

        Font headFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD,
                BaseColor.WHITE);
        for (String col : columns) {
            PdfPCell c = new PdfPCell(new Phrase(col == null ? "" : col, headFont));
            c.setBackgroundColor(PRIMARY);
            c.setBorderColor(PRIMARY);
            c.setPadding(6f);
            table.addCell(c);
        }

        Font cellFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, TEXT);
        if (data.length == 0) {
            PdfPCell empty = new PdfPCell(new Phrase("Tidak ada data.", cellFont));
            empty.setColspan(columns.length);
            empty.setPadding(10f);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
        } else {
            boolean stripe = false;
            for (String[] row : data) {
                for (int i = 0; i < columns.length; i++) {
                    String val = i < row.length && row[i] != null ? row[i] : "";
                    PdfPCell c = new PdfPCell(new Phrase(val, cellFont));
                    c.setPadding(5f);
                    c.setBorderColor(BORDER);
                    if (stripe) {
                        c.setBackgroundColor(STRIPE);
                    }
                    table.addCell(c);
                }
                stripe = !stripe;
            }
        }
        return table;
    }

    /** Footer tiap halaman: tanggal/waktu cetak, pencetak, dan nomor halaman. */
    private static final class Footer extends PdfPageEventHelper {
        private final String stamp;
        private final String by;
        private final Font font = new Font(Font.FontFamily.HELVETICA, 8,
                Font.NORMAL, MUTED);

        Footer(String stamp, String by) {
            this.stamp = stamp;
            this.by = by;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document doc) {
            PdfContentByte cb = writer.getDirectContent();
            float y = doc.bottom() - 12;

            cb.setLineWidth(0.6f);
            cb.setColorStroke(BORDER);
            cb.moveTo(doc.left(), doc.bottom() - 2);
            cb.lineTo(doc.right(), doc.bottom() - 2);
            cb.stroke();

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Dicetak pada: " + stamp + "   |   Dicetak oleh: "
                            + by, font), doc.left(), y, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Halaman " + writer.getPageNumber(), font),
                    doc.right(), y, 0);
        }
    }
}
