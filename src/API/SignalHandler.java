package API;

public class SignalHandler {
    public SignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                System.out.println("Shutting down, sanitizing");
                Overwriter ow = new Overwriter();
                try {
                    ow.overwrite(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
