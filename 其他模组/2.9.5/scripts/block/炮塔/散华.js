const color = Items.surgeAlloy.color;
const lib = require("base/coflib");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");

let bu = Object.assign(bullets.TwiceHomingBulletType(160), {
    damage: 115,
    speed: 6,
    width: 16,
    height: 8,
    backColor: color,
    frontColor: color,
    trailWidth: 2,
    trailLength: 10,
    trailColor: color,
    despawnEffect: new Effect(60, e => {
        Draw.color(color);
        Lines.circle(e.x, e.y, 32 * e.fout(new Interp.PowOut(4)));
    })
});
let bB = Object.assign(bullets.FortressBulletType(240, 6, bu, 4, 0, 90), {
    lifetime: 600,
    speed: 8,
    width: 8,
    height: 8,
    backColor: color,
    frontColor: color,
    despawnEffect: new Effect(120, e => {
        Draw.color(color);
        Lines.circle(e.x, e.y, 32 * e.fout(new Interp.PowOut(4)));
    })
});

let sh = new PowerTurret("散华");
sh.reload = 720;
sh.consumePower(45);
sh.shootType = bB;
sh.range = 640;
sh.size = 4;
sh.recoil = 2;
sh.setupRequirements(
    Category.turret,
    BuildVisibility.sandboxOnly,
    ItemStack.with(
        Items.graphite, 120,
        Items.titanium, 90,
        Items.silicon, 60
    )
);