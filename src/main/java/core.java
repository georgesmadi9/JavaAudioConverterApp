import ws.schild.jave.encode.AudioAttributes;

import java.io.File;

public class core {

    public static void main(String[] args) {
        Converter cv = new Converter("./src/main/resources/input", "./src/main/resources/output");
        //cv.convertFile(new File("./src/main/resources/input/music1.wav"), "music1");
        cv.convertAll();
    }

}
