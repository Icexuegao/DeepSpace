const lib = require("base/coflib");
const my = require("base/物品");
const ability = require("base/ability");
const status = require("base/status");
const FX = require("base/Effect/fightFx");

function newUnit(name, unitType, cons) {
    const u = extend(UnitType, name, cons || {});
    u.constructor = () => new unitType.create();
    return exports[name] = u;
}

exports.newUnit = newUnit;

function specialUnit(name, unitType, cons, cons2) {
    const u = extend(UnitType, name, cons || {});
    let id;
    u.constructor = () => extend(unitType, cons2 || {
        classId() {
            return id
        }
    });
    id = EntityMapping.register(lib.modName + name, u.constructor);
    return exports[name] = u;
}

exports.specialUnit = specialUnit;

function newUnitList(name, unitType, num, cons) {
    for (let i = 1; i <= num; i++) newUnit(name + i, unitType, cons);
}

function immunities(u) {
    u.immunities.addAll(StatusEffects.burning, StatusEffects.melting, StatusEffects.blasted, StatusEffects.wet, StatusEffects.freezing, StatusEffects.sporeSlowed, StatusEffects.slow, StatusEffects.tarred, StatusEffects.muddy, StatusEffects.sapped, StatusEffects.electrified, StatusEffects.unmoving, status.EMP, status.封冻, status.熔融, status.蚀骨, status.寄生, status.湍能, status.损毁, status.破甲, status.辐射, status.衰变);
}

exports.immunities = immunities;

/*flying -> UnitEntity;
mech -> MechUnit;
legs -> LegsUnit;
naval -> UnitWaterMove;
payload -> PayloadUnit;
missile -> TimedKillUnit;
tank -> TankUnit;
hover -> ElevationMoveUnit;
tether -> BuildingTetherPayloadUnit;
crawl -> CrawlUnit;*/

function BottleUnitType(name, lifeTime, damage, range, status, effect, color) {
    let i = 0, dx = 0, dy = 0;
    let u = extend(MissileUnitType, name, {
        health: 10000,
        lifetime: lifeTime,
        speed: 0,
        trailLength: 0,
        flying: false,
        drawCell: false,
        hittable: false,
        targetable: false,
        loopSound: Sounds.none,
        update(unit) {
            this.super$update(unit);
            if ((i += Time.delta) >= 6) {
                Damage.damage(unit.x, unit.y, range, damage);
                Damage.status(null, unit.x, unit.y, range, status, 900, true, true);
                dx = unit.x + Mathf.range(range * 0.6);
                dy = unit.y + Mathf.range(range * 0.7);
                if (Mathf.chance(0.5)) effect.at(dx, dy, color);
                i = 0;
            }
        },
        draw(unit) {
            this.super$draw(unit);
            Draw.color(Pal.accent);
            Draw.z(Layer.effect);
            for (let i = 0; i < 4; i++) {
                let r = Time.time + i * 90;
                Lines.arc(unit.x, unit.y, range, 0.2, r);
            }
        }
    });
    u.immunities.add(status);
    return exports[name] = u;
}

Object.assign(new UnitType("毒刺"), {
    flying: true,
    physics: false,
    lowAltitude: true,
    createScorch: false,
    logicControllable: false,
    playerControllable: false,
    constructor: () => new UnitEntity.create(),
    aiController: () => extend(MissileAI, {
        updateMovement() {
            if (this.shooter && this.target) {
                this.circleAttack(150);
            } else if (this.shooter) {
                this.circle(this.shooter, this.shooter.type.hitSize * 2);
            } else if (!this.shooter) {
                this.unit.kill();
            }
        },
        retarget() {
            return this.timer.get(this.timerTarget, 45);
        }
    })
});
newUnit("伊普西龙", PayloadUnit);
newUnit("泽塔", PayloadUnit);
newUnit("欧米茄", UnitEntity, {
    draw(unit) {
        this.super$draw(unit);
        FX.PlayerAim(unit, lib.FF5845);
    }
});
let 雨燕 = newUnit("雨燕", UnitEntity);
雨燕.abilities.add(ability.RotatorAbility("副螺旋桨", 32, -5, 2, 30, true), ability.RotatorAbility("主螺旋桨", 0, 12, 2, 64));
newUnitList("陆战", MechUnit, 6);
newUnitList("空雷", UnitEntity, 5);
newUnitList("空炸", UnitEntity, 5);
newUnit("空炸6", PayloadUnit);
require("unit/崔文甘");
require("unit/罗织构陷");
newUnit("冥", UnitEntity);
newUnit("玄", UnitEntity);
let ww = newUnit("无畏", UnitEntity);
ww.abilities.add(Object.assign(new EnergyFieldAbility(0, 60, 0), {
    y: -31.75,
    display: false,
    maxTargets: 0,
    healPercent: 0,
    color: lib.FF5845
}), new ShieldRegenFieldAbility(400, 12000, 60, 240));

let 渊狱 = newUnit("渊狱", UnitEntity);

let RA = Object.assign(new RegenAbility(), {
    percentAmount: 1 / 100
});
let SFA = Object.assign(new StatusFieldAbility(status.屠戮, 60, 30, 200), {
    activeEffect: Fx.none
});
let LEA = Object.assign(new LiquidExplodeAbility(), {
    liquid: my.血水,
    noiseMag: 8,
});
渊狱.abilities.add(RA, SFA, LEA);

let 黑棘 = newUnit("黑棘", UnitEntity);

let hjSFA = SFA.copy();
hjSFA.duration = 600;

let hjLEA = LEA.copy();
hjLEA.radAmountScale = 4;

let hjEFA = Object.assign(new EnergyFieldAbility(225, 30, 240), {
    y: 5.75,
    maxTargets: 20,
    status: status.熔融,
    statusDuration: 120,
    sectors: 4,
    sectorRad: 0.18,
    color: lib.FF5845,
    effectRadius: 4,
    rotateSpeed: 0.5
});
黑棘.abilities.add(ability.HealthRequireAbility(0.25, StatusEffects.none, status.迅疗));
黑棘.abilities.addAll(RA, hjSFA, hjEFA, hjLEA);

let 噬星 = newUnit("噬星", UnitEntity, {
    draw(unit) {
        this.super$draw(unit);
        FX.PlayerAim(unit, lib.FF5845);
    }
});

let sxEFA = hjEFA.copy();
sxEFA.y = 10.75;
sxEFA.range = 320;
sxEFA.damage = 445;
sxEFA.maxTargets = 40;
sxEFA.sectors = 3;
sxEFA.sectorRad = 0.2;
噬星.abilities.add(ability.HealthRequireAbility(0.4, StatusEffects.none, status.迅疗), ability.StatusAbility(240, status.寄生, 300));
噬星.abilities.addAll(RA, hjSFA, sxEFA, hjLEA);

let thermite = BottleUnitType("bottle", 300, 120, 240, status.熔融, Fx.titanSmoke, lib.FF5845);

let zz = newUnit("炸蛛", LegsUnit);
zz.abilities.add(ability.DeathGiftAbility(80, status.庇护, 120, 0.05, 240), ability.HealthRequireAbility(0.2, StatusEffects.none, status.迅疗));

newUnit("异种", LegsUnit);