const Ef = require("zerg/Effect");

let reduceArmor = Stat("reduceArmor");
let disabled = Stat("disabled");
let percentDamage = Stat("percentDamage");
let armorLimit = Stat("armorLimit");

exports.corroding = extend(StatusEffect, "corroding", {
    update(unit, time) {
        this.super$update(unit, time);

        if (unit.armor >= 0) {
            unit.armor -= 0.5 / 60
        }
    },
    setStats() {
        this.super$setStats();

        this.stats.add(reduceArmor, 0.5, StatUnit.perSecond)
    },
    damage: 0.2
});

exports.dissolved = extend(StatusEffect, "dissolved", {
    update(unit, time) {
        this.super$update(unit, time);

        if (unit.type.outlineColor === Pal.neoplasmOutline) {
            unit.damageContinuousPierce(unit.type.health / 1200)
            unit.shield = 0
            unit.speedMultiplier = 0.2
            unit.abilities = []

            if (unit.getDuration(this) <= 10) {
                unit.apply(this, 180)
            }
        }
    },
    setStats() {
        this.super$setStats();

        this.stats.add(disabled, false);
        this.stats.add(percentDamage, 5, StatUnit.perSecond)
    },
    init() {
        this.opposite(StatusEffects.tarred)
    },
    effect: Fx.unitDust,
    color: Color.valueOf("b3e5fa"),
})

const poisoned = extend(StatusEffect, "poisoned", {
    color: Color.valueOf("92ab11"),
    damage: 15 / 60,
    effect: Fx.mineSmall,
    damageMultiplier: 1,
    healthMultiplier: 0.8,
})
exports.poisoned = poisoned

const adhering = extend(StatusEffect, "adhering", {
    color: Color.valueOf("9e172c"),
    effect: Fx.mineSmall,
    speedMultiplier: 0.8,
    reloadMultiplier: 0.6
})