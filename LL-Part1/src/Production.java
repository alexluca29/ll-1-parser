import java.util.Objects;

public class Production {
    private String left;
    private String right;

    public Production(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " -> " + right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return left.equals(((Production) o).getLeft()) && right.equals(((Production) o).getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
