package snw.file;


import snw.engine.core.Engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FileIOHelper {


    public static File readFile(String filePath) {
        return new File(filePath);
    }

    public static boolean ensurePath(String path) {
        File directory = new File(path);

        return(directory.mkdirs());
    }

    public static BufferedReader getFileReader(String filePath) {
        FileReader reader;
        File file = readFile(filePath);
        if (!file.exists()) return null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            //TODO
            e.printStackTrace();
            return null;
        }
        return new BufferedReader(reader);
    }

    public static String[] readFileStrArr(String filePath) {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader reader = getFileReader(filePath);
        if (reader == null) return null;
        try {
            for (String line = reader.readLine(); line != null; ) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
            return null;
        }

        try {
            reader.close();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
            return null;
        }

        String[] a = new String[0];
        return list.toArray(a);
    }

    public static String readFileStr(String filePath) {
        String[] contents = readFileStrArr(filePath);
        if (contents == null) return null;

        String content = "";
        for (String line : contents) {
            content += line + "\n";
        }
        return content;
    }

    public static BufferedWriter getFileWriter(String filePath) {
        //TODO
        File file = readFile(filePath);
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
            return null;
        }
        return new BufferedWriter(writer);
    }

    public static boolean writeFile(String filePath, Object... contents) {
        BufferedWriter writer = getFileWriter(filePath);
        if (writer == null) return false;

        try {
            for (Object obj : contents) {
                writer.write(obj.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
            return false;
        }

        try {
            writer.close();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeImage(String filePath, String formatName, BufferedImage image) {
        try {
            return ImageIO.write(image, formatName, new File(filePath));
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        return false;
    }

    public static BufferedImage readImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        return null;
    }
}
