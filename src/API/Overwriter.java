package API;

import java.util.ArrayList;
import java.util.Arrays;

public class Overwriter {
    public Overwriter() {
    }
    public void overwrite(int security) throws InterruptedException {
        for (int i=0;i<security;i++) {
            Runtime runtime = Runtime.getRuntime();
            try {
                ArrayList<byte[]> clearer = new ArrayList<>();
                double x = 2140000000L;
                while(true) {
                    while(true) {
                        try {
                            byte[] array = new byte[(int)Math.floor(x)];
                            Arrays.fill(array, (byte) 2);
                            clearer.add(array);
                            break;
                        } catch (OutOfMemoryError e) {
                            x = x*99/100;
                        }
                        Thread.sleep(50);
                    }
                    System.out.print(".");
                    Thread.sleep(100);
                }
            } catch (OutOfMemoryError e) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.sleep(100);
        }
        System.out.println();
    }
}
