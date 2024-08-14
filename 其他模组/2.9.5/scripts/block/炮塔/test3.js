const color = Items.surgeAlloy.color;
const lib = require("base/coflib");
const bullets = require("base/bullet");

let b = Object.assign(bullets.StickyBombBulletType(8, 80, 120, 40), {
    backColor: color,
    frontColor: color,
    width: 8,
    height: 16,
    trailWidth: 2,
    trailLength: 4,
    trailColor: color,
    despawnEffect: Object.assign(ExplosionEffect(), {
        sparkColor: lib.Color("F6E096"),
        lifetime: 30,
        smokes: 30,
        smokeSize: 13,
        smokeSizeBase: 0.6,
        smokeRad: 32,
        waveLife: 30,
        waveStroke: 2,
        waveRad: 73,
        waveRadBase: 2,
        sparkRad: 64,
        sparkLen: 13,
        sparkStroke: 4,
        sparks: 40
    }),
    fragBullets: 13,
    fragBullet: Object.assign(FireBulletType(5, 13), {
        lifetime: 60
    })
});

let SUT = lib.SpeedUp2Turret(PowerTurret, "SUT2", {
    size: 4,
    recoil: 2,
    reload: 30,
    range: 360,
    shootCone: 5,
    shootType: b
}, 10, 1, 300);
SUT.consumePower(45);
SUT.setupRequirements(
    Category.turret,
    BuildVisibility.sandboxOnly,
    ItemStack.with(
        Items.graphite, 120,
        Items.titanium, 90,
        Items.silicon, 60
    )
);