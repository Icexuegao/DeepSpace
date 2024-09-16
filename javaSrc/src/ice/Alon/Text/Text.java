package ice.Alon.Text;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.util.Log;
import arc.util.Time;
import ice.Alon.library.IceMathf;
import ice.Alon.world.blocks.environment.IceOreBlock;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;

import static mindustry.Vars.world;

public class Text extends Wall {

    public Text(String name) {
        super(name);
        itemCapacity = 10;
        liquidCapacity = 100;
        hasItems = true;
        hasPower = true;
        hasLiquids = true;
        hasConsumers = true;
        health = 100;
        size = 1;
        saveConfig = true;
        configurable = true;
        buildType = TextBuild::new;
        update = true;
        requirements(Category.liquid, ItemStack.with(Items.copper, 10));
    }

    public class TextBuild extends Wall.WallBuild implements Ranged {

        public TextBuild() {
        }

        @Override
        public void damage(float damage) {
            damage -= damage;
            super.damage(damage);
        }

        boolean showOres = true;
        float startTime;

        public float curTime() {
            return Time.time - startTime;
        }

        @Override
        public void created() {
            startTime = Time.time;
        }

        float range = 15f * 8f;

        @Override
        public float range() {
            return range * potentialEfficiency;
        }

        public float radarRot() {
            return (curTime() * speed) % 360f;
        }

        public float speed = 0.8f;
        public Color effectColor = Color.valueOf("4b95ff");

        @Override
        public void draw() {
            Draw.z(Layer.light);
            Draw.alpha(0.6f);
            Lines.stroke(2.5f, effectColor);
            if (showOres) {
                Draw.alpha(1f - (curTime() % 120f) / 120f);
                Lines.circle(x, y, (curTime() % 120f) / 120f * range());

                Draw.alpha(0.3f);
                Fill.arc(x, y, range(), 18 / 360f, radarRot());
            }

            Draw.alpha(0.2f);
            Lines.circle(x, y, range());
            Lines.circle(x, y, range() * 0.95f);

            Draw.reset();
            if (showOres) {
            } //locateOres(range());


            //  Drawf.dashRect(Color.red, x - radius / 2, y - radius / 2, radius, radius);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return true;
        }

        float radius = 40;
        float v = radius / 2;
        int i = 0;

        @Override
        public void drawSelect() {
            Drawf.dashRect(Color.red, x - radius / 2, y - radius / 2, radius, radius);
        }

        @Override
        public void update() {
            i++;
            IceMathf.goe60(i, ()->{
                i = 0;
                Tile[] tiles = {

                        world.tile(tile.x - 1, tile.y),

                        world.tile(tile.x + 1, tile.y),

                        world.tile(tile.x, tile.y - 1),

                        world.tile(tile.x, tile.y + 1)};

                for (Tile tile : tiles) {
                    if (tile.overlay() instanceof IceOreBlock ice) {
                        Log.info("设置了一个块");
                        if (tile.block() != null) {
                            Draw.z(Layer.max);
                            Draw.alpha(1f);
                            Draw.rect(ice.itemDrop.uiIcon, tile.x * 8, tile.y * 8 + 8);
                        }
                    }


                }

            });

            super.update();
        }
    }
}
