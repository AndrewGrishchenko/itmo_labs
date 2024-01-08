import java.util.Objects;

public class Young extends Entity implements Noisable {
    protected boolean isNoising = false;
    
    public Young(String name, Place place) {
        super(name, place);
    }

    // public boolean isNoising() {
    //     return isNoising;
    // }

    @Override
    public void makeNoise() {
        System.out.println(this.getName() + " подняли шум в " + getPlace().toString());
        isNoising = true;
    }

    @Override
    public String toString() {
        String data = "Молодой и здоровый " + this.getName();
        data += ", находящийся в " + this.getPlace();
        return isNoising ? data : data + ", поднял шум";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        
        Young other = (Young) obj;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        int result = super.getName().hashCode();
        result = 31 * result + super.getPlace().toString().hashCode();
        return result;
    }
}
