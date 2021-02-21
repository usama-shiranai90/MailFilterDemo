import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionThreadSingleton {

    private int connectionTries = 0;
    private static ConnectionThreadSingleton instance;
    private URL url;
    private URLConnection urlConnection;
    private ExecutorService executorService;

    private ConnectionThreadSingleton() {
        establishedConnection();
    }

    public void establishedConnection() {
        try {

            String httpStartup = "http://";
            String domainName = "google.com";
            url = new URL(httpStartup + domainName);
            urlConnection = url.openConnection();
            urlConnection.connect();
            System.out.println("Connection maintained");

//            TimeUnit.SECONDS.sleep(3);
            if (connectionTries > 0) {
                connectionTries = 0;
                FilterChecks.filterPause = false;
            }


        } catch (IOException e) {
            System.out.println("Internet not connected");
            reEstablishingNetwork();
        }
   /*     catch (InterruptedException e) {
            System.out.println("Error on wait");
            e.printStackTrace();
        }*/
    }

    synchronized public void reEstablishingNetwork() {
/*        System.out.println("Inside : "+ Thread.currentThread().getName());

        System.out.println("Creating Executor Service with a thread pool of Size 2");
        executorService = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            System.out.println("Executing Task1 inside : " + Thread.currentThread().getName());
            try {
                System.out.println("Recheck Network");
                TimeUnit.SECONDS.sleep(5);
                establishedConnection();
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        };
        executorService.submit(task1);
        executorService.shutdown();*/

        if (connectionTries == 3) {
            System.exit(1);
        }

        try {
            System.out.println("ReEstablishing Network....");
            connectionTries++;

            TimeUnit.SECONDS.sleep(5);
            establishedConnection();
        } catch (InterruptedException e) {
            System.out.println("Error on waiting for no internet connection");
            e.printStackTrace();
        }

    }


    static synchronized ConnectionThreadSingleton getInstance() {
        if (instance == null) {
            return instance = new ConnectionThreadSingleton();
        } else
            return instance;
    }


}