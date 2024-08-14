package ice.asundry.Game;

import ice.asundry.Var.IceTile;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;

import java.util.Iterator;

public class Game {
    public static void load() {
        new
                Wall("test3") {{
                    size = 1;
                    health = 100;
                    update = true;
                    itemCapacity = 30;
                    hasItems = true;
                    requirements(Category.effect, ItemStack.empty);
                    buildType = () -> new WallBuild() {
                        @Override
                        public void update() {
                            Tile tile = Vars.world.tiles.get((int) x, (int) (y+1));
                            if (tile!=null){
                                Log.info("检测到实体");
                              tile.build=null;
                            }
                            super.update();
                        }
                    };
                }
        };

        new Wall("test2") {{
            size = 1;
            requirements(Category.effect, ItemStack.empty);
            health = 100;
            update = true;
            itemCapacity = 30;
            hasItems = true;
            buildType = () -> new WallBuild() {
                @Override
                public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {

                    IceTile t =new IceTile(tile.x,tile.y+1);
                    t.setFloor(Vars.world.tile(tile.x,tile.y+1).floor());
                    Vars.world.tiles.set(tile.x,tile.y+1,t);
                    t.set();
                    return super.init(tile, team, shouldAdd, rotation);
                }

                @Override
                public void update() {
                    super.update();
                }
            };
        }

            @Override
            public boolean canPlaceOn(Tile tile, Team team, int rotation) {
                Tile tile1 = Vars.world.tile(tile.x, tile.y + 2);
                if (tile1.block()!=Blocks.air)return false;
                return super.canPlaceOn(tile, team, rotation);
            }
        };
        new Wall("test1") {{
            size=2;
            requirements(Category.effect, ItemStack.empty);
            health = 100;
            update = true;
            itemCapacity = 30;
            hasItems = true;
            buildType = () -> new WallBuild() {
                private Seq<Point2> visited = new Seq<>();
                private Queue<Point2> queue = new Queue<>();
                private float timer = 0f;
                public Block drop;

                @Override
                public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
                    /* Blocks.oreCopper*/

                    Block overlay = tile.overlay();
                    if (drop==null)drop=overlay;
                    if (overlay!=Blocks.air){
                        queue.add(new Point2(tile.x, tile.y));
                    }
                    Log.info(overlay.name+drop.name);
                    return super.init(tile, team, shouldAdd, rotation);
                }

                @Override
                public void updateTile() {

                    if (drop.itemDrop != null) {
                        dump(drop.itemDrop);
                    }
                    timer += edelta();
                    if (timer > 20f) {
                        timer = 0f;
                        int size = queue.size;
                        Iterator<Point2> iterator = queue.iterator();
                        Point2[] tmp = new Point2[size * 8];
                        int idx = 0;
                        for (int i1 = 0; i1 < size; ++i1) {
                            var e = iterator.next();
                            iterator.remove();
                            for (int i = 0; i < 9; ++i) {
                                Point2 point = new Point2(Geometry.d8(i).x + e.x, Geometry.d8(i).y + e.y);
                                Tile tile1 = Vars.world.tile(point.x, point.y);
                                if (tile1 == null || visited.contains(point)) continue;
                                if (tile1.overlay() != drop) continue;
                                if (tile1.build instanceof WallBuild &tile1.build!=this)this.kill();
                                if (tile1.block() != Blocks.air) continue;


                                visited.add(point);
                                tmp[idx++] = point;
                            }
                        }
                        for (int i = 0; i < size * 8 && tmp[i] != null; i++) queue.add(tmp[i]);

                        if (drop.itemDrop != null) {
                            if (items.get(drop.itemDrop) < itemCapacity - visited.size) {
                                items.add(drop.itemDrop, visited.size);
                            } else {
                                items.set(drop.itemDrop, itemCapacity);
                            }
                        }
                    }
                    for (Point2 p:visited){
                        Tile tile1 = Vars.world.tile(p.x, p.y);
                     if (tile1.block()!= Blocks.air){
                         visited.remove(p);
                     }
                    }
                    Log.info("数量为:" + visited.size);
                }

                @Override
                public void draw() {
                    super.draw();
                    Draw.color(Color.red);
                    Draw.alpha(0.4f);
                    for (Point2 e : visited) {
                        Fill.crect(e.x * Vars.tilesize - Vars.tilesize / 2, e.y * Vars.tilesize - Vars.tilesize / 2, Vars.tilesize, Vars.tilesize);
                    }
                    Draw.reset();
                }
            };
        }};
    }
}
