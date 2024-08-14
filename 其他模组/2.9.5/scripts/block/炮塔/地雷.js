const my = require("base/物品");
const lib = require("base/coflib")
const status = require("base/status");

let pulse = Object.assign(new BasicBulletType(0, 120), {
    instantDisappear: true,
    splashDamage: 480,
    splashDamageRadius: 32,
    status: status.连锁闪电,
    statusDuration: 60,
    ammoMultiplier: 1,
    hitSound: Sounds.plasmaboom,
    hitEffect: Fx.dynamicSpikes.wrap(lib.Color("A9D8FF"), 30)
});
let pulseMine = extend(ShockMine, "高能脉冲地雷", {
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.reload, 60 / 90, StatUnit.perSecond);
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, pulse)));
    }
});
lib.setBuilding(ShockMine.ShockMineBuild, pulseMine, {
    unitOn(unit) {
        if (!this.enabled || unit.team == this.team || !this.timer.get(cooldown)) return
        pulse.create(this, this.x, this.y, 0);
    }
});
Object.assign(pulseMine, {
    size: 2,
    armor: 4,
    health: 480,
    hasShadow: false,
    underBullets: true,
    crushDamageMultiplier: 0
});
pulseMine.setupRequirements(
    Category.effect,
    BuildVisibility.shown,
    ItemStack.with(
        my.铱板, 35,
        my.导能回路, 25,
        Items.surgeAlloy, 15
    )
);
exports.高能脉冲地雷 = pulseMine;


let damage = 150, cooldown = 300;
let Back = extend(ShockMine, "传送奇点", {
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.damage, damage);
        this.stats.add(Stat.reload, 60 / cooldown, StatUnit.perSecond);
    }
})
lib.setBuilding(ShockMine.ShockMineBuild, Back, {
    unitOn(unit) {
        if (!this.enabled || unit.team == this.team || !this.timer.get(cooldown)) return
        let spawner = Vars.spawner.getSpawns();
        if (spawner.size > 0) {
            let random = Mathf.random(0, spawner.size - 1);
            unit.x = spawner.get(random).x * 8 + Mathf.range(2 * 8);
            unit.y = spawner.get(random).y * 8 + Mathf.range(2 * 8);
            Fx.chainLightning.at(this.x, this.y, 0, Pal.gray, unit);
            unit.damagePierce(damage);
            Sounds.spark.at(this);
        } else {
            this.kill();
        }
    }
});
Object.assign(Back, {
    size: 2,
    armor: 4,
    health: 480,
    hasShadow: false,
    underBullets: true,
    crushDamageMultiplier: 0
});
Back.setupRequirements(
    Category.effect,
    BuildVisibility.shown,
    ItemStack.with(
        my.铱板, 35,
        my.导能回路, 25,
        Items.phaseFabric, 15
    )
);
exports.Back = Back;