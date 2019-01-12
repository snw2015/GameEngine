package snw.engine.audio;

import snw.engine.core.Engine;

import javax.sound.sampled.*;
import java.io.File;

public class AudioData {
    private AudioInputStream stream;
    private Clip clip;

    private boolean hasPrepared = false;

    public AudioData(Clip clip) {
        this.clip = clip;
        hasPrepared = true;
    }

    public AudioData(AudioInputStream stream) {
        this.stream = stream;
    }

    /**
     * @return <tt>true</tt> if has prepared
     */
    public boolean prepareClip() {
        boolean b = hasPrepared();
        if (stream == null) return b;
        getNewClip();
        return b;
    }

    public Clip getClip() {
        if (hasPrepared()) return clip;
        if (stream == null) return null;
        prepareClip();
        return clip;
    }

    public Clip getNewClip() {
        hasPrepared = false;

        while (!hasPrepared || !clip.isOpen()) {
            try {
                clip = AudioSystem.getClip();
                clip.open(stream);
                hasPrepared = true;
            } catch (Exception e) {
                Engine.log("can't create clip");
                e.printStackTrace();
            }
        }
        return clip;
    }

    public boolean releaseClip() {
        if (hasPrepared()) {
            if(clip!=null) clip.close();
            clip = null;
            hasPrepared = false;
            return true;
        }
        return false;
    }

    public void setStream(AudioInputStream stream) {
        this.stream = stream;
        hasPrepared = false;
    }

    public AudioInputStream getStream() {
        return this.stream;
    }

    public boolean hasPrepared() {
        return hasPrepared;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        releaseClip();
    }

    @Override
    public String toString() {
        return "AudioData [\n" +
                "\t" + stream != null ? stream.toString() : "No stream" + "\n" +
                "\tPrepared: " + hasPrepared() + "]\n";
    }
}