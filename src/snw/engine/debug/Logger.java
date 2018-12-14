package snw.engine.debug;

import snw.engine.core.Engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;

public class Logger {
    public static BufferedWriter writer = null;

    public static void print(Object... s) {
        if (writer == null) return;

        try {
            writer.write(Calendar.getInstance().getTime().toString() + ": \r\n");
            for (Object o : s) {
                writer.write(o.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void println(Object... s) {
        if (writer == null) return;

        try {
            writer.write(Calendar.getInstance().getTime().toString() + ": \r\n");
            for (Object o : s) {
                writer.write(o + "\r\n\r");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        writer = Engine.getFileWriter(Engine.getProperty("log_path")
                + Calendar.getInstance().getTime().toString().replaceAll(":", ",") + ".log");
    }

    public static void save() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}