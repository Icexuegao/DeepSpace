package ice;

import ice.asundry.BaseTool.io.FileTool;
import ice.content.IceBlocks;
import ice.content.IceItems;
import ice.content.IceLiquids;
import ice.content.IceStatus;
import ice.ui.IceContentInfoDialog;
import iceKotlin.KtItems;
import iceScala.ScalaContentLoad;
import iceScala.ScalaItem;
import mindustry.Vars;
import mindustry.mod.Mod;

import java.util.Properties;

public class Ice extends Mod {
    public static final String MOD_NAME = "ice";
    public static String Display_Name;
    public static final String Author = "Alon";


    public Ice() {
    }


    @Override
    public void init() {
        Vars.ui.content = new IceContentInfoDialog();
        super.init();
    }

    @Override
    public void loadContent() {
        ScalaContentLoad load =new ScalaContentLoad();
        load.load();
        KtItems.KtItems.INSTANCE.load();
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.load();

       /* IcePlanets.load();
        Tool.load();
        stone();
        DisplayName.displayName();
        Game.load();
        TreeTech.load();*/
    }

    @Override
    public String toString() {
        return "{" + MOD_NAME + "-" + Author + "}";
    }

    protected void stone() {
        Properties properties = FileTool.getJarFileProperties("mod.hjson");
        Display_Name = properties.getProperty("displayName");
    }
}
