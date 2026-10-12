package ui;

/**
 * Item combo box bernilai id + label. Menghindari praktik rapuh "memisah
 * string 'id - nama'" saat membaca pilihan pengguna.
 */
public class ComboItem {

    private final int id;
    private final String label;

    public ComboItem(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComboItem other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
