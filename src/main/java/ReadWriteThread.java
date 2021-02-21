import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ReadWriteThread extends Thread {

    private static String verifiedEmails = "VerifiedEmails";
    private static ArrayList<String> files = new ArrayList<>();
    String combinedExchanger;
    String fileDirectory;
    String details;

    public ReadWriteThread(String ThreadName, String combinedExchanger, String d) {
        super(ThreadName);
        fileDirectory = System.getProperty("user.dir");
        this.combinedExchanger = combinedExchanger;
        details = d;
    }

    public ReadWriteThread(String ThreadName, String combinedExchanger)  {
        super(ThreadName);
        fileDirectory = System.getProperty("user.dir");
        this.combinedExchanger = combinedExchanger;

    }

    @Override
    public void run() {

        if (Thread.currentThread().getName().startsWith("Read") && currentThread().getPriority() == 3) {
            createFile(fileDirectory, combinedExchanger);
        } else if (Thread.currentThread().getName().startsWith("Write") && currentThread().getPriority() == 4) {

            writeToFile(fileDirectory, combinedExchanger, details);

        }
        super.run();
    }

    private void createFile(String path, String nameOfFile) {
        try {
            File file = new File(String.format("%s\\%s", path, verifiedEmails));

            if (!file.isDirectory()) {
                file.mkdir();
                file = new File(String.format("%s\\%s\\%s.txt", path, verifiedEmails, nameOfFile));
            } else
                file = new File(String.format("%s\\%s\\%s.txt", path, verifiedEmails, nameOfFile));

            if (!files.contains(nameOfFile)) {
                file.delete();
                file.createNewFile();
                files.add(nameOfFile);
            } else {
//                System.out.println("File already exists");
            }

        } catch (IOException e) {
            System.out.println("An error occurred while creating the file " + nameOfFile);
            e.printStackTrace();
        }
    }


    private static void writeToFile(String path, String nameOfFile, String line) {
        try {
            File file = new File(String.format("%s\\%s\\%s.txt", path, verifiedEmails, nameOfFile));
//            System.out.println("filepath = " + file);
            if (file.exists()) {
                FileWriter myWriter = new FileWriter(String.format("%s\\%s\\%s.txt", path, verifiedEmails, nameOfFile), true);
                myWriter.write(line);
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } else {
                System.out.println("File " + nameOfFile + " does not exist");
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
