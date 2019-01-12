package snw.engine.audio;

import snw.engine.core.Engine;

import javax.sound.sampled.*;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public interface Decoder {
    AudioInputStream decode(String fileName);
}
