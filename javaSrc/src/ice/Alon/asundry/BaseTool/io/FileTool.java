package ice.Alon.asundry.BaseTool.io;

import ice.Ice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class FileTool {
    @FunctionalInterface
    public interface FileIO {
        void manipulate(File file);
    }

    public static void manipulateFile(File file, FileIO fo) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    manipulateFile(file1, fo);
                } else {
                    fo.manipulate(file1);
                }
            }
        }
    }


    public static Properties getJarFileProperties(String FileName) {
        Properties properties = new Properties();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Ice.class.getClassLoader().getResourceAsStream(FileName)), StandardCharsets.UTF_8));
        String s;
        while (true) {
            try {
                if ((s = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(FileTool.class.getName() + "出错了");
            }
            if (s.contains(":")) {
                String[] split = s.split(":");
                properties.setProperty(split[0], split[1]);
            } else if (s.contains("=")) {
                String[] split = s.split("=");
                properties.setProperty(split[0], split[1]);
            }

        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
