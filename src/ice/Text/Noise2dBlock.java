package ice.Text;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.noise.Simplex;
import ice.library.IceString;
import ice.ui.Tex.IceTex;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;

import static arc.graphics.Color.blue;
import static mindustry.Vars.world;

public class Noise2dBlock extends Block {

    public Noise2dBlock(String name) {
        super(name);
        itemCapacity = 10;
        health = 100;
        size = 1;
        buildType = TextBuild::new;
        update = true;
        requirements(Category.effect, ItemStack.with(Items.copper, 10));
        configurable = true;
    }

    public class TextBuild extends Building {
        ObjectMap<Tile, Float> map = new ObjectMap<>();

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void buildConfiguration(Table table) {
            table.setBackground(IceTex.background);
            TextField octavest = new TextField("");
            TextField persistencet = new TextField("");
            TextField scalet = new TextField("");
            TextField fst = new TextField("");

            table.add("octaves:" + octaves).update((s)->{
                String o = octavest.getText();
                boolean numeric4 = IceString.isNumeric(o);
                if (!o.isEmpty() && numeric4) octaves = Float.parseFloat(o);
                s.setText("octaves:" + octaves);
            });

            table.row();
            table.add("persistence:" + persistence).update((s)->{
                String o = persistencet.getText();
                boolean numeric4 = IceString.isNumeric(o);
                if (!o.isEmpty() && numeric4) persistence = Float.parseFloat(o);
                s.setText("persistence:" + persistence);
            });
            table.row();
            table.add("scale:" + scale).update((s)->{
                String o = scalet.getText();
                boolean numeric4 = IceString.isNumeric(o);
                if (!o.isEmpty() && numeric4) scale = Float.parseFloat(o);
                s.setText("scale:" + scale);
            });
            table.row();

            table.add("fs:" + fs).update((s)->{
                String o = fst.getText();
                boolean numeric4 = IceString.isNumeric(o);
                if (!o.isEmpty() && numeric4) fs = Float.parseFloat(o);
                s.setText("fs:" + fs);
            });
            table.row();


            table.add("octaves");
            table.add(octavest);
            table.row();


            table.add("persistence");
            table.add(persistencet);
            table.row();


            table.add("scale");
            table.add(scalet);
            table.row();

            table.add("fs");
            table.add(fst);
            table.row();

            super.buildConfiguration(table);
        }

        float octaves = 1;
        float fs = 0.7f;
        float persistence = 0.1f;
        float scale = 0.006f;

        void next() {
            map.clear();
            world.tiles.eachTile((t)->{
                Float snoise = Simplex.noise2d(0, octaves, persistence, scale, t.x, t.y);
                map.put(t, snoise);
            });
        }

        @Override
        public void draw() {
            if (map.size > 0) {
                map.each((t, f)->{
                    Draw.color(blue);
                    if (f >= fs) Fill.rect(t.x * 8, t.y * 8, 8, 8);

                });
            }
        }

        int i = 0;

        @Override
        public void updateTile() {
            i++;
            if (i >= 30) {
                next();
                i = 0;
            }

        }
    }
}