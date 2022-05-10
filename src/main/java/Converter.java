import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashMap;

/****************************************************************************************************
 * The Converter class is the one that will search through the source directory
 * to retrieve the files that need to be converted and then has the option to convert file
 * after file or all the files in the directory all at once.
 *
 * It is important ot note that there are some default values hardcoded into the class
 * to make the app work correctly in case no parameter was given to it at the start
 * Those values are the default directory within this project (resources/input and resources/output)
 * they can be modified from the core (main) at the launch of the application if the user desires so
 *
 * Note: The array that is used to store the files names is limited to 100 entries it is not the limit
 * of the application as is but is a default value that can be changed depending on the desired use
 *******************************************************************************************************/

public class Converter {

    private final File sourcePath; // source directory
    private final File targetPath; // target directory
    private final String[] filenames = new String[100]; // array with the names of the files to be converted

    // Constructor to initialize the source and target directories passed as arguments
    // and do the initial setup for the rest of the process
    // Note: See comments on top of each function to see what they do
    public Converter(String sPath, String tPass) throws IOException {
        sourcePath = new File(sPath);
        targetPath = new File(tPass);
        listFiles();
        sortByDateAsc();
    }

    // Alternative constructor with default directories
    public Converter() throws IOException {
        sourcePath = new File("./src/main/resources/input");
        targetPath = new File("./src/main/resources/output");
        listFiles();
        sortByDateAsc();
    }

    // Lists all the files in a directory (Debug Method Only)
    public void showFiles(File directory) {
        File[] files = directory.listFiles();
        assert files != null : "Directory is empty!";
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getAbsolutePath());
                showFiles(file); // Calls same method again
            } else {
                System.out.println("File: " + file.getAbsolutePath());
            }
        }
    }

    // Collect all the source files names and stores them in an array to be used later in the conversion
    public void listFiles() {
        File[] files = sourcePath.listFiles();
        String[] fns = new String[100];
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();
                filenames[i] = filename; //.substring(0, filename.length() - 4);
            }
        }
    }

    // Method to sort the different files by ascending order of creation
    // Note: Sorted names are stored in "filenames"
    public void sortByDateAsc() throws IOException {
        HashMap<Integer, String> dateName = new HashMap<>();

        for (String filename : filenames) {

            if (filename != null) {
                Path file = Paths.get(sourcePath + "/" + filename);
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

                FileTime creationTime = attr.creationTime();
                Integer creationDateMs = (int) creationTime.toMillis();

                dateName.put(creationDateMs, filename);
            }

        }

        Object[] dates = dateName.keySet().toArray();
        Arrays.sort(dates); // the basic sort was used here to avoid remaking sorting algorithm and make the process more complex

        for (int i = 0; i < dates.length; i++) {
            String file = dateName.get((int) dates[i]);
            filenames[i] = file.substring(0, file.length() - 4);
        }
    }

    // Convert all the files in the source directory (sourcePath)
    public void convertAll() {
        File[] sourceFiles = sourcePath.listFiles(); // array of file path (files that will be converted)
        Logger logger = new Logger(); // Logger to save the successful processes into the csv files
        assert sourceFiles != null : "No files found in source directory!"; // make sure that sourceFiles is not empty and give an error message if is empty

        // Loop through all the files and convert them by using the "Engine"
        // Saves each successful process into the csv file using the logger
        for (int i = 0; i < sourceFiles.length; i++) {

            if (filenames[i] != null) {
                System.out.println("Converting " + filenames[i] + " ...");

                Engine e = new Engine(targetPath, sourceFiles[i], filenames[i]);
                try {
                    e.start();
                } catch (Exception ex) {
                    convertSingle(sourceFiles[i], filenames[i]);
                }

                System.out.println("Done \n");

                logger.CSVLogger(e.getThreadUID(), filenames[i] + ".wav");
            }
        }
    }

    // Convert a single file in the source directory
    // Saves the process in the CSV file it the conversion succeeded
    public void convertSingle(File sourceFile, String targetName) {
        System.out.println("Converting " + targetName + " ...");

        Logger logger = new Logger();
        Engine e = new Engine(targetPath, sourceFile, targetName);
        try {
            e.start();
        } catch (Exception ex) {
            convertSingle(sourceFile, targetName);
        }

        System.out.println("Done \n");

        logger.CSVLogger(e.getThreadUID(), targetName + ".wav");
    }

}
