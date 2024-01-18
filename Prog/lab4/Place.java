public enum Place {
    Home("Дома"),
    Valley("Долина"),
    Forest("Лес"),
    Hills("Горы"),
    SeaCoast("Морское побережье");

    private final String title;
    Place(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
