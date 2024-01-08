public class lab3 {
    public static void main (String[] args) {
        Young valleyPeople = new Young("Жители долины", Place.Valley);
        Young forestPeople = new Young("Жители леса", Place.Forest);
        Young hillsPeople = new Young("Жители гор", Place.Hills);
        Young seaCoastPeople = new Young("Жители морского побережья", Place.SeaCoast);
        ForestRat rat = new ForestRat("Лесная крыса");
        
        Old oldPeople = new Old("Старики", Place.Home);

        News news = new News("ПРОПАЛА СУМКА МУМИ-МАМЫ!", "Никаких путеводных нитей! Розыски продолжаются. Неслыханное пиршество в вознаграждение за находку!");
        news.spread();

        valleyPeople.makeNoise();
        forestPeople.makeNoise();
        hillsPeople.makeNoise();
        seaCoastPeople.makeNoise();
        rat.makeNoise();

        System.out.println(oldPeople.toString());
    }
}