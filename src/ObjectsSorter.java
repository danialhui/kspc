import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectsSorter {

    private static String identifyObject(String object) {

        Pattern pattern1 = Pattern.compile("-?\\d+");
        Matcher matcher1 = pattern1.matcher(object);
        if (matcher1.matches()) {
            return "Integer";
        }

        Pattern pattern2 = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher2 = pattern2.matcher(object);
        if (matcher2.matches()) {
            return "Real Numnber";
        }

        Pattern pattern3 = Pattern.compile("^[a-zA-Z\\s]+$");
        Matcher matcher3 = pattern3.matcher(object);
        if (matcher3.matches()) {
            return "Alphabetical";
        }
        // Check for alphanumeric
        Pattern pattern4 = Pattern.compile("^[a-zA-Z0-9\\s]+$");
        Matcher matcher4 = pattern4.matcher(object);
        if (matcher4.matches()) {
            return "Alphanumeric";
        }
        //System.out.println("ERROR : " + object);
        return "ERROR";
    }

    private static String getTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        // Define the format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        // Format the current date and time
        String formattedTimestamp = now.format(formatter);
        // Print the formatted timestamp
        //System.out.println("Current Timestamp: " + formattedTimestamp);
        return formattedTimestamp;
    }

    public static void processFile(String fileIn, String fileOut) throws IOException {
        fileOut = fileOut.substring(0, fileOut.lastIndexOf(".txt")) + "_"+ getTimestamp() + ".txt";
        //System.out.println("fileOut : " + fileOut);
        //System.out.println("fileIn : " + fileIn);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileIn))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (String object : line.split(",")) {
                        // System.out.println("object : " + object);
                        String objectType = identifyObject(object);
                        String strippedObject = objectType.equals("Alphanumeric") ? object.trim()
                                : (objectType.equals("Alphabetical") ? object.trim() : object);
                        String strOut = "Type: " + objectType + ", Value: " + strippedObject;
                        System.out.println(strOut);
                        writer.write(strOut);
                        writer.newLine();
                    }
                }
                reader.close();
                writer.close();
                //System.out.println("FILES CLOSED");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String fileIn = (args == null || args.length <= 0) ? "random_objects.txt" : args[0];
        String fileOut = (args == null || args.length <= 1) ? "proccessed_objects.txt" : args[1];

        String fileSeparator = System.getProperty("file.separator");

        String fileOutDir = fileOut.substring(0, fileOut.lastIndexOf(fileSeparator));
        String fileInDir = fileIn.substring(0, fileIn.lastIndexOf(fileSeparator));
        
        //System.out.println(fileOut);
        //System.out.println(fileIn);
        //System.out.println(fileOutDir);
        //System.out.println(fileInDir);
        System.out.println("--------- Starting Object Sorter Service ---------");

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(fileInDir);
            Path filepath = Paths.get(fileIn);
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            boolean poll = true;

            while (poll) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    //System.out.println(kind.name() + ": " + fileName);
                    //System.out.println("Event kind : " + event.kind() + " - File : " + event.context());

                    // Perform actions based on the event kind and file name
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                        if (Files.exists(filepath)) {
                            try {
                                // Sleep for 5 seconds, give time for input file creation
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                            }
                            processFile(fileIn, fileOut);
                            try {
                                // Sleep for 5 seconds, give time for input file creation
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                            }
                            //Files.delete(filepath);
                            String fileInNew = fileIn.substring(0, fileIn.lastIndexOf(".txt")) + "_"+ getTimestamp() + ".txt";
                            Files.move(filepath, Paths.get(fileInNew), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
                poll = key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
