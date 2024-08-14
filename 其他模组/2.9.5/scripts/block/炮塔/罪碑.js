const my = require("base/物品");
const lib = require("base/coflib");
const bullet = require("base/bullet");
const status = require("base/status");
const HX = require("base/Effect/hitFx");
const FX = require("base/Effect/fightFx");
let FF5845 = lib.FF5845, FF8663 = lib.FF8663, black = lib.Color("000000"), F03B0E = lib.Color("F03B0E");

let hole = new MultiEffect(
    Object.assign(new WaveEffect(), {
        lifetime: 30,
        sizeFrom: 0,
        sizeTo: 48,
        strokeFrom: 6,
        interp: Interp.circleOut,
        lightColor: FF5845,
        colorFrom: FF5845,
        colorTo: FF8663
    }),
    Object.assign(new ParticleEffect(), {
        particles: 1,
        lifetime: 48,
        sizeFrom: 18,
        length: 0,
        layer: 111,
        region: "curse-of-flesh-plasma",
        interp: Interp.swingIn,
        colorFrom: black,
        colorTo: black
    }),
    Object.assign(new ParticleEffect(), {
        particles: 1,
        lifetime: 48,
        sizeFrom: 20,
        length: 0,
        interp: Interp.swingIn,
        colorFrom: FF5845,
        colorTo: FF8663
    }),
    Object.assign(new ParticleEffect(), {
        line: true,
        particles: 18,
        lifetime: 24,
        length: 96,
        baseLength: -96,
        strokeFrom: 0,
        strokeTo: 0.8,
        lenFrom: 0,
        lenTo: 4,
        cone: 360,
        interp: Interp.swingIn,
        sizeInterp: Interp.exp10Out,
        colorFrom: FF5845,
        colorTo: FF8663
    })
)

let chargeHole = new Effect(360, e => {
    function s(to) {
        return Interp.elasticOut.apply(0, to, e.fin(Interp.slope)) * 2;
    }

    Draw.color(FF5845, FF8663, e.fin());
    Fill.circle(e.x, e.y, s(4.5));
    Draw.color(black);
    Draw.z(111);
    Draw.rect("curse-of-flesh-plasma", e.x, e.y, s(8), s(8));
    let rand = new Rand();
    rand.setSeed(e.id);
    for (let i = 0; i < 60; i++) {
        let fin = (rand.random(2) + Time.time / 60) % 1;
        let fout = 1 - fin;
        let angle = rand.random(360) + (Time.time / 0.5) % 360;
        Draw.alpha(0.6 * (1 - Mathf.curve(fin, 1 - 0.5)));
        let xy = lib.AngleTrns(angle, 160 * Interp.slope.apply(fout));
        Fill.circle(e.x + xy.x * e.fout(Interp.swing), e.y + xy.y * e.fout(Interp.swing), 4 * Interp.slope.apply(fin) * e.fin());
    }
})

let graviton = new Effect(15, e => {
    Draw.color(FF5845, FF8663, e.fin(Interp.pow10In));
    let rand = new Rand(), rv = new Vec2();
    Lines.stroke(Interp.exp10Out.apply(0, 4, e.fin()));
    let len = Interp.exp10Out.apply(0, 16, e.fin());
    rand.setSeed(e.id);
    for (let i = 0; i < 12; i++) {
        let l = 320 * e.fin(Interp.pow10In) - 320;
        rv.trns(e.rotation + rand.range(360), rand.random(l));
        let x = rv.x, y = rv.y;
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), len, true);
        Drawf.light(e.x + x, e.y + y, len * 2, FF8663, 0.6 * Draw.getColor().a);
    }
})

let blackhole = new Effect(300, e => {
    Draw.color(black);
    Draw.z(111);
    let f = e.fin(Interp.swingIn);

    function s(to) {
        return Interp.elasticOut.apply(0, to, e.fin(Interp.slope)) * 2;
    }

    Draw.rect("curse-of-flesh-plasma", e.x, e.y, s(160), s(160));
    Draw.blend(Blending.additive);
    Draw.color(FF5845, FF8663, e.fin());
    Draw.z(110);
    Fill.circle(e.x, e.y, s(64));
    for (let i of Mathf.signs) {
        Drawf.tri(e.x + (i * 40 * e.fout(f)), e.y, 40 * e.fout(f), 640 * i * e.fin(Interp.exp10Out), i * 180);
    }
    Lines.stroke(8 * e.fout(Interp.exp10Out));
    Lines.circle(e.x, e.y, 180 * e.fout(f));
    if (!Vars.state.isPaused()) graviton.at(e.x, e.y);
})

