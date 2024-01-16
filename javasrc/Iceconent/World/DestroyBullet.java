package Iceconent.World;

import arc.graphics.Color;
import arc.math.Interp;
import mindustry.Vars;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Sounds;

public class DestroyBullet {
    public static BasicBulletType basicBulletType1;

    public static void load() {
        basicBulletType1 = new BasicBulletType(0, 1300) {{
            collides = false;
            absorbable = false;
            sprite = "circle";
            lifetime = 120f;
            width = 0;
            height = 0;
            hitSound = Sounds.explosionbig;
            hitSoundVolume = 2f;
            splashDamageRadius = 80f;
            splashDamage = 985;
            status = Vars.content.statusEffect("S-GR");
            statusDuration = 360;
            bulletInterval = 5;
            intervalBullets = 12;
            intervalSpread = 10;
            hitEffect = new MultiEffect(new ParticleEffect() {{
                particles = 1;
                sizeFrom = 0;
                sizeTo = 80;
                length = 0;
                baseLength = 0;
                lifetime = 30;
                sizeInterp = Interp.pow10Out;
                colorFrom = Color.valueOf("6586 B0");
                colorTo = Color.valueOf("C0ECFF");
            }}, new ParticleEffect() {{
                particles = 21;
                line = true;
                strokeFrom = 7;
                strokeTo = 5;
                lenFrom = 66;
                lenTo = 0;
                sizeInterp = Interp.pow3In;
                interp = interp.pow10Out;
                length = 83;
                baseLength = 20;
                lifetime = 60;
                colorFrom = Color.valueOf("C0ECFF");
                colorTo = Color.valueOf("6586B0");
            }}, new ParticleEffect() {{
                particles = 1;
                startDelay = 28;
                sizeFrom = 80;
                sizeTo = 0;
                length = 0;
                baseLength = 0;
                lifetime = 70;
                sizeInterp = Interp.pow5In;
                colorFrom = Color.valueOf("C0ECFF");
                colorTo = Color.valueOf("6586B0");
            }}, new WaveEffect() {{
                lifetime = 100;
                sizeFrom = 80;
                sizeTo = 80;
                strokeFrom = 6;
                strokeTo = 0;
                colorFrom = Color.valueOf("C0ECFF");
                colorTo = Color.valueOf("6586B0");
            }});
            intervalBullet = new BasicBulletType(8, 100) {{
                lifetime = 40f;
                hitSound = Sounds.plasmaboom;
                width = 0;
                height = 0;
                splashDamageRadius = 24;
                splashDamage = 85;
                status = Vars.content.statusEffect("S-GR");
                statusDuration = 180;
                trailChance = 0.1f;
                trailInterval = 16;
                trailEffect = new ParticleEffect() {{
                    particles = 1;
                    length = 6;
                    baseLength = 1;
                    lifetime = 20;
                    colorFrom = Color.valueOf("C0ECFF");
                    colorTo = Color.valueOf("6586B0");
                    sizeFrom = 2;
                    sizeTo = 0;
                }};
                hitEffect = new MultiEffect(new ParticleEffect() {{
                    particles = 1;
                    sizeFrom = 6;
                    sizeTo = 24;
                    length = 0;
                    baseLength = 0;
                    lifetime = 15;
                    sizeInterp = interp.pow10Out;
                    colorFrom = Color.valueOf("6586B0");
                    colorTo = Color.valueOf("C0ECFF");
                }}, new ParticleEffect() {{
                    particles = 1;
                    startDelay = 14;
                    sizeFrom = 24;
                    sizeTo = 0;
                    length = 0;
                    baseLength = 0;
                    lifetime = 20;
                    sizeInterp = interp.pow5In;
                    colorFrom = Color.valueOf("C0ECFF");
                    colorTo = Color.valueOf("6586B0");
                }}, new ParticleEffect() {{
                    particles = 9;
                    sizeFrom = 3;
                    sizeTo = 0;
                    length = 80;
                    baseLength = 10;
                    lifetime = 15;
                    colorFrom = Color.valueOf("C0ECFF");
                    colorTo = Color.valueOf("6586B0");
                }}, new WaveEffect() {{
                    lifetime = 15;
                    sizeFrom = 0;
                    sizeTo = 32;
                    strokeFrom = 6;
                    strokeTo = 0;
                    colorFrom = Color.valueOf("C0ECFF");
                    colorTo = Color.valueOf("6586B0");
                }});
            }};
        }};
    }
}
