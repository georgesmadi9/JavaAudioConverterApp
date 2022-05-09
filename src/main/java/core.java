import javax.crypto.spec.PSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class core {

    public static WatchService watcher;

    static {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Logger logs = new Logger();

    public static void watch(String directory, Converter converter) {
        Path dir = Paths.get(directory);
        try {
            WatchKey key = dir.register(watcher, ENTRY_CREATE);
        } catch (IOException x) {
            System.err.println(x);
        }
        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // The filename is the context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                // Verify that the new file is a text file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    Path child = dir.resolve(filename);
                    if (!Files.probeContentType(child).equals("audio/wav")) {
                        System.err.format("New file '%s'" + " is not a wav file.%n", filename);
                    } else {
                        converter.convertSingle(
                                new File(directory + "/" + filename),
                                filename.toString().substring(0, filename.toString().length()-4)
                        );
                    }
                } catch (IOException x) {
                    System.err.println(x);
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
    public static void main(String[] args) throws IOException {
        String sourcePath = "./src/main/resources/input";
        String targetPath = "./src/main/resources/output";
        if (args.length != 0) {
            sourcePath = args[1];
            targetPath = args[2];
        }

        logs.errorLogger();
        Converter cv = new Converter(sourcePath, targetPath);
        cv.convertAll();
        watch(sourcePath, cv);
    }

}

