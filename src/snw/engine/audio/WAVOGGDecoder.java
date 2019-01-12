package snw.engine.audio;

import snw.engine.core.Engine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WAVOGGDecoder implements Decoder{
    @Override
    public AudioInputStream decode(String fileName) {
        AudioInputStream stream = null;

        try {
            stream = AudioSystem.getAudioInputStream(new File(fileName));
        } catch (UnsupportedAudioFileException e) {
            Engine.log("Unsupported audio file format (wav/ogg).");
            e.printStackTrace();
        } catch (IOException e) {
            Engine.log("cannot open file: " + fileName + ".");
            e.printStackTrace();
        } finally {
            return stream;
        }
    }
}
