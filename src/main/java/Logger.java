import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public void errorLogger() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            PrintStream errorLogs = new PrintStream(new FileOutputStream("logs.txt", true), true);
            System.setErr(errorLogs);
            System.err.println("\n[Error Logs - " + now + "]");
        } catch (FileNotFoundException e) {
            System.out.println("Log file (logs.txt) not found.");
            e.printStackTrace();
        }
    }

    public void CSVLogger(String threadUID, String convertedFile) {
        String[] entries = { threadUID, convertedFile };
        String fileName = "processes.csv";

        try (FileOutputStream fos = new FileOutputStream(fileName, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            writer.writeNext(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
