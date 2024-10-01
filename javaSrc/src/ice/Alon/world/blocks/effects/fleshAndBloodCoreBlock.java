package ice.Alon.world.blocks.effects;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.storage.CoreBlock;

/**
 * 血肉核心
 */
public class fleshAndBloodCoreBlock extends CoreBlock {
    public TextureRegion eye;

    public fleshAndBloodCoreBlock(String name) {
        super(name);
        buildType = fleshAndBloodCoreBlockBuild::new;
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-icon")};
    }

    @Override
    public void load() {
        eye = Core.atlas.find(name + "-eye");
        if (eye == null) eye = Core.atlas.find("error");
        super.load();
    }

    public class fleshAndBloodCoreBlockBuild extends CoreBlock.CoreBuild {
        Vec2 movement = new Vec2(1, 0);
        float radius2 = 8 * 50;
        Unit unit = null;
        Seq<Unit> units = new Seq<>();

        @Override
        public void update() {

            Units.nearby(new Rect(x - radius2 / 2f, y - radius2 / 2f, radius2, radius2), (u)->{
                units.add(u);
            });
            if (units.size == 0) unit = null;
            else unit = units.first();
            units.clear();
            super.update();
        }

        @Override
        public void drawSelect() {
            Drawf.dashSquare(Color.red, x, y, radius2);
        }

        @Override
        public void draw() {
            super.draw();
            if (unit == null) {
                Draw.rect(eye, x, y);
            } else {
                //设置vec角度为 获得以(x,y)为起点，(aimX,aimY)到(x,y)矢量的角度
                movement.setAngle(Angles.angle(x, y, unit.x, unit.y));
                Draw.rect(eye, x + movement.x, y + movement.y);
            }
        }
    }
}
