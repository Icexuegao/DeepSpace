const my = require("base/物品");
const lib = require("base/coflib");
const {斩杀} = require("base/status");
const FX = require("base/Effect/fightFx");
let FF5845 = lib.FF5845, FF8663 = lib.FF8663, F03B0E = lib.Color("F03B0E");
let minusMaxHealth = Stat("minusMaxHealth", StatCat.function);

let cj = extend(PowerTurret, "处决", {
    setStats() {
        this.super$setStats();
        this.stats.add(minusMaxHealth, lib.bundle("minusMaxHealth", 5));
    },
    canPlaceOn(tile, team, rotation) {
        if (lib.mapLimit()) return true;
        return Vars.state.teams.get(team).getCount(this) < 4;
    },
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        if (lib.mapLimit()) return
        if (Vars.state.teams.get(Vars.player.team()).getCount(this) < 4) return
        this.drawPlaceText(lib.limitBuild(this, 4), x, y, valid);
    }
});
cj.shootType = extend(BasicBulletType, {
    hitEntity(b, entity, health) {
        this.super$hitEntity(b, entity, health);
        let u = entity;
        u.maxHealth -= u.type.health * 0.05;
        if (u.health > u.maxHealth) health = u.maxHealth;
    },
    update(b) {
        this.super$update(b);
        if (b.time >= 1) return
        let e = b.owner;
        FX.chainLightningFade2.at(e.x, e.y, Mathf.random(16, 24), FF8663, b);
    },
    sprite: "curse-of-flesh-bullet",
    damage: 1,
    lifetime: 27,
    speed: 32,
    width: 12,
    height: 17,
    shrinkY: 0,
    hittable: false,
    absorbable: false,
    reflectable: false,
    status: 斩杀,
    statusDuration: 300,
    ammoMultiplier: 1,
    pierce: true,
    pierceArmor: true,
    pierceBuilding: true,
    hitColor: FF8663,
    frontColor: FF8663,
    backColor: FF5845,
    hitSound: Sounds.shockBlast,
    shootEffect: FX.shockShootLarge,
    trailLength: 4,
    trailWidth: 2,
    trailInterval: 2,
    tarilColor: FF8663,
    trailRotation: true,
    trailEffect: FX.shockShoot
})

let pro = DrawPart.PartProgress;
cj.drawer = (() => {
    const d = new DrawTurret();
    for (let i = 0; i < 4; i++) {
        d.parts.add(Object.assign(new RegionPart("-shot"), {
            heatProgress: pro.warmup.sin((3 - i) * Math.PI * 4.5, Math.PI * 4.5, 1),
            y: 6 * i,
            mirror: false,
            drawRegion: false,
            heatColor: F03B0E
        }))
    }
    d.parts.add(
        Object.assign(new RegionPart("-glow"), {
            heatProgress: pro.warmup,
            drawRegion: false,
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-side"), {
            heatProgress: pro.warmup,
            x: 5.75,
            y: -24.25,
            moveX: 4,
            moveY: 1,
            mirror: true,
            heatColor: F03B0E,
            children: Seq.with(Object.assign(new RegionPart("-side"), {
                heatProgress: pro.warmup,
                moveX: -8,
                moveY: -4,
                moveRot: -25,
                under: true,
                mirror: true,
                heatColor: F03B0E
            }))
        })
    )
    for (let i = 0; i < 4; i++) {
        d.parts.add(
            Object.assign(new RegionPart("-aim"), {
                heatProgress: pro.warmup.sin((4 - i) * Math.PI * 4.5, Math.PI * 4.5, 1),
                heatLight: true,
                drawRegion: false,
                y: (i + 1) * 20 / (1 - i / 20) + 9,
                xScl: 1 - i * 0.13,
                yScl: 1 - i * 0.13,
                heatColor: F03B0E
            })
        )
    }
    d.parts.add(
        Object.assign(new ShapePart(), {
            progress: pro.warmup.delay(0.2),
            y: -28,
            circle: true,
            hollow: true,
            stroke: 0,
            strokeTo: 1.2,
            radius: 7.5,
            color: FF5845,
            colorTo: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            progress: pro.warmup.delay(0.8),
            y: -28,
            sides: 4,
            hollow: true,
            stroke: 0,
            strokeTo: 0.9,
            radius: 3.6,
            rotateSpeed: 0.5,
            color: FF5845,
            colorTo: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            progress: pro.warmup.delay(0.6),
            y: -28,
            sides: 4,
            hollow: true,
            stroke: 0,
            strokeTo: 0.9,
            radius: 6.3,
            rotateSpeed: -0.5,
            color: FF5845,
            colorTo: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            progress: pro.warmup.delay(0.1),
            y: -28,
            moveY: -4.5,
            circle: true,
            hollow: true,
            stroke: 0,
            strokeTo: 1.5,
            radius: 12,
            color: FF5845,
            colorTo: FF8663,
            layer: 110
        })
    )
    lib.DoubleHalo(d, {
        progress: pro.warmup.delay(0.3),
        haloRot: -90,
        x: 16,
        y: -28,
        shapes: 1,
        radius: 0,
        radiusTo: 4.5,
        triL: 36
    })
    lib.DoubleHalo(d, {
        progress: pro.warmup.delay(0.4),
        x: 12,
        y: -34,
        shapes: 1,
        radius: 0,
        radiusTo: 3,
        triL: 27,
        haloRot: -120
    })
    lib.DoubleHalo(d, {
        progress: pro.warmup.delay(0.5),
        x: 6,
        y: -40,
        shapes: 1,
        radius: 0,
        radiusTo: 3,
        triL: 27,
        haloRot: -150
    })
    lib.DoubleHalo(d, {
        progress: pro.warmup.delay(0.6),
        haloRot: 180,
        mirror: false,
        y: -44,
        shapes: 1,
        radius: 0,
        radiusTo: 4.5,
        triL: 36
    })
    return d;
})();