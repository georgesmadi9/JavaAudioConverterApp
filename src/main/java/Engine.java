import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.util.UUID;

/************************************************
 * Engine is the conversion part of the app
 * This class implements the Tread logic along
 * with a few extra information on the thread to
 * make the rest of the app work as expected
 * **********************************************/

public class Engine extends Thread {

    private final File sourcePath; // Source path from where to get the files to convert
    private final File targetPath; // Target path where the converted files will be stored
    private final String targetName; // Converted file name
    public String threadUID; // Thread UID

    // Constructor for the class with the parameters required for it to work
    public Engine(File sourceDir, File targetDir, String target) {
        threadUID = UUID.randomUUID().toString().substring(0, 8); // Thread UID generator with UUID and modifying it to be only 8 characters
        sourcePath = sourceDir;
        targetPath = targetDir;
        targetName = target;
    }

    // Getter for the UID of the thread
    public String getThreadUID() {
        return threadUID;
    }

    @Override
    // Thread business logic aka convert the .wav file to .mp3
    public void run() {
        // full path definition to reach the file to convert
        File newTargetPath = new File(targetPath + "\\" + targetName + ".mp3");
        try {
            // Do the conversion
            toMP3(newTargetPath);
            // Delete the .wav after converting
            sourcePath.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Conversion logic
    public void toMP3(File targetPath) throws IllegalArgumentException, InputFormatException, EncoderException {
        // Audio attributes - any type of file can be converted using the "libmp3lame"
        // This codec was used because it was the one that supports .wav files
        // between all the codec encoders supported by JAVE2 listed here:
        // https://github.com/a-schild/jave2/wiki/Supported-formats
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(16000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        //Encoding attributes - conversion to mp3 as an output file type
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);

        // Encode operation to start the conversion
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(sourcePath), targetPath, attrs);
    }

}
