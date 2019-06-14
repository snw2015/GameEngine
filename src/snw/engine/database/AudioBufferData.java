package snw.engine.database;

import snw.engine.audio.AudioData;
import snw.engine.audio.Decoder;
import snw.engine.audio.WAVOGGDecoder;
import snw.engine.core.Engine;
import snw.file.FileIOHelper;
import snw.structure.BufferData;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AudioBufferData extends BufferData<AudioData> {
    private final static AudioBufferData INSTANCE = new AudioBufferData();

    public static AudioBufferData getInstance() {
        return INSTANCE;
    }

    private AudioBufferData() {
        Decoder decoder = new WAVOGGDecoder();
        decoderList.put("wav", decoder);
        decoderList.put("ogg", decoder);
    }

    private Map<String, Decoder> decoderList = new HashMap<>();

    /**
     * @param format
     * @param decoder
     * @return <tt>true</tt> if overwritten
     */
    public boolean setDecoder(String format, Decoder decoder) {
        boolean overwritten = decoderList.containsKey(format);
        decoderList.put(format, decoder);
        return overwritten;
    }

    public Decoder getDecoder(String format) {
        return decoderList.get(format);
    }

    public String[] getAvailableFormatList() {
        String[] list = new String[0];
        list = decoderList.keySet().toArray(list);

        return list;
    }

    public void clear() {
        releaseAll();
    }

    public boolean store(String name, String format) {
        if (!super.store(name)) {
            return store(name, load(name, format));
        }
        return true;
    }

    @Override
    public boolean store(String name) {
        if (!super.store(name)) {
            return store(name, load(name));
        }
        return true;
    }

    public AudioData get(String name, String format) {
        AudioData data = super.get(name);
        return data != null ? data : load(name, format);
    }

    @Override
    public AudioData get(String name) {
        AudioData data = super.get(name);
        return data != null ? data : load(name);
    }

    public AudioData attain(String name, String format) {
        store(name, format);
        return get(name);
    }

    public Clip getClip(String name, String format) {
        AudioData data = get(name, format);
        return data.getClip();
    }

    public Clip getClip(String name) {
        AudioData data = get(name);
        return data.getClip();
    }

    public Clip getNewClip(String name, String format) {
        AudioData data = get(name, format);
        return data.getNewClip();
    }

    public Clip getNewClip(String name) {
        AudioData data = get(name);
        return data.getNewClip();
    }

    public AudioInputStream getStream(String name, String format) {
        String fileName = Engine.getProperty("sounds_path") + name + "." + format;
        return getDecoder(format).decode(fileName);
    }

    public AudioInputStream getStream(String name) {
        return getStream(name, Engine.getProperty("default_audio_format"));
    }

    public AudioInputStream getStreamAbsolute(String nameAbsolute, String format) {
        return getDecoder(format).decode(nameAbsolute);
    }

    public AudioInputStream getStreamAbsolute(String nameAbsolute) {
        return getStreamAbsolute(nameAbsolute, Engine.getProperty("default_audio_format"));
    }

    public AudioData load(String name, String format) {
        return new AudioData(getStream(name, format));
    }

    public AudioData load(String name) {
        return new AudioData(getStream(name));
    }

    public Clip attainClip(String name, String format) {
        store(name, format);
        return getClip(name, format);
    }

    public Clip attainClip(String name) {
        store(name);
        return getClip(name);
    }

    @Override
    protected void disposeData(AudioData data) {
        data.setStream(null);
        data.releaseClip();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return s.equals("") ? "no clip stored" : s;
    }
}