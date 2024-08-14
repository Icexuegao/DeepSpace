package ice.asundry.world.planet;


import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Tmp;
import ice.asundry.world.schematics.IceSchematics;
import mindustry.Vars;
import mindustry.ai.BaseRegistry;
import mindustry.ctype.Content;
import mindustry.game.Schematic;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;

import java.util.Iterator;

@SuppressWarnings("unchecked")

public class ADriBaseRegistry extends BaseRegistry {

    public Seq<BaseRegistry.BasePart> cores = new Seq<>();
    public Seq<BaseRegistry.BasePart> parts = new Seq<>();
    public ObjectMap<Content, Seq<BaseRegistry.BasePart>> reqParts = new ObjectMap<>();
    public ObjectMap<Item, OreBlock> ores = new ObjectMap<>();
    public ObjectMap<Item, Floor> oreFloors = new ObjectMap<>();

    public ADriBaseRegistry() {
    }

    public Seq<BaseRegistry.BasePart> forResource(Content item) {
        return this.reqParts.get(item, Seq::new);
    }

    @Override
    public void load() {
        this.cores.clear();
        this.parts.clear();
        this.reqParts.clear();


        for (Block block : Vars.content.blocks()) {
            if (block instanceof OreBlock ore) {
                if (ore.itemDrop != null && !ore.wallOre && !this.ores.containsKey(ore.itemDrop)) {
                    this.ores.put(ore.itemDrop, ore);
                    continue;
                }
            }

            if (block.isFloor() && block.asFloor().itemDrop != null && !this.oreFloors.containsKey(block.asFloor().itemDrop)) {
                this.oreFloors.put(block.asFloor().itemDrop, block.asFloor());
            }
        }
        Seq<Schematic> sc = IceSchematics.schematics;
        label89:
        for (int i = 0; i < sc.size; i++) {
            Schematic schem = sc.get(i);

            BasePart part = new BasePart(schem);
            Tmp.v1.setZero();
            int drills = 0;
            Iterator<Schematic.Stile> var9 = schem.tiles.iterator();

            while (true) {
                Schematic.Stile tile;
                do {
                    if (!var9.hasNext()) {
                        schem.tiles.removeAll((s) -> s.block.buildVisibility == BuildVisibility.sandboxOnly);
                        part.tier = schem.tiles.sumf((s) -> Mathf.pow(s.block.buildCost / s.block.buildCostMultiplier, 1.4F));
                        if (part.core != null) {
                            cores.add(part);
                        } else if (part.required == null) {
                            parts.add(part);
                        }

                        if (drills > 0) {
                            Tmp.v1.scl(1.0F / (float) drills).scl(0.125F);
                            part.centerX = (int) Tmp.v1.x;
                            part.centerY = (int) Tmp.v1.y;
                        } else {
                            part.centerX = part.schematic.width / 2;
                            part.centerY = part.schematic.height / 2;
                        }

                        if (part.required != null && part.core == null) {
                            ((Seq) reqParts.get(part.required, Seq::new)).add(part);
                        }
                        continue label89;
                    }

                    tile = var9.next();
                    if (tile.block instanceof CoreBlock) {
                        part.core = tile.block;
                    }

                    if (tile.block instanceof ItemSource) {
                        Item config = (Item) tile.config;
                        if (config != null) {
                            part.required = config;
                        }
                    }

                    if (tile.block instanceof LiquidSource) {
                        Liquid config = (Liquid) tile.config;
                        if (config != null) {
                            part.required = config;
                        }
                    }
                } while (!(tile.block instanceof Drill) && !(tile.block instanceof Pump));

                Tmp.v1.add((float) (tile.x * 8) + tile.block.offset, (float) (tile.y * 8) + tile.block.offset);
                ++drills;
            }
        }

        this.cores.sort((b) -> b.tier);
        this.parts.sort();
        this.reqParts.each((key, arr) -> arr.sort());
    }

}
