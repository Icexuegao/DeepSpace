package ice;

import ice.Alon.AlonContentLoad;
import ice.Alon.asundry.BaseTool.io.FileTool;
import mindustry.mod.Mod;

import java.util.Properties;

public class Ice extends Mod {
    public static final String MOD_NAME = "ice";
    public static String Display_Name;

    public Ice() {
    }
    /**
     * 初始化mod内容 一般来说你们不用动
     */
    @Override
    public void init() {
        AlonContentLoad.init();
        super.init();
    }

    /**
     * 用于加载content内容
     */
    @Override
    public void loadContent() {
        AlonContentLoad.load();


    }

    protected void stone() {
        Properties properties = FileTool.getJarFileProperties("mod.hjson");
        Display_Name = properties.getProperty("displayName");
    }
}
