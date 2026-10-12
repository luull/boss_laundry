package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

/**
 * Ikon vektor sederhana yang digambar dengan Java2D.
 *
 * <p>Tidak memerlukan file resource eksternal sehingga selalu tersedia saat
 * aplikasi dijalankan dari JAR. Garis bersih (stroke) agar tetap tajam dan
 * tidak "berantakan", konsisten dengan design system.
 */
public final class Icons {

    private Icons() {
    }

    public static final String DASHBOARD = "dashboard";
    public static final String BRANCH    = "branch";
    public static final String CUSTOMER  = "customer";
    public static final String SERVICE   = "service";
    public static final String USER      = "user";
    public static final String TRX       = "trx";
    public static final String REPORT    = "report";
    public static final String LOGOUT    = "logout";
    public static final String REFRESH   = "refresh";
    public static final String SEARCH    = "search";
    public static final String PLUS      = "plus";
    public static final String EDIT      = "edit";
    public static final String TRASH     = "trash";
    public static final String PRINT     = "print";
    public static final String PDF       = "pdf";
    public static final String EYE       = "eye";
    public static final String EYE_OFF   = "eye_off";
    public static final String FILTER    = "filter";
    public static final String SAVE      = "save";
    public static final String CLOSE     = "close";
    public static final String MONEY     = "money";
    public static final String CLOCK     = "clock";
    public static final String CHECK     = "check";
    public static final String BOX       = "box";

    public static Icon of(String name, int size, Color color) {
        return new VectorIcon(name, size, color);
    }

    private static final class VectorIcon implements Icon {

        private final String name;
        private final int size;
        private final Color color;

        VectorIcon(String name, int size, Color color) {
            this.name = name;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);

            float s = size;
            float stroke = Math.max(1.4f, s / 12f);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            // Semua ikon digambar dalam kotak 0..s dengan sedikit padding.
            float p = s * 0.16f;
            float a = p;             // awal
            float b = s - p;         // akhir
            float m = s / 2f;        // tengah
            float w = b - a;

