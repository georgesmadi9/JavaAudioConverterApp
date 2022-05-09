import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashMap;

public class Converter {

    private File sourcePath = new File("./src/main/resources/input"); // source directory with default values
    private File targetPath = new File("./src/main/resources/output"); // target directory with default values
    private String[] filenames = new String[100]; // array with the names of the files to be converted

    // Constructor to initialize the source and target directories passed as arguments
    public Converter(String sPath, String tPass) throws IOException {
        sourcePath = new File(sPath);
        targetPath = new File(tPass);
        listFiles();
        sortByDateAsc();
    }

    // Alternative constructor with default directories
    public Converter() throws IOException {
        listFiles();
        sortByDateAsc();
    }

    // Lists all the files in a directory (Debug Method Only)
    public void showFiles(File directory) {
        File[] files = directory.listFiles();
        assert files != null;
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
                FileTime time = attr.creationTime();
                Integer newDate = (int) time.toMillis();

                dateName.put(newDate, filename);
            }
        }
        Object[] dates = dateName.keySet().toArray();
        Arrays.sort(dates);
        for (int i = 0; i < dates.length; i++) {
            String file = dateName.get((int) dates[i]);
            filenames[i] = file.substring(0, file.length() - 4);
        }
    }

    // Convert all the files in the source directory (sourcePath)
    public void convertAll() {
        File[] sourcefiles = sourcePath.listFiles();
        Logger logger = new Logger();
        assert sourcefiles != null;
        for (int i = 0; i < sourcefiles.length; i++) {
            if (filenames[i] != null) {
                System.out.println("Converting " + filenames[i] + " ...");
                Engine e = new Engine(targetPath, sourcefiles[i], filenames[i]);
                try {
                    e.run();
                } catch (Exception ex) {
                    convertSingle(sourcefiles[i], filenames[i]);
                }
                System.out.println("Done \n");
                logger.CSVLogger(e.getThreadUID(), filenames[i] + ".wav");
            }
        }
    }

    // Convert a single file in the source directory
    public void convertSingle(File sourceFile, String targetName) {
        System.out.println("Converting " + targetName + " ...");
        Logger logger = new Logger();
        Engine e = new Engine(targetPath, sourceFile, targetName);
        try {
            e.run();
        } catch (Exception ex) {
            convertSingle(sourceFile, targetName);
        }
        System.out.println("Done \n");
        logger.CSVLogger(e.getThreadUID(), targetName + ".wav");
    }

}
