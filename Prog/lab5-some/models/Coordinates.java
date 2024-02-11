package models;

public class Coordinates {
    private double x; //Максимальное значение поля: 87
    private Double y; //Значение поля должно быть больше -194, Поле не может быть null

    public Coordinates(double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public boolean validate() {
        if (x > 87) return false;
        if (y <= -194 || y == null) return false;
        return true;
    }
}