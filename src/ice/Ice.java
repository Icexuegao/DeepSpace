package ice;


import ice.content.*;
import ice.content.blocks.IceBlocks;
import ice.library.EventType;
import ice.parse.JTContents;
import ice.ui.UI;
import ice.ui.menus.SettingValue;
import ice.world.blocks.effects.digitalStorage.DigitalConduit;
import ice.world.blocks.effects.digitalStorage.DigitalStorage;
import ice.world.blocks.effects.digitalStorage.DigitalUnloader;
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
        IceBlocks.load();
        IceUnitTypes.INSTANCE.load();
        IcePlanets.INSTANCE.load();
        IceWeathers.load();
        JTContents.INSTANCE.load();
        new DigitalStorage("12");
        new DigitalConduit("34");
        new DigitalUnloader("56");
    }
}