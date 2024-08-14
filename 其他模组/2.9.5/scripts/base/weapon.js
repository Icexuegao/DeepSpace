const lib = require("base/coflib");
const {熔融} = require("base/status");

exports.StatWeapon = (name, b, stat, stat2) => {
    return extend(Weapon, {
        name: "curse-of-flesh-" + name,
        bullet: b,
        addStats(u, t) {
            this.super$addStats(u, t);
            t.row();
            t.add(stat).row();
            if (stat2) t.add(stat2);
        }
    })
}

exports.SpawnWeapon = (name, reload, b) => {
    let w = exports.StatWeapon(name, b, lib.bundle("spawn", b.despawnUnit.localizedName));
    w.x = 0;
    w.reload = reload;
    w.mirror = false;
    return w;
}

exports.TurretWeapon = (name, b, limitTime) => {
    let w = exports.StatWeapon(name, b, lib.bundle("turret", limitTime, b.turretName()));
    w.x = 0;
    w.mirror = false;
    return w;
}

exports.ArmorWeapon = (name, b) => {
    return exports.StatWeapon(name, b, lib.bundle("factor", b.pierceFactor()), lib.bundle("armor", b.armorDamage(), b.armorReduce()));
}

exports.SizeWeapon = (name, b) => {
    return exports.StatWeapon(name, b, lib.bundle("stifle"));
}

exports.RepairWeapon = (name, wx, wy, color, speed, range) => {
    return extend(RepairBeamWeapon, {
        name: "curse-of-flesh-" + name,
        x: wx,
        y: wy,
        shootY: 0,
        mirror: false,
        laserColor: color,
        repairSpeed: speed,
        bullet: Object.assign(new BulletType(), {
            maxRange: range
        })
    })
}

exports.PointDefenseWeapon = (name, wx, wy, color, reload, damage, range) => {
    return extend(PointDefenseWeapon, {
        name: "curse-of-flesh-" + name,
        x: wx,
        y: wy,
        shootY: 0,
        color: color,
        reload: reload,
        targetInterval: reload,
        targetSwitchInterval: reload,
        bullet: Object.assign(new BulletType(), {
            damage: damage,
            maxRange: range,
            shootEffect: Fx.sparkShoot,
            hitEffect: Fx.pointHit
        })
    })
}

exports.EngineWeapon = (wy, damage, length, width, color, mirror) => {
    return extend(Weapon, {
        x: 0,
        y: wy,
        shootY: 0,
        reload: 300,
        mirror: mirror,
        alternate: !mirror,
        useAmmo: false,
        baseRotation: 180,
        alwaysShooting: true,
        alwaysContinuous: true,
        shootSound: Sounds.none,
        bullet: Object.assign(new ContinuousFlameBulletType(), {
            colors: [
                lib.Color(color + "8C"),
                lib.Color(color + "C2"),
                lib.Color(color + "CC"),
                lib.Color(color),
                lib.Color(Color.white)
            ],
            damage: 20,
            length: length,
            width: width,
            drawFlare: false,
            status: 熔融,
            statusDuration: 150
        })
    })
}