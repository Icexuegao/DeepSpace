const color = Items.surgeAlloy.color;
const lib = require("base/coflib");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");

let b = Object.assign(bullets.HomingBulletType(8, 45, 120, 4), {
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
    /*bulletInterval: 15,
    intervalBullets: 4,
    intervalRandomSpread: 30,
    intervalBullet: Object.assign(new BasicBulletType(8, 45), {
        lifetime: 25,
        width: 8,
        height: 4,
        trailWidth: 1,
        trailLength: 5,
        trailColor: color,
        despawnEffect: Fx.flakExplosion
    })*/
});

let DUT = lib.DamageUpTurret(PowerTurret, "DUT", {
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