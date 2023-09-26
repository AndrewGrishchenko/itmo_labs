import java.util.Random;
import java.lang.Math;

public class lab1 {
    public static void main(String[] args) {
        int[] c = new int[9];
        float[] x = new float[13];
        final Random random = new Random();

        int sch = 0;
        for (int i = 20; i >= 4; i-=2) {
            c[sch] = i;
            sch += 1;
        }
        for (int i = 0; i < x.length; i++) {
            x[i] = -4.0f + random.nextFloat() * 13;
        }

        double[][] arr = new double[9][13];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                switch (c[i]) {
                    case 18:
                        arr[i][j] = 1/4 * Math.pow(Math.E, x[j]) - 2/3;
                        break;
                    case 4, 12, 14, 16:
                        arr[i][j] = Math.pow(2 / Math.pow((Math.pow((x[j] - 0.25) / 4, x[j]) + 1) / Math.pow(Math.E, x[j]), 3), Math.pow((Math.tan(x[j]) + 1/3) / Math.tan(x[j]), 2));
                        break;
                    default:
                        arr[i][j] = Math.pow(Math.E, Math.pow((Math.cos(Math.log(Math.abs(x[j]))) + 1) / Math.pow(Math.E, Math.pow(Math.E, x[j])), Math.cos(Math.pow(Math.E, x[j]))));
                        break;    
                }              
            }
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.printf("%10.2f ", arr[i][j]);
            }
            System.out.println();
        }
    }
}
