import java.io.File;
import java.io.IOException;
import java.nio.file.*;

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
 *
 * Note #2.1: You need to compile the directory files first before
 * running them. Use this to achieve that:
 *           javac -d ./out/ ./src/main/java/*.java
 * (Outputs all compile files to a new directory called out)
 * You also need to make sure that when you run those commands you are
 * in the correct directory.
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
                // WatchEvent.Kind<?> kind = event.kind();

                // The filename is the context of the event.
                WatchEvent<?> ev = event;
                Path filename = (Path) ev.context();

                // Verify that the new file is a wav file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    Path child = dir.resolve(filename);
                    converter.move();
                    try {
                        if (Files.isDirectory(child)) {
                            System.err.println(filename + " is a directory and will not be converted");
                            continue;
                        }

                        if (!Files.probeContentType(child).equals("audio/wav")) {
                            System.err.println(Files.probeContentType(child));
                            System.err.println("New file " + filename + " is not a wav file.");
                        } else {
                            converter.convertSingle(
                                    new File(directory + "\\" + filename),
                                    filename.toString().substring(0, filename.toString().length() - 4)
                            );
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

            String sourcePath = "iDIR";
            String targetPath = "iDIR";

            Converter converter = new Converter(sourcePath, targetPath);
            converter.convertAll();
            watch(sourcePath, converter);
        } catch (Exception e) {
            System.err.println();
            e.printStackTrace();
            System.err.println();
        }

        // if (args.length != 0) {
        //     sourcePath = args[0];
        //     targetPath = args[1];
        //     Converter cv = new Converter(sourcePath, targetPath);
        //     cv.convertAll();
        //     watch(sourcePath, cv);
        // } else {
        //     Converter converter = new Converter();
        //     converter.convertAll();
        //     watch(sourcePath, converter);
        // }
    }

}

