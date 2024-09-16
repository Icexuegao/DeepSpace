package ice.Alon.asundry.world.bullet;

import arc.graphics.Color;
import ice.Alon.asundry.Content.Ix;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Bullet;

public class IceMissileBulletTypes {

    public static MissileBulletType lj, lj1;

    static {
        lj = new MissileBulletType(8, 50, "missile") {
            private int g = 0;

            {
                trailParam = 5;/** 传递给 Trail 的旋转/大小参数。通常，这控制大小。*/
                trailLength = 16;/** 轨道长度。   */
                trailWidth = 1.3f;/** 轨道宽度。   */
                hitEffect = despawnEffect = new MultiEffect(Ix.tarnationCharge, Ix.tarnationLines) ;
                lifetime = 75;
                homingPower = 0f;/**  追踪力度 */
                homingRange = 0f;/**  追踪范围 */
                width = 7;
                height = 24;
                weaveScale = 7;/** 控制轨道振动幅度  */
                weaveMag = -1.5f;/** 控制轨道振动频率 */
                splashDamageRadius = 24;/** 溅射伤害半径。   */
                splashDamage = 30;/** 溅射伤害  */
                trailEffect = Fx.none;/** 轨道特效。   */
                trailSinMag = 1;/** 轨道变大变小参数  */
                ammoMultiplier = 2f;
                shootEffect = Fx.hitLancer;
            /*    backColor = Color.valueOf("cdf4ff");
                frontColor = Color.valueOf("cdf4ff");
                trailColor = Color.valueOf("cdf4ff");*/
                backColor = Color.valueOf("ff7171");
                frontColor = Color.valueOf("ff7171");
                trailColor = Color.valueOf("ff7171");
                fragBullet =null;
              /*  fragBullet = new LightningBulletType() {{
                    lightning = 2;
                    lifetime = 45;
                    hitEffect = despawnEffect = Fx.none;
                    lightningLength = 4;
                    lightningColor = Color.valueOf("ff7171");
                }};*/
            }

            @Override
            public void update(Bullet b) {
                if (g == 180) {
                    if (Vars.world.tileWorld(b.x, b.y) != null && Vars.world.tileWorld(b.x, b.y).block() == Blocks.air) {
                        Vars.world.tileWorld(b.x, b.y).setBlock(Blocks.shockMine, b.team);
                        g = 0;
                    }
                } else {
                    g++;
                }
                super.update(b);
            }
        };
        lj1 = new MissileBulletType(34, 50, "missile") {
            private int g = 0;
            {
                trailParam = 5;/** 传递给 Trail 的旋转/大小参数。通常，这控制大小。*/
                trailLength = 16;/** 轨道长度。   */
                trailWidth = 1.3f;/** 轨道宽度。   */
                hitEffect = despawnEffect = Ix.hj1;
                lifetime = 75;
                homingPower = 0f;/**  追踪力度 */
                homingRange = 0f;/**  追踪范围 */
                width = 7;
                height = 24;
                weaveScale = 7;/** 控制轨道振动幅度  */
                weaveMag = -1.5f;/** 控制轨道振动频率 */
                splashDamageRadius = 24;/** 溅射伤害半径。   */
                splashDamage = 30;/** 溅射伤害  */
                trailEffect = Fx.none;/** 轨道特效。   */
                trailSinMag = 1;/** 轨道变大变小参数  */
                ammoMultiplier = 2f;
                shootEffect = Ix.hjs;
            /*    backColor = Color.valueOf("cdf4ff");
                frontColor = Color.valueOf("cdf4ff");
                trailColor = Color.valueOf("cdf4ff");*/
                backColor = Color.valueOf("ff7171");
                frontColor = Color.valueOf("ff7171");
                trailColor = Color.valueOf("ff7171");
              /*  fragBullet = new LightningBulletType() {{
                    lifetime = 45;
                    hitEffect = despawnEffect = Fx.none;
                    lightningLength = 4;
                    lightningColor = Color.valueOf("ff7171");
                }};*/
            }

            @Override
            public void update(Bullet b) {
                if (g == 180) {
                    if (Vars.world.tileWorld(b.x, b.y) != null && Vars.world.tileWorld(b.x, b.y).block() == Blocks.air) {
                        Vars.world.tileWorld(b.x, b.y).setBlock(Blocks.shockMine, b.team);
                        g = 0;
                    }
                } else {
                    g++;
                }
                super.update(b);
            }
        };
    }
}
