package ice.Alon;

import ice.Alon.asundry.BaseTool.io.FileTool;
import ice.Alon.content.IceBlocks;
import ice.Alon.content.IceItems;
import ice.Alon.content.IceLiquids;
import ice.Alon.content.IceStatus;
import ice.Alon.ui.IceContentInfoDialog;
import iceKotlin.Alon.KtItems;
import mindustry.Vars;
import mindustry.mod.Mod;

import java.util.Properties;

public class Ice extends Mod {
    public static final String MOD_NAME = "ice";
    public static String Display_Name;

    public Ice() {
    }

    @Override
    public void init() {
        Vars.ui.content = new IceContentInfoDialog();
        super.init();
    }

    @Override
    public void loadContent() {
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
        return "{" + MOD_NAME + "}";
    }

    protected void stone() {
        Properties properties = FileTool.getJarFileProperties("mod.hjson");
        Display_Name = properties.getProperty("displayName");
    }
}
