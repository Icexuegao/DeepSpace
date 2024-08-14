package ice.Alon.asundry.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class IceDrawPistons extends DrawBlock {
    public float /** 移动距离*/
            sinMag = 2f, /**
     * 速度
     */
    sinScl = 6f, sinOffset = 50f, sideOffset = 0f, lenOffset = 20f, horiOffset = 0f, /**
     * 角度
     */
    angleOffset = 0f;
    public int sides = 4;
    public String suffix = "-piston";
    public TextureRegion region1, region2, regiont, iconRegion;

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (iconRegion.found()) {
            Draw.rect(iconRegion, plan.drawx(), plan.drawy());
        }
    }

    @Override
    public void draw(Building build) {
        for (int i = 0; i < sides; i++) {
            float len = Mathf.absin(build.totalProgress() + sinOffset + sideOffset * sinScl * i, sinScl, sinMag) + lenOffset;
            float angle = angleOffset + i * 360f / sides;
            TextureRegion reg = regiont.found() && (Mathf.equal(angle, 315) || Mathf.equal(angle, 135)) ? regiont : angle >= 135 && angle < 315 ? region2 : region1;

            if (Mathf.equal(angle, 315)) {
                Draw.yscl = -1f;
            }

            Tmp.v1.trns(angle, len, -horiOffset);
            Draw.rect(reg, build.x + Tmp.v1.x, build.y + Tmp.v1.y, angle);

            Draw.yscl = 1f;
        }
    }

    @Override
    public void load(Block block) {
        super.load(block);

        region1 = Core.atlas.find(block.name + suffix + "0", block.name + suffix);
        region2 = Core.atlas.find(block.name + suffix + "1", block.name + suffix);
        regiont = Core.atlas.find(block.name + suffix + "-t");
        iconRegion = Core.atlas.find(block.name + suffix + "-icon");
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{iconRegion};
    }
}
