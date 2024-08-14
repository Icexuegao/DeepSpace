const lib = require("base/coflib");
const status = require("base/status");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");
const NX = require("base/Effect/normalFx");
let FF5845 = lib.FF5845, FF8663 = lib.FF8663;

let 解脱 = new PowerTurret("解脱");
Object.assign(解脱, {
    size: 9,
    range: 960,
    shoot: Object.assign(new ShootPattern(), {
        firstShotDelay: 120
    }),
    shootType: Object.assign(extend(PointBulletType, {}), {
        damage: 0,
        lifetime: 1,
        speed: 960,
        status: status.蚀骨,
        statusDuration: 30,
        splashDamage: 480,
        splashDamageRadius: 240,
        trailSpacing: 26,
        trailEffect: Object.assign(new ParticleEffect(), {
            region: "curse-of-flesh-crystal",
            baseRotation: -90,
            particles: 1,
            length: 1,
            baseLength: 1,
            lifetime: 45,
            sizeFrom: 16,
            sizeTo: 0,
            colorFrom: FF5845,
            colorTo: FF8663,
            cone: 0
        }),
        fragBullets: 1,
        fragBullet: Object.assign(bullets.BlockHoleBulletType(320, 6, 500, 0.05), {
            lifetime: 600,
            spin: 2,
            width: 32,
            height: 32,
            frontColor: FF8663,
            backColor: FF5845,
            status: status.蚀骨,
            statusDuration: 300,
            splashDamage: 480,
            splashDamageRadius: 80,
            sprite: "curse-of-flesh-stardart",
            despawnShake: 8,
            despawnSound: Sounds.plasmaboom,
            despawnEffect: new MultiEffect(
                Fx.dynamicSpikes.wrap(FF5845, 75),
                Fx.mineImpactWave.wrap(FF8663, 112.5)
            )
        })
    })
});
let aimShoot = FX.aimShoot(Pal.accent, 120, 解脱.range, 1, 14);
lib.setBuilding(PowerTurret.PowerTurretBuild, 解脱, {
    shoot(type) {
        let vec = new Vec2();
        vec.trns(this.rotation, 解脱.size * 8 / 2);
        let length = Math.hypot(this.x - this.targetPos.x, this.y - this.targetPos.y);
        aimShoot.at(this.x + vec.x, this.y + vec.y, this.rotation, {
            length: Math.min(length, 解脱.range)
        });
        this.super$shoot(type);
    },
    draw() {
        this.super$draw();
        FX.PlayerAim(this, FF5845);
    }
})

解脱.drawer = (() => {
    const d = new DrawTurret();
    d.parts.add(
        Object.assign(new RegionPart("-body"), {
            heatProgress: DrawPart.PartProgress.warmup,
            mirror: true,
            moveX: 4,
            moveY: -0.75,
            layerOffset: 0.0001,
            children: Seq.with(
                Object.assign(new RegionPart("-side"), {
                    heatProgress: DrawPart.PartProgress.warmup,
                    under: true,
                    mirror: true,
                    moveX: 3.75,
                    moveY: -2.75
                }),
                Object.assign(new RegionPart("-track"), {
                    under: true,
                    mirror: true,
                    layerOffset: 0.0001
                }),
                Object.assign(new RegionPart("-top1"), {
                    under: true,
                    mirror: true,
                    moveY: 15,
                    layerOffset: 0.0001,
                    children: Seq.with(
                        Object.assign(new RegionPart("-top2"), {
                            under: true,
                            mirror: true,
                            y: -0.25,
                            moveY: 5,
                            layerOffset: 0.0001
                        })
                    )
                })
            ),
            heatColor: lib.Color("F03B0E")
        })
    )
    NX.DotCircle(d, 32);
    return d;
})();