            switch (name) {
                case DASHBOARD -> {
                    float g1 = s * 0.1f;
                    float cw = (w - g1) / 2f;
                    g2.draw(new RoundRectangle2D.Float(a, a, cw, cw, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(a + cw + g1, a, cw, cw, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(a, a + cw + g1, cw, cw, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(a + cw + g1, a + cw + g1, cw, cw, 3, 3));
                }
                case BRANCH -> {
                    g2.draw(new RoundRectangle2D.Float(a, a + s * 0.08f, w, b - (a + s * 0.08f), 2, 2));
                    g2.draw(new Line2D.Float(m, a + s * 0.08f, m, b));
                    float dw = w * 0.16f;
                    g2.draw(new RoundRectangle2D.Float(a + dw * 0.6f, a + s * 0.26f, dw, dw, 1, 1));
                    g2.draw(new RoundRectangle2D.Float(m + dw * 0.6f, a + s * 0.26f, dw, dw, 1, 1));
                    g2.draw(new RoundRectangle2D.Float(a + dw * 0.6f, a + s * 0.5f, dw, dw, 1, 1));
                    g2.draw(new RoundRectangle2D.Float(m + dw * 0.6f, a + s * 0.5f, dw, dw, 1, 1));
                }
                case CUSTOMER -> {
                    float r = w * 0.17f;
                    g2.draw(new Ellipse2D.Float(a + w * 0.06f, a, r * 2, r * 2));
                    g2.draw(new Ellipse2D.Float(b - w * 0.06f - r * 2, a, r * 2, r * 2));
                    GeneralPath p1 = new GeneralPath();
                    p1.moveTo(a, b);
                    p1.curveTo(a, a + w * 0.52f, a + w * 0.44f, a + w * 0.52f, a + w * 0.44f, b);
                    g2.draw(p1);
                    GeneralPath p2 = new GeneralPath();
                    p2.moveTo(b - w * 0.44f, b);
                    p2.curveTo(b - w * 0.44f, a + w * 0.52f, b, a + w * 0.52f, b, b);
                    g2.draw(p2);
                }
                case USER -> {
                    float r = w * 0.22f;
                    g2.draw(new Ellipse2D.Float(m - r, a, r * 2, r * 2));
                    GeneralPath pp = new GeneralPath();
                    pp.moveTo(a + w * 0.08f, b);
                    pp.curveTo(a + w * 0.08f, a + w * 0.55f, b - w * 0.08f, a + w * 0.55f, b - w * 0.08f, b);
                    g2.draw(pp);
                }
                case SERVICE -> {
                    // tag
                    GeneralPath tag = new GeneralPath();
                    tag.moveTo(m, a);
                    tag.lineTo(b, a);
                    tag.lineTo(b, a + w * 0.5f);
                    tag.lineTo(a + w * 0.5f, b);
                    tag.lineTo(a, a + w * 0.5f);
                    tag.closePath();
                    g2.draw(tag);
                    g2.fill(new Ellipse2D.Float(b - w * 0.3f, a + w * 0.14f, w * 0.1f, w * 0.1f));
                }
                case TRX -> {
                    // receipt
                    GeneralPath rc = new GeneralPath();
                    rc.moveTo(a + w * 0.12f, a);
                    rc.lineTo(b - w * 0.12f, a);
                    rc.lineTo(b - w * 0.12f, b);
                    rc.lineTo(b - w * 0.28f, b - w * 0.12f);
                    rc.lineTo(m, b);
                    rc.lineTo(a + w * 0.28f, b - w * 0.12f);
                    rc.lineTo(a + w * 0.12f, b);
                    rc.closePath();
                    g2.draw(rc);
                    g2.draw(new Line2D.Float(a + w * 0.26f, a + w * 0.32f, b - w * 0.26f, a + w * 0.32f));
                    g2.draw(new Line2D.Float(a + w * 0.26f, a + w * 0.52f, b - w * 0.26f, a + w * 0.52f));
                }
                case REPORT -> {
                    g2.draw(new Line2D.Float(a, b, b, b));
                    float bw = w * 0.18f;
                    g2.draw(new Line2D.Float(a + bw * 0.6f, b, a + bw * 0.6f, a + w * 0.55f));
                    g2.draw(new Line2D.Float(m, b, m, a + w * 0.3f));
                    g2.draw(new Line2D.Float(b - bw * 0.6f, b, b - bw * 0.6f, a + w * 0.15f));
                }
                case LOGOUT -> {
                    g2.draw(new Line2D.Float(a, a, a, b));
                    g2.draw(new Line2D.Float(a, a, a + w * 0.5f, a));
                    g2.draw(new Line2D.Float(a, b, a + w * 0.5f, b));
                    g2.draw(new Line2D.Float(a + w * 0.4f, m, b, m));
                    g2.draw(new Line2D.Float(b - w * 0.22f, m - w * 0.2f, b, m));
                    g2.draw(new Line2D.Float(b - w * 0.22f, m + w * 0.2f, b, m));
                }
                case REFRESH -> {
                    g2.draw(new java.awt.geom.Arc2D.Float(a, a, w, w, 60, 250, java.awt.geom.Arc2D.OPEN));
                    GeneralPath ar = new GeneralPath();
                    double ang = Math.toRadians(60);
                    float ax = (float) (m + (w / 2) * Math.cos(ang));
                    float ay = (float) (m - (w / 2) * Math.sin(ang));
                    ar.moveTo(ax - w * 0.02f, ay - w * 0.18f);
                    ar.lineTo(ax + w * 0.12f, ay);
                    ar.lineTo(ax - w * 0.16f, ay + w * 0.04f);
                    g2.draw(ar);
                }
                case SEARCH -> {
                    float r = w * 0.34f;
                    g2.draw(new Ellipse2D.Float(a, a, r * 2, r * 2));
                    g2.draw(new Line2D.Float(a + r * 2 - r * 0.3f, a + r * 2 - r * 0.3f, b, b));
                }
                case PLUS -> {
                    g2.draw(new Line2D.Float(m, a, m, b));
                    g2.draw(new Line2D.Float(a, m, b, m));
                }
                case CLOSE -> {
                    g2.draw(new Line2D.Float(a, a, b, b));
                    g2.draw(new Line2D.Float(b, a, a, b));
                }
                case CHECK -> {
                    g2.draw(new Line2D.Float(a, m, a + w * 0.33f, b - w * 0.08f));
                    g2.draw(new Line2D.Float(a + w * 0.33f, b - w * 0.08f, b, a + w * 0.08f));
                }
                case EDIT -> {
                    GeneralPath pen = new GeneralPath();
                    pen.moveTo(a, b);
                    pen.lineTo(a, b - w * 0.22f);
                    pen.lineTo(b - w * 0.22f, a + w * 0.02f);
                    pen.lineTo(b, a + w * 0.24f);
                    pen.lineTo(a + w * 0.22f, b);
                    pen.closePath();
                    g2.draw(pen);
                    g2.draw(new Line2D.Float(b - w * 0.36f, a + w * 0.16f, b - w * 0.14f, a + w * 0.38f));
                }
                case TRASH -> {
                    g2.draw(new Line2D.Float(a, a + w * 0.18f, b, a + w * 0.18f));
                    g2.draw(new Line2D.Float(m - w * 0.14f, a + w * 0.18f, m - w * 0.1f, a));
                    g2.draw(new Line2D.Float(m + w * 0.14f, a + w * 0.18f, m + w * 0.1f, a));
                    GeneralPath can = new GeneralPath();
                    can.moveTo(a + w * 0.12f, a + w * 0.18f);
                    can.lineTo(a + w * 0.2f, b);
                    can.lineTo(b - w * 0.2f, b);
                    can.lineTo(b - w * 0.12f, a + w * 0.18f);
                    g2.draw(can);
                    g2.draw(new Line2D.Float(m - w * 0.1f, a + w * 0.34f, m - w * 0.08f, b - w * 0.08f));
                    g2.draw(new Line2D.Float(m + w * 0.1f, a + w * 0.34f, m + w * 0.08f, b - w * 0.08f));
                }
                case PRINT -> {
                    g2.draw(new Line2D.Float(a + w * 0.2f, a + w * 0.3f, a + w * 0.2f, a));
                    g2.draw(new Line2D.Float(a + w * 0.2f, a, b - w * 0.2f, a));
                    g2.draw(new Line2D.Float(b - w * 0.2f, a, b - w * 0.2f, a + w * 0.3f));
                    g2.draw(new RoundRectangle2D.Float(a, a + w * 0.3f, w, w * 0.42f, 4, 4));
                    g2.draw(new RoundRectangle2D.Float(a + w * 0.2f, a + w * 0.55f, w * 0.6f, w * 0.45f, 2, 2));
                }
                case PDF, SAVE -> {
                    GeneralPath doc = new GeneralPath();
                    doc.moveTo(a + w * 0.12f, a);
                    doc.lineTo(b - w * 0.28f, a);
                    doc.lineTo(b - w * 0.12f, a + w * 0.18f);
                    doc.lineTo(b - w * 0.12f, b);
                    doc.lineTo(a + w * 0.12f, b);
                    doc.closePath();
                    g2.draw(doc);
                    g2.draw(new Line2D.Float(b - w * 0.28f, a, b - w * 0.28f, a + w * 0.18f));
                    g2.draw(new Line2D.Float(b - w * 0.12f, a + w * 0.18f, b - w * 0.28f, a + w * 0.18f));
                    if (name.equals(SAVE)) {
                        g2.draw(new Line2D.Float(a + w * 0.3f, a + w * 0.45f, b - w * 0.3f, a + w * 0.45f));
                        g2.draw(new Line2D.Float(a + w * 0.3f, a + w * 0.65f, b - w * 0.3f, a + w * 0.65f));
                    }
                }
                case FILTER -> {
                    GeneralPath f = new GeneralPath();
                    f.moveTo(a, a);
                    f.lineTo(b, a);
                    f.lineTo(m + w * 0.1f, m);
                    f.lineTo(m + w * 0.1f, b);
                    f.lineTo(m - w * 0.1f, b - w * 0.14f);
                    f.lineTo(m - w * 0.1f, m);
                    f.closePath();
                    g2.draw(f);
                }
                case EYE -> {
                    GeneralPath eye = new GeneralPath();
                    eye.moveTo(a, m);
                    eye.quadTo(m, a + w * 0.05f, b, m);
                    eye.quadTo(m, b - w * 0.05f, a, m);
                    eye.closePath();
                    g2.draw(eye);
                    g2.draw(new Ellipse2D.Float(m - w * 0.14f, m - w * 0.14f, w * 0.28f, w * 0.28f));
                }
                case EYE_OFF -> {
                    GeneralPath eye = new GeneralPath();
                    eye.moveTo(a, m);
                    eye.quadTo(m, a + w * 0.05f, b, m);
                    eye.quadTo(m, b - w * 0.05f, a, m);
                    eye.closePath();
                    g2.draw(eye);
                    g2.draw(new Ellipse2D.Float(m - w * 0.14f, m - w * 0.14f, w * 0.28f, w * 0.28f));
                    g2.draw(new Line2D.Float(a, a, b, b));
                }
                case MONEY -> {
                    g2.draw(new RoundRectangle2D.Float(a, a + w * 0.14f, w, w * 0.72f, 4, 4));
                    g2.draw(new Ellipse2D.Float(m - w * 0.14f, m - w * 0.14f, w * 0.28f, w * 0.28f));
                }
                case CLOCK -> {
                    g2.draw(new Ellipse2D.Float(a, a, w, w));
                    g2.draw(new Line2D.Float(m, m, m, a + w * 0.28f));
                    g2.draw(new Line2D.Float(m, m, m + w * 0.22f, m));
                }
                case BOX -> {
                    g2.draw(new RoundRectangle2D.Float(a, a, w, w, 4, 4));
                    g2.draw(new Line2D.Float(a, a + w * 0.33f, b, a + w * 0.33f));
                }
                default -> {
                    g2.draw(new RoundRectangle2D.Float(a, a, w, w, 4, 4));
                }
            }

            g2.dispose();
        }
    }
}
