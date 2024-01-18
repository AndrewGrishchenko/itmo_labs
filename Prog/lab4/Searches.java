public class Searches {
    public static int powerMark;
    public static SearchPlaces places;
    private static boolean bagFound = false;

    public Searches() {}

    static void setBagFound() {
        bagFound = true;
    }

    static class Search {
        public static boolean isBagFound() {
            return bagFound;
        }

        static void primarySearch() {
            System.out.println("Розыски продолжились с силой " + String.valueOf(Searches.powerMark) + ".");
            for (int i = 0; i < places.getAll().size(); i++) {
                if (!places.get(i).wasSearched()) places.get(i).search();
            }
            System.out.println("Сумка была найдена? " + String.valueOf(Searches.bagFound));
        }

        static void secondarySearch() {
            Young valleyPeople = new Young("Жители долины", Place.Valley);
            Young forestPeople = new Young("Жители леса", Place.Forest);
            Young hillsPeople = new Young("Жители гор", Place.Hills);
            Young seaCoastPeople = new Young("Жители морского побережья", Place.SeaCoast);
            Young rat = new Young("Лесная крыса", Place.Forest) {
                @Override
                public void makeNoise() {
                    System.out.println("Даже лесная крыса " + this.getName() + " подняла шум в " + getPlace().toString());
                    super.isNoising = true;
                }

                @Override
                public String toString() {
                    String data = "Лесная крыса " + this.getName();
                    return super.isNoising ? data : data + ", которая подняла шум";
                }
            };

            Old oldPeople = new Old("Старики", Place.Home);

            valleyPeople.makeNoise();
            forestPeople.makeNoise();
            hillsPeople.makeNoise();
            seaCoastPeople.makeNoise();
            rat.makeNoise();

            System.out.println(oldPeople.toString());
        }

        static void finalSearch() {
            places.getByName("Потайное местечко").search();
            System.out.println("Сумка была найдена? " + String.valueOf(Searches.bagFound));
        }
    }

    
}