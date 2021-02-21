import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class EmailFilter {

    private int totalEmails = 0;
    private  Object firstKey = new Object();
    private  Object secondKey = new Object();
    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> filteredEmails = new ArrayList<>();
    ArrayList<String> domainList = new ArrayList<>();//stores domainList from input file
    //    ArrayList<String> emailList = new ArrayList<>();//stores emailList from input file
    private ArrayList<String> files = new ArrayList();
    private String verifiedEmails = "VerifiedEmails";


    public void filterThemAll(int hop) {
        try {
            synchronized (firstKey) {
                if (FilterChecks.noPauseFilter) {
                    emails.forEach((data) -> {
                        String emailByLine = data;
//                        emailList.add(emailByLine);
 /*               checks for  and remove a preceding characters , e.g followed by any sequence of chars
                 followed by a '@' ,  from the beginning of the string  */
                        emailByLine = emailByLine.replaceFirst("^*?@", " ");
                        int index = emailByLine.indexOf(" ");
                        emailByLine = emailByLine.substring(index + 1);
                        domainList.add(emailByLine);
                    });
                    System.out.println("Domain Size: " + domainList.size());
                    FilterChecks.noPauseFilter = false;
                }
            }

            int domainCounter = hop;
            ProcessBuilder processBuilder = new ProcessBuilder();

            while (domainCounter < (domainList.size())) {

                if (!filteredEmails.contains(emails.get(domainCounter))) {
                    processBuilder.command("cmd.exe", "/c", "nslookup -type=mx " + domainList.get(domainCounter) + " | findstr \"mail exchanger =\"");
                } else {
//                    System.out.println(Thread.currentThread().getName() + " continued");
                    domainCounter += hop;
                    continue;
                }

                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    if (line.contains(domainList.get(domainCounter).toString()) && !filteredEmails.contains(emails.get(domainCounter))) {
                        if (!line.contains("mail exchanger = " + domainList.get(domainCounter))) {
//                            hopCount = 1;
                            String combinedExchanger = null;
                            if (line.contains("mail exchanger = localhost")) {
                                combinedExchanger = "localhost";
                            } else {
                                String[] mailExchanger = line.split("\\.");
                                combinedExchanger = mailExchanger[mailExchanger.length - 2];
                            }
                            String fileDirectory = System.getProperty("user.dir");
                            synchronized (secondKey) {
/*                                ReadWriteThread read = new ReadWriteThread("Read", combinedExchanger);
                                read.setPriority(3);
                                read.start();
                                synchronized (read) {
                                    try {
                                        System.out.println("Wait until read is completed ");
                                        read.wait();
                                    } catch (InterruptedException E) {
                                        E.printStackTrace();
                                    }
                                }
                                String details = emails.get(domainCounter) + "\n";
                                ReadWriteThread write = new ReadWriteThread("Write", combinedExchanger, details);
                                write.setPriority(4);
                                write.start();
                                filteredEmails.add(emails.get(domainCounter));*/
                                createFile(fileDirectory, combinedExchanger);
                                String details = emails.get(domainCounter) + "\n";
                                writeToFile(fileDirectory, combinedExchanger, details);
                                filteredEmails.add(emails.get(domainCounter));
                            }

                            break;
                        } else {
                 /*           ReadWriteThread read = new ReadWriteThread("Read", "Invalid Domains");
                            read.setPriority(3);
                            read.start();
                            ReadWriteThread write = new ReadWriteThread("Write", "Invalid Domains", emails.get(domainCounter) + "\n");
                            write.setPriority(4);
                            write.start();*/

                            if (!filteredEmails.contains(emails.get(domainCounter))) {
                                String errorsDirectory = System.getProperty("user.dir");
                                synchronized (secondKey) {
                                    createFile(errorsDirectory, "Invalid Domains");
                                    writeToFile(errorsDirectory, "Invalid Domains", emails.get(domainCounter).toString() + "\n");
                                    filteredEmails.add(emails.get(domainCounter));

                                }
                            }

                        }
                    }
                }
                domainCounter += hop;
                if (Thread.currentThread().getName().equalsIgnoreCase("Thread-1"))
                    break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Internet is not connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalEmails() {
        return totalEmails;
    }


    int totalNumberOfEmails() {

        File myObj = new File("emails.txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                emails.add(data);
                totalEmails++;
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return totalEmails;
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
//                System.out.println("File " + nameOfFile + " exists therefore rewriting it.");
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

    private void writeToFile(String path, String nameOfFile, String line) {
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