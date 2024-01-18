import java.util.ArrayList;
import java.util.Objects;

public class Paper {
    private ArrayList<News> news;

    public Paper() {
        news = new ArrayList<News>();
    }

    public void addNew(String title, String description) {
        news.add(new News(title, description));
    }

    public News getNew(int index) {
        return news.get(index);
    }

    public void Release() throws NewsEmptyException {
        if (news.size() != 0) {
            System.out.println("Вышла газета с " + String.valueOf(news.size()) + " новостями: ");
            
            for (int i = 0; i < news.size(); i++) {
                System.out.println(String.valueOf(i+1) + ". \"" + news.get(i).getTitle() + "\": " + news.get(i).getDescription());
            }
        } else {
            throw new NewsEmptyException("В газете нет новостей!");
        } 
    }


    class News implements Spreadable {
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
}
