package lab5.models;

public class Coordinates {
    private double x; //Максимальное значение поля: 87
    private Double y; //Значение поля должно быть больше -194, Поле не может быть null

    public Coordinates() {

    }

    public Coordinates(double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public boolean validate() {
        if (x > 87) return false;
        if (y <= -194 || y == null) return false;
        return true;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}