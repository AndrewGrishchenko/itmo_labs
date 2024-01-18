import java.util.Arrays;

public class lab4 {
    public static void main (String[] args) {
        Searches.places = new SearchPlaces(Arrays.asList("Ковер", "Кровать", "Печь", "Погреб", "Чердак", "Крыша", "Сад", "Дровяной сарай", "Берег реки"));
        Searches.places.setHiddenPlace("Потайное местечко");
        Searches.powerMark = 2;
        
        Searches.Search.primarySearch();
        
        Paper paper = new Paper();
        paper.addNew("СНУСМУМРИК ПОКИДАЕТ МУМИ-ДОЛ!", "Таинственный уход на рассвете!");
        paper.addNew("ПРОПАЛА СУМКА МУМИ-МАМЫ!", "Никаких путеводных нитей! Розыски продолжаются. Неслыханное пиршество в вознаграждение за находку!");
        
        try {
            paper.Release();
            paper.getNew(1).spread();

            Searches.Search.secondarySearch();
            Searches.Search.finalSearch();
        } catch (NewsEmptyException e) {
            System.out.println(e.getMessage());
        }
    }
}