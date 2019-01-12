package snw.engine.debug;

import snw.engine.core.Engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;

public class Logger {
    public static BufferedWriter writer = null;

    public static void print(Object... s) {
        for (Object o : s) {
            System.out.print(o);
        }
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
        for (Object o : s) {
            System.out.println(o);
        }
        if (writer == null) return;

        try {
            writer.write(Calendar.getInstance().getTime().toString() + ": \r\n");
            for (Object o : s) {
                writer.write(o + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        Engine.ensurePath(Engine.getProperty("log_path"));
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

    @Override
    protected void finalize() throws Throwable {
        save();
        super.finalize();
    }
}