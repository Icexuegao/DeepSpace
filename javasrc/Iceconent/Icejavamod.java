package Iceconent;

import Iceconent.content.*;
import mindustry.mod.Mod;

public class Icejavamod extends Mod {

    public Icejavamod() {
    }

    @Override
    public void loadContent() {
        IceUnitTypes.load();
        IceStatus.load();
        IceItems.load();
        IceFloor.load();
        IceBlocks.load();
        IcePlanet.load();
        IceTechTree.load();
        IceSectorPresets.load();
    }
}