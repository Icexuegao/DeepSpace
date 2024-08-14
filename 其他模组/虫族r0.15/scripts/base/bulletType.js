const status = require('zerg/status');
const liquid = require('zerg/liquid');

function Acid(puddleSize) {
    return extend(LiquidBulletType, {
        speed: 0.1,
        damage: 0,
        liquid: liquid.acid,
        lifetime: 1,
        puddleSize: puddleSize,

        knockback: 0,

        status: status.corroding,
        statusDuration: 120,
    })
}

exports.Acid = Acid

function ReduceArmorBulletType(speed, damage, amount) {
    return extend(BasicBulletType, speed, damage, {
        hitEntity(b, entity, health) {
            this.super$hitEntity(b, entity, health);
            if (entity instanceof Unit) {
                var unit = entity;
                unit.armor -= amount;
            }
        },
        pierceCap: 2,
        pierce: true,
        pierceBuilding: true,

        buildingDamageMultiplier: 1.5,

        smokeEffect: Fx.shootBigSmoke,
        shootEffect: Fx.shootBigColor,
        despawnEffect: Fx.hitBulletColor,
        hitEffect: Fx.hitBulletColor,

        shrinkX: 0,
        shrinkY: 0,

        ammoMultiplier: 2,
    });
}

exports.ReduceArmorBulletType = ReduceArmorBulletType;

function RicochetBulletType(speed, damage) {
    return extend(BasicBulletType, speed, damage, {
        hitEntity(b, entity, health) {
            if (entity instanceof Unit) {
                if (Mathf.chance(Math.pow(entity.armor / b.damage, 3))) {
                    b.vel.setAngle(b.rotation() + Mathf.range(60) + 180)
                } else {
                    this.super$hitEntity(b, entity, health);
                    b.remove()
                }
            }
        },
        pierce: true
    });
}

exports.RicochetBulletType = RicochetBulletType