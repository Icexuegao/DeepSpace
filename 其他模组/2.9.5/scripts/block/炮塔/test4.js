const color = Items.surgeAlloy.color;
const lib = require("base/coflib");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");

let b = Object.assign(bullets.HomingMainBulletType(13, 550, 40, 6, true), {
    backColor: color,
    frontColor: color,
    width: 8,
    height: 16,
    pierceCap: 2,
    pierceBuilding: true,
    trailWidth: 2,
    trailLength: 10,
    trailColor: color,
    hitEffect: new Effect(30, e => {
        let RGB = new Color(Time.time % 1, Time.time * 2 % 1, Time.time * 3 % 1, 1);
        RGB.fromHsv((Time.time * 3) % 360, 1, 1);
        Draw.color(Color.white, RGB, e.fin());
        Lines.stroke(e.fout() * 1.3 + 0.7);
        Lines.circle(e.x, e.y, 32 * e.fout(new Interp.PowOut(4)));
    }),
    fragAngle: 45,
    fragBullets: 3,
    fragSpread: 15,
    fragBullet: Object.assign(new BasicBulletType(5, 50), {
        lifetime: 88,
        drag: -0.01,
        trailWidth: 2,
        trailLength: 10,
        trailColor: color,
    })
});

let DUT = lib.DamageUpTurret(PowerTurret, "DUT2", {
    size: 4,
    recoil: 2,
    reload: 30,
    range: 360,
    shootCone: 5,
    shootType: b
}, 1500, 1, 50);
DUT.consumePower(45);
DUT.setupRequirements(
    Category.turret,
    BuildVisibility.sandboxOnly,
    ItemStack.with(
        Items.graphite, 120,
        Items.titanium, 90,
        Items.silicon, 60
    )
);