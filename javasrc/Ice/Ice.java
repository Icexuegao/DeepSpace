package Ice;

import Ice.content.IceBlocks;
import Ice.content.IceItems;
import Ice.content.IceLiquids;
import Ice.content.IceStatus;
import Ice.ui.IceContentInfoDialog;
import mindustry.Vars;
import mindustry.mod.Mod;
import scala.SCItems;

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
        SCItems.SCItems$.MODULE$.load();
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

   /* protected void stone() {
        Properties properties = FileTool.getJarFileProperties("mod.hjson");
        Display_Name = properties.getProperty("displayName");
    }*/
}
