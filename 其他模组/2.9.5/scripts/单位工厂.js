const h = new UnitFactory("核心工厂");
h.buildType = prov(() => extend(UnitFactory.UnitFactoryBuild, h, {
    draw() {
        this.super$draw();
        Draw.z(50);
        let rand = new Rand();
        Drawf.liquid(Core.atlas.find("curse-of-flesh-" + this.name + "-liquid"), this.x, this.y, 1, Color.valueOf("5541B1"));
        Draw.color(Color.valueOf("474747"), Color.valueOf("7457CE"), this.fraction());
        rand.setSeed(this.pos());
        for (let i = 0; i < 5; i++) {
            let x = rand.range(3), y = rand.range(3);
            let life = 1 - ((Time.time / 90 + rand.random(6)) % 6);
            if (life > 0) {
                let warmup = this.progress > 0 ? 1 : 0;
                Lines.stroke(warmup * (life + 0));
                Lines.poly(this.x + x, this.y + y, 8, (1 - life) * 5);
            }
        }
        Draw.color();
    }
}))


/*package mindustry.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class DrawCultivator extends DrawBlock{
	public Color plantColor = Color.valueOf("5541b1");
	public Color plantColorLight = Color.valueOf("7457ce");
	public Color bottomColor = Color.valueOf("474747");

	public int bubbles = 12, sides = 8;
	public float strokeMin = 0.2f, spread = 3f, timeScl = 70f;
	public float recurrence = 6f, radius = 3f;

	public TextureRegion middle;

	@Override
	public void draw(Building build){
		Drawf.liquid(middle, build.x, build.y, build.warmup(), plantColor);

		Draw.color(bottomColor, plantColorLight, build.warmup());

		rand.setSeed(build.pos());
		for(int i = 0; i < bubbles; i++){
			float x = rand.range(spread), y = rand.range(spread);
			float life = 1f - ((Time.time / timeScl + rand.random(recurrence)) % recurrence);

			if(life > 0){
				Lines.stroke(build.warmup() * (life + strokeMin));
				Lines.poly(build.x + x, build.y + y, sides, (1f - life) * radius);
			}
		}

		Draw.color();
	}

	@Override
	public void load(Block block){
		middle = Core.atlas.find(block.name + "-middle");
	}
}*/