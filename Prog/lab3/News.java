import java.util.Objects;

public class News implements Spreadable {
    private String title;
    private String description;

    public News(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public void spread() {
        System.out.println("Новость \"" + getTitle() + ": " + getDescription() + "\" разлетелась");
    }

    @Override
    public String toString() {
        return "Новость \"" + getTitle() + "\": " + getDescription();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        News other = (News) obj;
        return Objects.equals(getTitle(), other.getTitle());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + (getDescription().hashCode());
        return result;
    }
}
