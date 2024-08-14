const my = require("base/物品");
const lib = require("base/coflib");
const status = require("base/status");
const FX = require("base/Effect/fightFx");
let FF5845 = lib.FF5845, F03B0E = lib.Color("F03B0E");

function dm(size) {
    return new MultiEffect(Fx.dynamicSpikes.wrap(Pal.redLight, size), Fx.mineImpactWave.wrap(Pal.redLight, size * 1.5))
}

let bu = Object.assign(extend(PointBulletType, {}), {
    lifetime: 6,
    speed: 160,
    ammoMultiplier: 1,
    status: status.蚀骨,
    statusDuration: 60,
    splashDamage: 14400,
    splashDamageRadius: 128,
    shootEffect: new MultiEffect(
        dm(30),
        FX.railShoot
    ),
    hitColor: Pal.redLight,
    smokeEffect: Fx.mineImpact.wrap(Pal.redLight),
    trailSpacing: 24,
    trailEffect: Object.assign(new ParticleEffect(), {
        region: "curse-of-flesh-aim",
        baseRotation: -90,
        particles: 1,
        length: 8,
        lifetime: 45,
        sizeFrom: 40,
        cone: 0,
        colorFrom: Pal.lightOrange,
        colorTo: Pal.redLight
    }),
    hitEffect: dm(120),
    fragBullets: 8,
    fragVelocityMin: 0.5,
    fragBullet: Object.assign(extend(PointBulletType, {}), {
        lifetime: 24,
        speed: 8,
        status: status.熔融,
        statusDuration: 45,
        splashDamage: 7200,
        splashDamageRadius: 64,
        trailEffect: Fx.disperseTrail,
        despawnShake: 4,
        despawnSound: Sounds.plasmaboom,
        despawnEffect: dm(60),
        fragBullets: 4,
        hitEffect: Fx.titanExplosion.wrap(Pal.redLight),
        fragBullet: Object.assign(extend(PointBulletType, {}), {
            lifetime: 12,
            speed: 8,
            status: StatusEffects.melting,
            statusDuration: 30,
            splashDamage: 3600,
            splashDamageRadius: 32,
            trailEffect: Fx.disperseTrail,
            despawnShake: 4,
            despawnSound: Sounds.plasmaboom,
            despawnEffect: dm(30)
        })
    })
})

let qs = lib.limitTurret(PowerTurret, "清算", 1);
Object.assign(qs, {
    health: 256000,
    armor: 24,
    size: 16,
    range: 3840,
    reload: 60,
    shake: 8,
    shootY: 12,
    shootCone: 1,
    rotateSpeed: 0.5,
    recoil: 8,
    recoilTime: 90,
    cooldownTime: 120,
    canOverdrive: false,
    minWarmup: 0.99,
    liquidCapacity: 60,
    shootWarmupSpeed: 0.016,
    warmupMaintainTime: 300,
    //unitFilter: u => u instanceof LegsUnit,
    unitSort: UnitSorts.strongest,
    ammoUseEffect: FX.casing8Double,
    shootSound: Sounds.malignShoot,
    shootType: bu
});
qs.consumePower(8750);
qs.consumeLiquid(Liquids.cryofluid, 30);
qs.setupRequirements(
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

lib.setBuilding(PowerTurret.PowerTurretBuild, qs, {
    draw() {
        this.super$draw();
        FX.PlayerAim(this, FF5845);
    },
    onDestroyed() {
        this.super$onDestroyed();
        if (!this.floor().solid && !this.floor().isLiquid) {
            Effect.decal(lib.region("rubble-" + this.block.size), this.x, this.y, Math.round(Mathf.random(4)) * 90);
        }
    }
})

let pro = DrawPart.PartProgress;
qs.drawer = (() => {
    const d = new DrawTurret();
    for (let i = 0; i < 5; i++) {
        d.parts.add(
            Object.assign(new RegionPart("-glow"), {
                progress: pro.warmup.delay(i / 8),
                heatProgress: pro.warmup.sin((4 - i) * 10, 10, 1),
                under: true,
                mirror: true,
                moveX: -6,
                moveY: 11 * i,
                layerOffset: -0.3,
                heatColor: F03B0E,
                color: lib.Color("FF9C5A"),
                turretHeatLayer: 50 - 0.2,
                moves: Seq.with(new DrawPart.PartMove(pro.recoil.delay((4 - i) / 8), 0, 0, -15))
            })
        )
    }
    for (let i = 0; i < 8; i++) {
        lib.DoubleHalo(d, {
            progress: pro.warmup.sin(i * 10, 20, 0.6), //频率/速度/进度
            x: 34 + i * 2,
            y: 43 - i * 8,
            shapes: 1,
            radius: 3.5 + i * 0.2,
            triL: 3,
            triLT: 8 + 2 * i,
            haloRot: -45,
            color: FF5845,
            colorTo: Pal.redLight
        })
    }
    d.parts.add(
        Object.assign(new RegionPart("-side-l"), {
            heatProgress: pro.warmup,
            under: true,
            moveX: -1.75,
            moveY: 1,
            children: Seq.with(
                Object.assign(new RegionPart("-top-l"), {
                    heatProgress: pro.warmup,
                    under: true,
                    moveY: 32.5,
                    layerOffset: -0.0001,
                    turretHeatLayer: 50 - 0.00001
                }),
                Object.assign(new RegionPart("-sidep-l"), {
                    under: true,
                    drawRegion: false,
                    heatColor: F03B0E
                })
            ),
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-side-r"), {
            heatProgress: pro.warmup,
            under: true,
            moveX: 1.75,
            moveY: 1,
            children: Seq.with(
                Object.assign(new RegionPart("-top-r"), {
                    heatProgress: pro.warmup,
                    under: true,
                    moveY: 32.5,
                    layerOffset: -0.0001,
                    turretHeatLayer: 50 - 0.00001
                }),
                Object.assign(new RegionPart("-sidep-r"), {
                    under: true,
                    drawRegion: false,
                    heatColor: F03B0E
                })
            ),
            heatColor: F03B0E
        })
    )
    return d;
})();