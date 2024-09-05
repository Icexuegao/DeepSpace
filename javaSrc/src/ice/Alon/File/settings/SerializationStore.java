package ice.Alon.File.settings;

import arc.Core;

import java.io.*;

/**
 * 序列化操作流
 */
@SuppressWarnings("all")
public class SerializationStore {

    /**
     * 一个序列化操作对象
     */
    public final static SerializationStore serializationStore = new SerializationStore();
    /**
     * 存储数据文件名称<></>
     */
    private static final String ICE_CONFIG_BIN = "iceConfig.bin";
    /**
     * 用来存储乱七八糟模组数据的序列化的对象
     */
    public static SerializationStoreBuild ser = new SerializationStoreBuild();
    /**
     * 序列化文件位置
     */
    private static String path = Core.files.getLocalStoragePath();

    /**
     * 你想要我的序列化操作流对象创建方法?不是!你配吗? 要我的序列化操作流对象创建方法.已经失传了懂吗?已经失传了😭
     */
    private SerializationStore() {
    }

    private boolean exists() {
        return new File(path + "/" + ICE_CONFIG_BIN).exists();
    }

    /**
     * 抛错仙人
     */
    public void load() {
        if (!exists()) {
            try {
                writeSer();
            } catch (Exception e) {
                throw new RuntimeException(e + "模组数据初始化错误!!![1]");
            }
        }
        try {
            read();
        } catch (Exception e) {
            throw new RuntimeException(e + "模组数据初始化错误!!![2]");
        }
    }

    /**
     * 读取以前设置,把属性赋值给新ser,主要是为以前序列化对象增加新属性,否则为nul
     */
    private void read() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "/" + ICE_CONFIG_BIN));
        if (ois.readObject() instanceof SerializationStoreBuild build) {
            ser = build;
        } else {
            new File(path + "/" + ICE_CONFIG_BIN).delete();
            writeSer();
        }
    }

    /**
     * 你看我这个聪明吧
     */
    public void write(Settings settings) {
        settings.setSettings(ser);
        try {
            writeSer();
        } catch (IOException e) {
            throw new RuntimeException(e + "写入设置失败");
        }
    }

    /**
     * 序列化数据
     */
    private static void writeSer() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/" + ICE_CONFIG_BIN));
        oos.writeObject(ser);
        oos.flush();
        oos.close();
    }

    /**
     * 设置接口
     */
    public interface Settings {
        void setSettings(SerializationStoreBuild s);
    }

    /**
     * 序列化对象
     */
    private static class SerializationStoreBuild implements Externalizable, Serializable {
        /**
         * 不要更改!!!会导致设置失效嘎嘎报错
         */
        private static final long serialVersionUID = 920920920920920920L;

        /**
         * 构造方法设置默认值 防止新字段为null
         */
        public SerializationStoreBuild() {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
        }

        /**
         * 重写读取方法,新字段会被设置成null,防止空指针报错,记得判断设置默认值
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        }
    }
}
