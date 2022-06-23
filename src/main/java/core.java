import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import ws.schild.jave.InputFormatException;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/************************************************************************
 * This is main code of the application that needs to be executed.
 * This file container the watcher that will monitor any change to
 * directory (addition or modification of any file within the directory)
 * and convert any new wav file added to a mp3 one.
 *
 * Note: the watcher uses the WatchService API to monitor the changes
 * and is configured to any detect only changes that concern wav files.
 *
 *
 * Note #2: This file can be run directly as is, or you can modify the
 * input/output directories either within this main code or via command line
 * by using this syntax:
 *           java core <input directory> <output directory> &
 ************************************************************************/

public class core {

    public static WatchService watcher; // watcher creation
    static Logger logs = new Logger(); // logger creation (will be used to change errors output)

    static {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Monitor changes to the source directory
    // Will only convert wav files and give errors if the file is not a .wav
    // This method relies on the Watch Service API
    public static void watch(String directory, Converter converter) throws InputFormatException {
        if (directory.equals("")) directory = "./src/main/resources/input"; // default directory if non is given
        Path dir = Paths.get(directory);
        WatchKey key;
        try {
            key = dir.register(watcher, ENTRY_CREATE);
        } catch (IOException x) {
            x.printStackTrace();
        }
        for (; ;) {
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                // The filename is the context of the event.
                WatchEvent<?> ev = event;
                Path filename = (Path) ev.context();

                // Verify that the new file is a wav file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    Path child = dir.resolve(filename);
                    try {
                        // Skip if the file found is a directory
                        if (Files.isDirectory(child)) {
                            System.err.println(filename + " is a directory and will not be converted");
                            continue;
                        }

                        // If the file is not a .wav prints an error message that it will not get converted
                        if (!Files.probeContentType(child).equals("audio/wav")) {
                            System.err.println(Files.probeContentType(child));
                            System.err.println("New file " + filename + " is not a wav file.");
                        } else { // If it is a .wav convert it
                            // TODO: Delete those 4 lines that get the creation time if we need to optimize space and give a -1 to the creation time to the AudioFile
                            String filePath = new String(directory + "\\" + filename);
                            BasicFileAttributes attr = Files.readAttributes(Paths.get(filePath), BasicFileAttributes.class);
                            FileTime creationTime = attr.creationTime();
                            Integer creationDateMs = (int) creationTime.toMillis();
                            // create an AudioFile object and convert it
                            AudioFile af = new AudioFile(filename.toString(), creationDateMs);
                            converter.convertSingle(af);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | InterruptedException x) {
                    x.printStackTrace();
                }

            }
            // Reset the key to receive further watch events.
            // If the key is no longer valid, the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    // Main code to run the application
    public static void main(String[] args) {
        try {
            logs.errorLogger();
            String sourcePath = "";
            String targetPath = "";
            
            if (args.length != 0) {
                sourcePath = args[0];
                targetPath = args[1];
                Converter cv = new Converter(sourcePath, targetPath);
                cv.convertAll();
                watch(sourcePath, cv);
            } else {
                Converter converter = new Converter();
                converter.convertAll();
                watch(sourcePath, converter);
            }
        } catch (Exception e) {
            System.err.println();
            e.printStackTrace();
            System.err.println();
        }
    }
}

