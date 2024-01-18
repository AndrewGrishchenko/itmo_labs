import java.util.ArrayList;
import java.util.List;

public class SearchPlaces {
    private ArrayList<SearchPlace> places;
    private SearchPlace hiddenPlace;

    public SearchPlaces(List<String> names) {
        places = new ArrayList<SearchPlace>();
        for (int i = 0; i < names.size(); i++) {
            places.add(new SearchPlace(names.get(i)));
        }
    }

    public void setHiddenPlace(String name) {
        hiddenPlace = new SearchPlace(name);
    }

    public ArrayList<SearchPlace> getAll() {
        return places;
    }

    public SearchPlace getByName(String name) throws placeNotFoundException {
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getName() == name) {
                return places.get(i);
            }
        }
        if (hiddenPlace.getName() == name) return hiddenPlace;
        throw new placeNotFoundException("No such place!");
    }

    public SearchPlace get(int index) {
        return places.get(index);
    }

    public class SearchPlace {
        private String name;
        private boolean wasSearched = false;
        
        SearchPlace(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean wasSearched() {
            return wasSearched;
        }

        public void search() {
            wasSearched = true;
            System.out.println("Искали в " + String.valueOf(name));
            if (name == hiddenPlace.getName()) {
                Searches.setBagFound();
            }
        }
    }
}