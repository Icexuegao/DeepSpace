package ice.Alon.Text;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import ice.Alon.library.PathfindAlgorithm.Pathfind;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;

public class Text extends Block {

    public Text(String name) {
        super(name);
        itemCapacity = 10;
        health = 100;
        size = 1;
        buildType = TextBuild::new;
        update = true;
        requirements(Category.effect, ItemStack.with(Items.copper, 10));
    }

    public static int rp(Point2 x, Point2 r) {
        if (r.y > x.y) {
            Vars.world.tile(x.x, x.y).build.rotation = 3;
            return 3;
        }
        if (r.y < x.y) {
            Vars.world.tile(x.x, x.y).build.rotation = 1;
            return 1;
        }
        if (r.x > x.x) {
            Vars.world.tile(x.x, x.y).build.rotation = 2;
            return 2;
        }
        if (r.x < x.x) {
            Vars.world.tile(x.x, x.y).build.rotation =0;
            return 0;
        }
        return 0;
    }

    public static class TextBuild extends Building {
        Pathfind pathfind;
        Seq<Point2> point2s;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            Point2 begin = new Point2(tile.x, tile.y);
            pathfind = new Pathfind(begin);
            pathfind.setDraw(false);
            return super.init(tile, team, shouldAdd, rotation);
        }


        Point2 start;

        @Override
        public void draw() {
            if (start != null) {
                Fill.crect(start.x * Vars.tilesize - (float) Vars.tilesize / 2, start.y * Vars.tilesize - (float) Vars.tilesize / 2, Vars.tilesize, Vars.tilesize);
                Draw.color(Color.red);
                Draw.alpha(0.5f);
            }
            Draw.rect(Blocks.copperWall.region, x, y);
        }

        float o = 0;
        float f = 0;
        int c = 0;

        @Override
        public void updateTile() {
            if (point2s != null) {
                f += Time.delta;
                if (f >= 15) {
                    f = 0;
                    if (c >= 1) {
                        c--;
                    } else {
                        c = point2s.size - 2;
                    }
                    start = point2s.get(c);
                    int r = rp(point2s.get(c + 1), start);
                    Vars.world.tile(start.x, start.y).setBlock(Blocks.conveyor, team, r);
                }
            }
            if (pathfind.getPathFirst()) {
                point2s = pathfind.getPath();
            }
            o += Time.delta;
            if (o >= 10) {
                o = 0;
            }
        }
    }
}