let b = Object.assign(bullet.RandomLightningBulletType(6000, 45 * 8, 40, 15, 8, hole), {
    damage: 14400,
    lifetime: 320,
    speed: 12,
    chargeEffect: chargeHole,
    shootEffect: FX.knellShoot.wrap(FF8663),
    smokeEffect: HX.hitExplosionMassive,
    collidesTiles: false,
    homingPower: 0.01,
    homingRange: 360,
    splashDamage: 8400,
    splashDamageRadius: 160,
    trailColor: FF8663,
    trailLength: 32,
    trailWidth: 8,
    trailSinScl: 5,
    trailSinMag: 0.5,
    trailInterp: Interp.pow10Out,
    hitShake: 480,
    hitEffect: blackhole,
    hitSound: Sounds.laserblast,
    fragBullets: 1,
    fragBullet: Object.assign(bullet.BlockHoleBulletType(600, 16, 12000, 0.5), {
        lifetime: 300,
        splashDamage: 7200,
        splashDamageRadius: 80
    })
})

b.parts.add(
    Object.assign(new ShapePart(), {
        circle: true,
        radius: 8,
        radiusTo: 32,
        color: black,
        layer: 111
    }),
    Object.assign(new ShapePart(), {
        circle: true,
        hollow: true,
        radius: 8,
        radiusTo: 36,
        stroke: 2,
        strokeTo: 9,
        color: FF8663,
        colorTo: FF8663,
        layer: 110
    })
)

let zb = lib.limitTurret(PowerTurret, "罪碑", 1);
Object.assign(zb, {
    health: 256000,
    armor: 24,
    size: 16,
    range: 3840,
    reload: 1800,
    shake: 12,
    shootY: 32,
    shootCone: 1,
    rotateSpeed: 0.5,
    recoil: 8,
    recoilTime: 600,
    cooldownTime: 810,
    shoot: Object.assign(new ShootPattern(), {
        firstShotDelay: 360
    }),
    liquidCapacity: 120,
    canOverdrive: false,
    moveWhileCharging: false,
    minWarmup: 0.99,
    shootWarmupSpeed: 0.008,
    warmupMaintainTime: 300,
    unitSort: UnitSorts.strongest,
    ammoUseEffect: FX.casing8Double,
    shootType: b
});
zb.consumePower(8750);
zb.consumeLiquid(Liquids.cryofluid, 60);
zb.setupRequirements(
    Category.turret,
    BuildVisibility.shown,
    ItemStack.with(
        Items.copper, 48000,
        Items.lead, 48000,
        Items.graphite, 36000,
        my.铱板, 24000,
        my.导能回路, 21000,
        my.陶钢, 18000,
        my.生物钢, 12000,
        my.肃正协议, 256
    )
);

lib.setBuilding(PowerTurret.PowerTurretBuild, zb, {
    draw() {
        this.super$draw();
        FX.PlayerAim(this, FF5845);
        /*if (this.warmup() <= 0.05) return
        Draw.color(FF8663);
        Draw.z(110);
        let rand = new Rand();
        rand.setSeed(this.id);
        for (let i = 0; i < 120; i++) {
            let fin = (rand.random(2) + (Time.time / 480)) % 1;
            let fout = 1 - fin;
            let angle = rand.random(360) + (Time.time / 0.6) % 360;
            let xy = lib.AngleTrns(angle, 160 * Interp.elasticIn.apply(fout)  + 32 * this.curRecoil);
            let xy2 = lib.AngleTrns(this.rotation, 32);
            Fill.circle(this.x + xy.x + xy2.x, this.y + xy.y + xy2.y, 4 * Interp.slope.apply(fin) * this.warmup());
            Drawf.light(this.x + xy.x + xy2.x, this.y + xy.y + xy2.y, 4 * Interp.slope.apply(fin) * this.warmup() * 8, FF8663, 0.6);
        }*/
    },
    onDestroyed() {
        this.super$onDestroyed();
        if (!this.floor().solid && !this.floor().isLiquid) {
            Effect.decal(lib.region("rubble-" + this.block.size), this.x, this.y, Math.round(Mathf.random(4)) * 90);
        }
    }
})

