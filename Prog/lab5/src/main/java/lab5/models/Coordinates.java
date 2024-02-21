package lab5.models;

import java.util.Objects;

/**
 * Класс Coordinates
 */
public class Coordinates {
    private double x; //Максимальное значение поля: 87
    private Double y; //Значение поля должно быть больше -194, Поле не может быть null

    /**
     * Конструктор класса
     */
    public Coordinates() {

    }

    /**
     * Конструктор класса с данными
     * @param x значение x
     * @param y значение y
     */
    public Coordinates(double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Проверяет валидность данных
     * @return возвращает true если все данные валидны, false в противном случае
     */
    public boolean validate() {
        if (x > 87) return false;
        if (y == null) return false;
        if (y <= -194) return false;
        return true;
    }

    @Override
    public String toString() {
        String s = "Coordinates{\n  x='" + String.valueOf(getX()) + "'\n  y='" + String.valueOf(getY()) + "'}\n";
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Coordinates other = (Coordinates) obj;
        return Objects.equals(getX(), other.getX()) && Objects.equals(getY(), other.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Возвращает значение x
     * @return значение x
     */
    public double getX() {
        return x;
    }

    /**
     * Устанавливает значение x
     * @param x значение x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Возвращает значение y
     * @return значение y
     */
    public Double getY() {
        return y;
    }

    /**
     * Устанавливает значение y
     * @param y значение y
     */
    public void setY(Double y) {
        this.y = y;
    }
}