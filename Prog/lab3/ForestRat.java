import java.util.Objects;

public class ForestRat extends Young {
    public ForestRat(String name) {
        super(name, Place.Forest);
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        
        ForestRat other = (ForestRat) obj;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        int result = super.getName().hashCode();
        result = 31 * result + super.getPlace().toString().hashCode();
        return result;
    }
}
