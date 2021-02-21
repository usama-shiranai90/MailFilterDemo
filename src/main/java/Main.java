public class Main {
    static int totalEmails = 0;
    static int hopCounter = 0;

    public static void main(String[] args) {
        EmailFilter emailFilter = new EmailFilter();
        totalEmails = emailFilter.totalNumberOfEmails();

        Runnable connectionEstablishmentThread = () -> {
            ConnectionThreadSingleton connectionThreadSingleton = ConnectionThreadSingleton.getInstance();
        };

        Runnable runnable = () -> {
            emailFilter.filterThemAll(hopCounter++);
        };

        Thread connection = new Thread(connectionEstablishmentThread);
        connection.start();

        Thread[] filterThreads = new Thread[totalEmails];
        for (int i = 0; i <= filterThreads.length / 2 - 10; i++) {
            filterThreads[i] = new Thread(runnable);
            filterThreads[i].start();
        }
    }
}
