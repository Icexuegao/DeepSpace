package Iceconent.World.Block;

import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.state;

public class haodiancnagku extends Block {
    public boolean coreMerge = true;

    public haodiancnagku(String name) {
        super(name);
        hasItems = true;
        solid = true;
        update = false;
        destructible = true;
        separateItemCapacity = true;
        group = BlockGroup.transportation;
        flags = EnumSet.of(BlockFlag.storage);
        allowResupply = true;
        envEnabled = Env.any;
        buildType = StorageBuild::new;
    }

    public static void incinerateEffect(Building self, Building source) {
        if (Mathf.chance(0.3)) {
            Tile edge = Edges.getFacingEdge(source, self);
            Tile edge2 = Edges.getFacingEdge(self, source);
            if (edge != null && edge2 != null && self.wasVisible) {
                Fx.coreBurn.at((edge.worldx() + edge2.worldx()) / 2f, (edge.worldy() + edge2.worldy()) / 2f);
            }
        }
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    public class StorageBuild extends Building {
        public @Nullable Building linkedCore;

        @Override
        public boolean acceptItem(Building source, Item item) {
            /** 判断电力是否到达1，否则就返回false  */
            if (power.status >= 0.999) {
                return linkedCore != null ? linkedCore.acceptItem(source, item) : items.get(item) < getMaximumAccepted(item);
            } else {
                return false;
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (linkedCore != null) {
                if (linkedCore.items.get(item) >= ((CoreBlock.CoreBuild) linkedCore).storageCapacity) {
                    incinerateEffect(this, source);
                }
                ((CoreBlock.CoreBuild) linkedCore).noEffect = true;
                linkedCore.handleItem(source, item);
            } else {
                super.handleItem(source, item);
            }
        }

        @Override
        public void itemTaken(Item item) {
            if (linkedCore != null) {
                linkedCore.itemTaken(item);
            }
        }

        @Override
        public int removeStack(Item item, int amount) {
            int result = super.removeStack(item, amount);

            if (linkedCore != null && team == state.rules.defaultTeam && state.isCampaign()) {
                state.rules.sector.info.handleCoreItem(item, -result);
            }

            return result;
        }

        @Override
        /** 获取最大接受数  */
        public int getMaximumAccepted(Item item) {
            /** linkedCore判断是否链接到核心  */
            return linkedCore != null ? linkedCore.getMaximumAccepted(item) : itemCapacity;
        }

        @Override
        public int explosionItemCap() {
            /**当连接到核心时，容器/保险库的爆炸性显着降低*/
            return linkedCore != null ? Math.min(itemCapacity / 60, 6) : itemCapacity;
        }

        @Override
        public void drawSelect() {
            if (linkedCore != null) {
                linkedCore.drawSelect();
            }
        }


        @Override
        public void overwrote(Seq<Building> previous) {
            //only add prev items when core is not linked
            if (linkedCore == null) {
                for (Building other : previous) {
                    if (other.items != null && other.items != items) {
                        items.add(other.items);
                    }
                }

                items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
            }
        }

        @Override
        public boolean canPickup() {
            return linkedCore == null;
        }
    }
}
