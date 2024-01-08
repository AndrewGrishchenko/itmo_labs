public abstract class Entity {
    private final String name;
    private Place place;

    public Entity(String name, Place place) {
        this.name = name;
        this.place = place;
    }

    public String getName() {
        return name;
    }
    public Place getPlace() {
        return place;
    }
}