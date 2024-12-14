package ice.content.blocks;

import arc.Core;
import arc.struct.Seq;
import ice.Ice;
import mindustry.Vars;
import mindustry.world.Block;

public class IceBlocks {
    public static Seq<Block> blocks =new Seq<>();
    public static void load() {
        OreBlocks.load();
        EnvironmentBlocks.load();
        TransportBlocks.load();
        liquidBlocks.load();
        FactoryBlocks.load();
        EffectBlocks.laod();
        loadName();
    }

    public static void loadName() {
        Vars.content.blocks().each((b)->{
            if (b.minfo.mod == Ice.ice) {
                blocks.add(b);
                b.localizedName = Core.bundle.get("block." + b.name.replace(Ice.NAME + "-", "") + ".name", b.name);
                b.description = Core.bundle.getOrNull("block." + b.name.replace(Ice.NAME + "-", "") + ".description");
                b.details = Core.bundle.getOrNull("block."+ b.name.replace(Ice.NAME + "-", "") + ".details");
            }
        });
    }
}
