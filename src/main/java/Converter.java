import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;

public class Converter {

    private File sourcePath = new File("./src/main/resources/input"); // source directory with default values
    private File targetPath = new File("./src/main/resources/output"); // target directory with default values
    private String[] filenames = new String[100]; // array with the names of the files to be converted

    // Constructor to initialize the source and target directories passed as arguments
    public Converter(String sPath, String tPass) {
        sourcePath = new File(sPath);
        targetPath = new File(tPass);
        listFiles();
    }

    // Alternative constructor with default directories
    public Converter() {
        listFiles();
    }

    // Lists all the files in a directory
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
                filenames[i] = filename.substring(0, filename.length() - 4);
            }
        }
    }

    // Convert all the files in the source directory (sourcePath)
    public void convertAll() {
        File[] sourcefiles = sourcePath.listFiles();
        assert sourcefiles != null;
        for (int i = 0; i < sourcefiles.length; i++) {
            System.out.println("Converting " + filenames[i] + " ...");
            convertFile(sourcefiles[i], filenames[i]);
            System.out.println("Done \n");
        }
    }

    // Conversion file by file from any type to mp3
    public boolean convertFile(File sourceFile, String targetName) {
        boolean succeeded;
        File newTargetPath = new File(targetPath + "/" + targetName + ".mp3");
        try {
            // Audio attributes - any type of file can be converted using the "libmp3lame"
            // This codec was used because it was the one that supports .wav files
            // between all the codec encoders supported by JAVE2 listed here:
            // https://github.com/a-schild/jave2/wiki/Supported-formats
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(128000);
            audio.setChannels(2);
            audio.setSamplingRate(44100);

            //Encoding attributes - conversion to mp3 as an output file type
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audio);

            // Encode operation to start the conversion
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(sourceFile), newTargetPath, attrs);

            // Mark that the conversion succeeded
            succeeded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            // Mark the operation failed
            succeeded = false;
        }
        return succeeded;
    }
 

}
