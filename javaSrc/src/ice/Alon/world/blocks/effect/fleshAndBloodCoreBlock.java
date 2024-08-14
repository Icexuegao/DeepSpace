package ice.Alon.world.blocks.effect;

import mindustry.world.blocks.storage.CoreBlock;

public class fleshAndBloodCoreBlock extends CoreBlock {
    public fleshAndBloodCoreBlock(String name) {
        super(name);
        buildType = fleshAndBloodCoreBlockBuild::new;
    }

    public class fleshAndBloodCoreBlockBuild extends CoreBlock.CoreBuild {
        @Override
        public void draw() {
            super.draw();
        }
    }
}
