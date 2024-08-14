const lib = require("base/coflib");
let b = Object.assign(extend(PointBulletType, {}), {
    damage: 35,
    lifetime: 8,
    speed: 20,
    ammoMultiplier: 1,
    shootEffect: new Effect(20, e => {
        let RGB = new Color(1, 1, 1, 1);
        RGB.fromHsv((Time.time * 3) % 360, 1, 1);
        Draw.color(Color.white, RGB, e.fin());
        Lines.stroke(e.fout() * 1.3 + 0.7);
        Angles.randLenVectors(e.id, 8, 41 * e.fin(), e.rotation, 10, (x, y) => {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6 + 0.5);
        });
    }),
    trailSpacing: 24,
    trailEffect: new Effect(20, e => {
        let RGB = new Color(1, 1, 1, 1);
        RGB.fromHsv((Time.time * 3) % 360, 1, 1);
        Draw.color(Color.white, RGB, e.fin());
        Lines.stroke(e.fout() * 1.3 + 0.7);
        Lines.lineAngle(e.x, e.y, e.rotation, 28);
    }),
    hitEffect: Fx.dynamicSpikes.wrap(Pal.redLight, 8),
    despawnEffect: Fx.dynamicSpikes.wrap(Pal.redLight, 8)
})

let nh = lib.SpeedUpTurret(PowerTurret, "霓虹", {
    health: 820,
    size: 2,
    range: 160,
    reload: 30,
    shootType: b,
    recoil: 2,
    shootY: 2,
    shootCone: 15,
    rotateSpeed: 6,
    recoilTime: 30,
    cooldownTime: 30,
    canOverdrive: false,
    shootSound: Sounds.bolt
}, 6, 120);
nh.consumePower(7);
nh.setupRequirements(
    Category.turret,
    BuildVisibility.shown,
    ItemStack.with(
        Items.copper, 130,
        Items.lead, 85,
        Items.silicon, 45
    )
);