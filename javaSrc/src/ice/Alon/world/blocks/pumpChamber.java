package ice.Alon.world.blocks;

import ice.Alon.asundry.BaseTool.codebase.DrawTools;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.blocks.production.Pump;
import mindustry.world.draw.*;

public class pumpChamber extends Pump {
    TextureRegion[] arr = new TextureRegion[4];
    DrawBlock drawer = new DrawMulti(new DrawRegion("-b2"), DrawTools.setLiquidTileSize(new DrawLiquidTile(), 2, 2, 2, 2), new DrawDefault() {
        public void draw(Building build) {
            IcetopmBuild building = (IcetopmBuild) build;
            float v = building.liquids.get(building.liquidDrop);
            int i = building.i;
            if (v + 1 >= liquidCapacity) {
                if (building.man | i == 3) {
                    building.man = true;
                    Draw.rect(arr[3], building.x, building.y, build.drawrot());
                } else {
                    extracted(build, i);
                }
            } else {
                extracted(build, i);
            }
        }

        private void extracted(Building build, int i) {
            Draw.rect(arr[i], build.x, build.y, build.drawrot());
        }

    });

    public pumpChamber(String name) {
        super(name);
        liquidCapacity = 30;
        squareSprite = false;
        buildType = IcetopmBuild::new;
        update = true;
    }

    @Override
    public void load() {
        arr[0] = null;
        arr[1] = new TextureRegion(Core.atlas.find(name ));
        arr[2] = new TextureRegion(Core.atlas.find(name+2));
        arr[3] = new TextureRegion(Core.atlas.find(name +3));
        drawer.load(this);
        super.load();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class IcetopmBuild extends Pump.PumpBuild {

        public float time = 0;
        public int i = 1;
        boolean fi = false;
        boolean man = false;

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }


        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(time);
            write.i(i);
            write.bool(fi);
            write.bool(man);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            time = read.f();
            i = read.i();
            fi = read.bool();
            man = read.bool();
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void updateTile() {
            if (liquids.get(liquidDrop) <= liquidCapacity - 0.01) {
                man = false;
            }
            if (time / 60 >= 0.5) {
                time = 0;
                if (i >= 3) {
                    fi = true;
                } else if (i <= 1) {
                    fi = false;
                }
                if (fi) {
                    i--;
                } else {
                    i++;
                }
            } else {
                time += Time.delta;
            }
            super.updateTile();
        }
    }
}
