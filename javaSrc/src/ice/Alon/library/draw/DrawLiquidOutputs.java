package ice.Alon.library.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
/**绘制单张LiquidOutput,与旋转方向*/
public class DrawLiquidOutputs extends DrawBlock {
    public TextureRegion liquidOutputRegion;
    public String name;
    public int rotation;

    public Blending blending;
    public float alpha;
    public float glowScale;
    public float glowIntensity;
    public float rotateSpeed;
    public float layer;
    public boolean rotate;
    public Color color;

    public DrawLiquidOutputs(String name, int rotation) {
        {
            this.name = name;
            this.rotation = rotation;

            blending = Blending.additive;
            alpha = 0.9F;
            glowScale = 10.0F;
            glowIntensity = 0.5F;
            rotateSpeed = 0.0F;
            layer = 31.0F;
            rotate = false;
            color = Color.red.cpy();
        }
    }

    @Override
    public void draw(Building build) {
        Draw.rect(liquidOutputRegion, build.x, build.y, rotation * 90);

        if (!(build.warmup() <= 0.001F)) {
            float z = Draw.z();
            if (this.layer > 0.0F) {
                Draw.z(this.layer);
            }

            Draw.blend(this.blending);
            Draw.color(this.color);
            Draw.alpha((Mathf.absin(build.totalProgress(), this.glowScale, this.alpha) * this.glowIntensity + 1.0F - this.glowIntensity) * build.warmup() * this.alpha);
            Draw.rect(this.liquidOutputRegion, build.x, build.y, build.totalProgress() * this.rotateSpeed + (this.rotate ? build.rotdeg() : 0.0F));
            Draw.reset();
            Draw.blend();
            Draw.z(z);
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {

        Draw.rect(liquidOutputRegion, plan.drawx(), plan.drawy(), rotation * 90);

    }

    @Override
    public void load(Block block) {
        liquidOutputRegion = Core.atlas.find(block.name + name);
        super.load(block);
    }
}
