import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/****************************************************************************************************
 * The Converter class is the one that will search through the source directory
 * to retrieve the files that need to be converted and then has the option to convert file
 * after file or all the files in the directory all at once.
 *
 * It is important ot note that there are some default values hardcoded into the class
 * to make the app work correctly in case no parameter was given to it at the start
 * Those values are the default directory within this project (resources/input and resources/output)
 * they can be modified from the core (main) at the launch of the application if the user desires so.
 *******************************************************************************************************/

public class Converter {

    private final int threadNo = 3;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(threadNo);

    private final File sourcePath; // source directory
    private final File targetPath; // target directory
    private final ArrayList<AudioFile> audioFiles = new ArrayList<>(); // array of AudioFiles

    // Constructor to initialize the source and target directories passed as arguments
    // and do the initial setup for the rest of the process
    // Note: See comments on top of each function to see what they do
    public Converter(String sPath, String tPass) throws IOException {
        sourcePath = new File(sPath);
        targetPath = new File(tPass);
        if (!checkDirectory(sourcePath)) createDirectory(sourcePath);
        if (!checkDirectory(targetPath)) createDirectory(targetPath);
        listFiles();
        sortByDateAsc();
    }

    // Alternative constructor with default directories
    public Converter() throws IOException {
        sourcePath = new File("./src/main/resources/input");
        targetPath = new File("./src/main/resources/output");
        if (!checkDirectory(sourcePath)) createDirectory(sourcePath);
        if (!checkDirectory(targetPath)) createDirectory(targetPath);
        listFiles();
        sortByDateAsc();
    }

    // Checks if the directory exists
    public boolean checkDirectory(File dirPath) {
        return dirPath.exists();
    }

    // If non-default directories are specified but do not 
    // this function will create the directories within the specified path 
    boolean createDirectory(File dirPath) {
        String dirName = dirPath.toString().substring(dirPath.toString().lastIndexOf("\\") + 1);
        System.out.println(dirName + " does not exist. \nCreating " + dirName + " ...");
        return dirPath.mkdir();
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
    public void listFiles() throws IOException {
        File[] files = sourcePath.listFiles();
        if (files != null) {
            for (File value : files) {
                if (!value.isDirectory() && Files.probeContentType(value.toPath()).equals("audio/wav")) {
                    String filename = value.getName();
                    Path file = Paths.get(sourcePath + "\\" + filename);
                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                    FileTime creationTime = attr.creationTime();
                    Integer creationDateMs = (int) creationTime.toMillis();
                    audioFiles.add(new AudioFile(filename, creationDateMs));
                }
            }
        }
    }

    // Method to sort the different files by ascending order of creation
    public void sortByDateAsc() {
        for (int i = 0; i < audioFiles.size() - 1; i++) {
            AudioFile current = audioFiles.get(i);
            AudioFile next = audioFiles.get(i + 1);
            if (current.getcDate() > next.getcDate()) {
                audioFiles.set(i, next);
                audioFiles.set(i + 1, current);
            }
        }
    }

    // Debug method to display the array of audio files
    public void showList() {
        System.err.println("Array: [");
        for (AudioFile af : audioFiles) {
            System.err.println(af.toString());
        }
        System.err.println("]");
    }

    // Convert all the files in the source directory (sourcePath)
    public void convertAll() throws InterruptedException {
        //Logger logger = new Logger(); // Logger to save the successful processes into the csv files
        
        // Loop through all the files and convert them by using the "Engine"
        // Saves each successful process into the csv file using the logger (desctivated at the moment)
        for (AudioFile af : audioFiles) {
            if (af != null) {
                String filename = af.getfNameNoExtention();
                File filePath = new File(sourcePath + "/" + af.getfName());
                System.out.println("Converting " + filename + " ...");
                Engine e = new Engine(filePath, targetPath, filename);
                try {
                    threadPool.submit(e);
                } catch (Exception ex) {
                    convertSingle(af);
                }
                System.out.println("Done \n");
                //logger.CSVLogger(e.getThreadUID(), filenameWithExtension);
            }
        }
    }

    // Convert a single file in the source directory
    // Saves the process in the CSV file it the conversion succeeded
    public void convertSingle(AudioFile audioFile) throws InterruptedException {
        String targetName = audioFile.getfNameNoExtention();
        File sourceFile = new File(sourcePath + "\\" + audioFile.getfName());
        System.out.println("Converting " + targetName + " ...");
        //Logger logger = new Logger();
        Engine e = new Engine(sourceFile, targetPath, targetName);
        try {
            threadPool.submit(e);
        } catch (Exception ex) {
            convertSingle(audioFile);
        }
        System.out.println("Done \n");
        //logger.CSVLogger(e.getThreadUID(), targetName + ".wav");
    }
}