let pro = DrawPart.PartProgress;
zb.drawer = new DrawMulti(
    (() => {
        const d = new DrawTurret();
        for (let i = 0; i < 4; i++) {
            lib.DoubleHalo(d, {
                progress: pro.warmup.sin(i * 10, 20, 0.6),
                x: -100 + i * 15,
                y: -68 + i * 28,
                /*x: -55,
                y: 16,
                moveX: (3 - i) * 15,
                moveY: (3 - i) * 28,*/
                shapeMR: (3 - i) * 20,
                shapes: 1,
                radius: 0,
                radiusTo: (24 - i * 4) * 0.6,
                triL: 80 - i * 10,
                triLT: 180 - i * 20,
                haloRot: 20
            })
        }
        d.parts.addAll(
            Object.assign(new RegionPart("-barrel-l"), {
                heatProgress: pro.warmup,
                under: true,
                moveX: -5.75,
                moveY: -18.5,
                children: Seq.with(
                    Object.assign(new RegionPart("-top-l"), {
                        progress: pro.warmup.delay(0.8),
                        heatProgress: pro.warmup,
                        under: true,
                        moveY: 13.75,
                        layerOffset: -0.0001,
                        heatColor: F03B0E
                    })
                ),
                heatColor: F03B0E,
                turretHeatLayer: 50 - 0.0001
            }),
            Object.assign(new RegionPart("-barrel-r"), {
                heatProgress: pro.warmup,
                under: true,
                moveX: 5.75,
                moveY: -18.5,
                children: Seq.with(
                    Object.assign(new RegionPart("-top-r"), {
                        progress: pro.warmup.delay(0.8),
                        heatProgress: pro.warmup,
                        under: true,
                        moveY: 13.75,
                        layerOffset: -0.0001,
                        heatColor: F03B0E
                    })
                ),
                heatColor: F03B0E,
                turretHeatLayer: 50 - 0.0001
            }),
            Object.assign(new RegionPart("-shot"), {
                heatProgress: pro.warmup,
                under: true,
                moveY: 14.75,
                turretHeatLayer: 50 - 0.0001
            }),
            Object.assign(new RegionPart("-bot"), {
                under: true
            }),
            Object.assign(new RegionPart("-arrows"), {
                progress: pro.recoil,
                heatProgress: pro.warmup,
                under: true,
                y: 15,
                moveY: -15,
                heatColor: F03B0E
            }),
            Object.assign(new RegionPart("-part"), {
                heatProgress: pro.warmup,
                drawRegion: false,
                heatColor: F03B0E
            }),
            Object.assign(new RegionPart("-column1"), {
                progress: pro.warmup.delay(0.3),
                mirror: true,
                under: true,
                moveX: 2.75,
                moveY: -2.75
            }),
            Object.assign(new RegionPart("-column2"), {
                progress: pro.warmup.delay(0.6),
                mirror: true,
                under: true,
                moveX: 2.75,
                moveY: -2.75
            }),
            Object.assign(new RegionPart("-bottom"), {
                mirror: true,
                moveX: 13,
                moveY: -14
            }),
            Object.assign(new ShapePart(), {
                progress: pro.warmup.sin(15, 20, 0.6),
                hollow: true,
                circle: true,
                y: -80,
                radius: 16,
                stroke: 4,
                color: FF5845,
                colorTo: FF8663,
                layer: 110
            }),
            lib.Halo({
                progress: pro.warmup.sin(15, 20, 0.6),
                y: -80,
                mirror: false,
                shapes: 4,
                radius: 4,
                triL: 16
            }),
            lib.Halo({
                progress: pro.warmup.sin(15, 20, 0.6),
                y: -80,
                radius: 4.8,
                triL: 18,
                haloRS: 1,
                haloRad: 16
            })
        )
        lib.DoubleHalo(d, {
            progress: pro.warmup.sin(15, 20, 0.6),
            y: -80,
            radius: 3,
            triL: 24,
            haloRS: -1,
            haloRot: 90,
            haloRad: 32
        })
        return d;
    })(),
    Object.assign(new DrawPulseShape(false), {
        color: lib.Color("FF9C5A"),
        timeScl: 300,
        stroke: 4,
        minStroke: 0,
        layer: 49
    })
)