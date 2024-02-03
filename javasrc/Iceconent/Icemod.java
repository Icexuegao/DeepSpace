package Iceconent;

import Iceconent.World.Bullet.IceBasicBulletTypes;
import Iceconent.World.Bullet.IceMissileBulletTypes;
import Iceconent.content.*;
import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.Mod;

import static mindustry.game.EventType.ClientLoadEvent;

public class Icemod extends Mod {
    private static final String MOD_NAME = "ice-mod";

    public Icemod() {
    }

    public static String getName(String string) {
        return MOD_NAME + "-" + string;
    }

    public void test() {
        Events.on(ClientLoadEvent.class, e -> Seq.with(Vars.content.getContentMap()).flatten().each(v -> {
            if (v instanceof UnlockableContent) {
                ((UnlockableContent) v).unlock();
            }
        }));
        Vars.maxSchematicSize = 128;
    }

    @Override
    public final void loadContent() {
        IceMissileBulletTypes.load();
        IceBasicBulletTypes.load();
        IceUnitTypes.load();
        IceStatus.load();
        IceItems.load();
        IceFloor.load();
        IceBlocks.load();
        IcePlanet.load();
        IceSectorPresets.load();
        IceTechTree.load();
        test();
    }
}