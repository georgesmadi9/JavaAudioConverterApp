import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**************************************************************************************
 * Logger is a class that will save the errors generated by the code into a logs file
 * and will also save successful processes into the csv file.
 *
 * Note: CSV file entry format:
 * +------------+-----------+
 * |    Feild   |    Type   |
 * +------------+-----------+
 * +------------+-----------+
 * | Thread UID |   String  |
 * +------------+-----------+
 * +------------+-----------+
 * | Feild Name |   String  | 
 * +------------+-----------+
 *
 * Note #2:
 * - errorLogger redirects the error standard output to the logs.txt.
 * - CSVLogger uses the OpenCSV library to save the parameters given to it via an array.
 *
 * Note #3: If you wish to change the output files names change them where the /!\ symbol is
 ****************************************************************************************/


public class Logger {

    // Saves the errors logs in logs.txt
    public void errorLogger() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            String now = LocalDateTime.now().format(dtf);
            /* /!\ File name here (logs.txt) */
            PrintStream errorLogs = new PrintStream(new FileOutputStream("logs.txt", true), true);
            System.setErr(errorLogs);
            System.err.println("\n[Error Logs - " + now + "]");
        } catch (FileNotFoundException e) {
            System.err.println("Log file (logs.txt) not found.");
            e.printStackTrace();
        }
    }

    // Saves the successful processes in processes.csv
    public void CSVLogger(String threadUID, String convertedFile) {
        String[] entries = {threadUID, convertedFile};
        /* /!\ File name here (processes.csv) */
        String fileName = "processes.csv";

        try (FileOutputStream fos = new FileOutputStream(fileName, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {
            writer.writeNext(entries);
        } catch (IOException e) {
            System.err.println("CSV file (processes.csv) not found.");
            e.printStackTrace();
        }
    }

}
