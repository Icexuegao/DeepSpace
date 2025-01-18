package ice;


import ice.content.*;
import ice.library.EventType;
import ice.parse.JTContents;
import ice.ui.UI;
import ice.ui.menus.SettingValue;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

public class Ice extends Mod {
    public static Mods.LoadedMod ice;
    public final static String NAME = "ice";
    public static final String[] author = {"Alon", "洋葱", "Elegy"};

    public Ice() {
    }

    @Override
    public void init() {
        SettingValue.INSTANCE.init();
        EventType.INSTANCE.init();
        UI.INSTANCE.init();
    }

    @Override
    public void loadContent() {
        ice = Vars.mods.getMod(this.getClass());
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.INSTANCE.load();
        IceUnitTypes.INSTANCE.load();
        JTContents.INSTANCE.load();
    }
}