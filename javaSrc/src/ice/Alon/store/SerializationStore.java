package ice.Alon.store;

import arc.Core;

import java.io.*;

@SuppressWarnings("all")
public class SerializationStore implements Serializable {
    /**
     * 用来存储乱七八糟模组数据的序列化的对象
     */
    public static SerializationStore ser;
    public static String path = Core.files.getLocalStoragePath();
    /**
     * 不要更改!!!会导致设置失效嘎嘎报错
     */
    private static final long serialVersionUID = 920920920920920920L;

    private SerializationStore() {
    }

    public static boolean exists() {
        return new File(path + "/iceConfig.bin").exists();
    }

    public static void init() throws Exception {
        ser = new SerializationStore();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/iceConfig.bin"));
        oos.writeObject(ser);
        oos.flush();
        oos.close();
    }

    public static void load() throws Exception {
        if (!exists()) {
            init();
        }
        read();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/iceConfig.bin"));
        oos.writeObject(ser);
        oos.flush();
        oos.close();
    }

    public static void write(Settings settings) throws Exception {
        if (!exists()) {
            init();
        }
        read();
        settings.setSettings(ser);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/iceConfig.bin"));
        oos.writeObject(ser);
        oos.flush();
        oos.close();
    }

    public static void read() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "/iceConfig.bin"));
        ser = (SerializationStore) ois.readObject();
    }

    public interface Settings {
        void setSettings(SerializationStore s);
    }
}
