import java.io.File;

public class AudioFile {

    private final String fName; // filename
    private final Integer cDate; // creation date in milliseconds
    private File fPath = new File("");

    // constructor with filename and creation date arguments
    public AudioFile(String filename, Integer creationDate) {
        fName = filename;
        cDate = creationDate;
    }

    // Converter with only the File Path
    public AudioFile(File filePath) {
        fName = filePath.getName();
        cDate = -1;
        fPath = filePath;
    }

    // fName getter
    public String getfName() {
        return fName;
    }

    // getter for fName without the .<ext> at the end 
    public String getfNameNoExtention() {
        return fName.substring(0, fName.length()-4);
    }

    // cDate getter
    public Integer getcDate() {
        return cDate;
    }

    // fPath getter
    public File getfPath() {
        return fPath;
    }

    // String version of the audio file
    @Override
    public String toString() {
        return "AudioFile{" +
                "fName='" + fName + '\'' +
                ", cDate=" + cDate +
                '}';
    }
}
