import java.util.Objects;

public class Old extends Entity {
    public Old(String name, Place place) {
        super(name, place);
    }

    @Override
    public String toString() {
        return "Старый и недужный " + this.getName() + " находится " + getPlace();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        
        Old other = (Old) obj;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        int result = super.getName().hashCode();
        result = 31 * result + super.getPlace().toString().hashCode();
        return result;
    }
}
