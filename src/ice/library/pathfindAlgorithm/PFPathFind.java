package ice.library.pathfindAlgorithm;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import ice.library.sortAlgorithm.InsertionSorting;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import java.util.IdentityHashMap;

public class PFPathFind {

    Tile target;
    Tile begin;
    int timer = 0;

    public PFPathFind(Tile begin, Tile target) {
        this.begin = begin;
        this.target = target;
        low = new Point2(begin.x, begin.y);
    }

    public void draw() {
        Draw.color(Color.red);
        Draw.alpha(0.4f);
        visited.each(p->Fill.crect(p.x * Vars.tilesize - (float) Vars.tilesize / 2, p.y * Vars.tilesize - (float) Vars.tilesize / 2, Vars.tilesize, Vars.tilesize));
    }

    boolean b;

    public void update() {
        if (b) return;
        timer++;
        if (timer >= 1) {
            timer = 0;
            traversal();
        }
    }

    public int forecastCost(Point2 currentTile) {
        return Math.abs(currentTile.x - target.x) + Math.abs(currentTile.y - target.y);
    }

    Seq<Point2> visited = new Seq<>();

    public Object[] lowP(IdentityHashMap<String, Point2> map) {
        if (map.isEmpty()) return null;
        Seq<String> seq = new Seq<>(map.keySet().toArray(new String[0]));
        String[] array = seq.toArray();
        int[] indices = new int[array.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = Integer.parseInt(array[i]);
        }
        int[] sort = new InsertionSorting().sort(indices);
        int i = sort[0];
        int i1 = seq.indexOf(String.valueOf(i));
        String s = seq.get(i1);
        return new Object[]{map.get(s), s};
    }

    Point2 low;
    IdentityHashMap<String, Point2> costMap = new IdentityHashMap<>();

    public void traversal() {
        if (low.equals(target.x, target.y)) {
            b = true;
            return;
        }
        for (int i = 0; i < 4; i++) {
            Point2 point = new Point2(Geometry.d4(i).x + low.x, Geometry.d4(i).y + low.y);
            Tile tile1 = Vars.world.tile(point.x, point.y);
            if (tile1 == null || visited.contains(point)) {
                continue;
            }

            // if (tile1.build != null) continue;
            if (tile1.block() == Blocks.air || tile1.block() instanceof CoreBlock) {

            } else {
                continue;
            }

            int i1 = forecastCost(point);

            String s = String.valueOf(i1);
            costMap.put(s, point);
            visited.add(point);
        }
        Object[] objects = lowP(costMap);
        if (objects == null) return;
        low = (Point2) objects[0];
        costMap.remove((String) objects[1]);
    }
}
