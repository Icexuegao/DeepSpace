package ice.Alon.asundry.world;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;

public class Ceshikuangji extends Block {
    public Ceshikuangji(String name) {
        super(name);
        buildType = ceshikuangjibuild::new;
        update = true;
        buildVisibility = BuildVisibility.shown;
    }

    public class ceshikuangjibuild extends Building {
        public Seq<Tile> seq = new Seq<>();
        public Seq<Tile> seq2 = new Seq<>();
        public int pos = 0;
        Block b = Blocks.darksand;

        @Override
        public void drawSelect() {
          /*  float sin = Mathf.absin(Time.time, 4, 1);
            for (int i = 0; i < seq.size; i++) {
                Tile t = seq.get(i);
                Draw.color(Color.red);
                Draw.alpha(sin);
                Fill.square(t.worldx(), t.worldy(), tilesize / 2f);
            }
            for (int i = 0; i < seq2.size; i++) {
                Tile t = seq2.get(i);
                Draw.color(Color.blue);
                Draw.alpha(-sin);
                Fill.square(t.worldx(), t.worldy(), tilesize / 2f);
            }*/
            super.drawSelect();
        }

        @Override
        public void draw() {
            float sin = Mathf.absin(Time.time, 4, 1);
            for (int i = 0; i < seq.size; i++) {
                Tile t = seq.get(i);
                Draw.color(Color.red);
                Draw.alpha(sin);
                Fill.square(t.worldx(), t.worldy(), Vars.tilesize / 2f);
            }
            for (int i = 0; i < seq2.size; i++) {
                Tile t = seq2.get(i);
                Draw.color(Color.blue);
                Draw.alpha(-sin);
                Fill.square(t.worldx(), t.worldy(), Vars.tilesize / 2f);
            }
            super.draw();
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            get8Tile(tile, b);
            return super.init(tile, team, shouldAdd, rotation);
        }


        public void get8Tile(Tile tile, Block getTile) {
            pos++;
            //每一行从左开始
            Tile tile1 = Vars.world.tile(tile.x - 1, tile.y + 1);
            Tile tile2 = Vars.world.tile(tile.x, tile.y + 1);
            Tile tile3 = Vars.world.tile(tile.x + 1, tile.y + 1);

            Tile tile4 = Vars.world.tile(tile.x - 1, tile.y);
            Tile tile5 = Vars.world.tile(tile.x + 1, tile.y);

            Tile tile6 = Vars.world.tile(tile.x - 1, tile.y - 1);
            Tile tile7 = Vars.world.tile(tile.x, tile.y - 1);
            Tile tile8 = Vars.world.tile(tile.x + 1, tile.y - 1);
            Tile[] tiles1 = {tile2, tile4, tile5, tile7, tile1, tile3, tile6, tile8};
            isGttTile(tiles1, getTile);
            seqAdd(seq2, tiles1);
            Seq<Tile> seq4 = new Seq<>();
            seq2.each((t1) -> {
                if (seq.contains(t1)) {
                    seq4.add(t1);
                } else {
                    seq.add(t1);
                    Log.info(seq.toString());
                }
            });
            seq2.removeAll(seq4);
            seq4.clear();


            if (!(pos >= 20) && seq2.size >= 1) {
                Seq<Tile> seq3 = new Seq<>(seq2);
                seq2.clear();
                Tile tile9;
                for (int i = 0; i < seq3.size; i++) {
                    tile9 = seq3.get(i);
                    if (tile9 != null) {
                        get8Tile(tile9, b);
                    }
                }

            }
        }
    }

    public void isGttTile(Tile[] tiles, Block getTile) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null && tiles[i].floor() != getTile) {
                tiles[i] = null;
            }
        }
    }

    public void seqAdd(Seq<Tile> seq, Tile[] tiles) {
        for (Tile e : tiles) {
            if (e != null) {
                seq.add(e);
            }
        }
    }
}