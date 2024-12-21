package ice.world.blocks.effects;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Rand;
import arc.struct.ObjectMap;
import ice.type.content.IceItem;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.StorageBlock;

import static arc.graphics.g2d.TextureAtlas.AtlasRegion;

public class ResBox extends StorageBlock {
    /**
     * 空的top
     */
    public TextureRegion resBoxNull;
    /**
     * 不同地形的贴图和地形name映射
     */
    public final ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

    public ResBox(String name) {
        super(name);
        size = 1;
        health = 40;
        category = Category.effect;
        itemCapacity = 20;
        buildType = ResBoxBuild::new;
    }

    @Override
    public void load() {
        resBoxNull = getRegion("1");
        super.load();
        Vars.content.blocks().each((b)->{
            if (b instanceof Floor floor) {
                TextureRegion res = getRegion(floor.name);
                regions.put(floor.name, res);
            }
        });
    }

    public AtlasRegion getRegion(String s) {
        return Core.atlas.find(name + "-" + s);
    }

    public class ResBoxBuild extends Building {
        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        @Override
        public void draw() {
            String s = tile.floor().name;
            AtlasRegion resBox1 = getRegion(s);
            if (items.empty()) {
                Draw.rect(region, x, y);
                if (!resBox1.name.equals("error")) {
                    Draw.rect(resBox1, x, y);
                }
                Draw.rect(resBoxNull, x, y);
            } else {
                Draw.rect(region, x, y);
                if (!resBox1.name.equals("error")) {
                    Draw.rect(resBox1, x, y);
                }
            }
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            super.init(tile, Team.derelict, shouldAdd, rotation);
            Item item = Vars.content.items().random();
            while (!(item instanceof IceItem)) {
                item = Vars.content.items().random();
            }
            items.add(item, new Rand().random(1, 20));
            return this;
        }
    }
}
