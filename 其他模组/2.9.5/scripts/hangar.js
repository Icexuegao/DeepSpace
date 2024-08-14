function CircleAI() {
    return extend(FlyingAI, {
        updateMovement() {
            unloadPayloads()
            if ((unit instanceof BuildingTetherc) && unit.building() != null) {
                let build = unit.building();
            }
            if (target != null) {
                circleAttack(120);
            } else {
                circle(120, build)
            }
        }
    })
}

function CircleAI() {
    let shooter;
    return extend(AIController, {
        updateMovement() {
            unloadPayloads();
            let time = unit instanceof TimedKillc ? t.time() : 3600;
            if (time >= unit.type.homingDelay && shooter != null) {
                unit.lookAt(shooter.aimX, shooter.aimY);
            }
            unit.moveAt(vec.trns(unit.rotation, unit.type.missileAccelTime <= 0 ? unit.speed() : Mathf.pow(Math.min(time / unit.type.missileAccelTime, 1), 2) * unit.speed()));
        },
        retarget() {
            return timer.get(timerTarget, 4);
        }
    })
}

const electron = new UnitType("electron");
exports.electron = electron;
Object.assign(electron, {
    health: 75,
    speed: 4,
    flying: true,
    hitSize: 6,
    engineOffset: 5.75,
    engineColor: Pal.lancerLaser,
    trailLength: 8,
    trailColor: Pal.lancerLaser,
    armor: 0,
    accel: 0.08,
    drag: 0.04,
    targetAir: false,
    range: 140,
    faceTarget: false,
    circleTarget: true,
    crashDamageMultiplier: 5,
    targetFlags: [BlockFlag.generator, null],
    itemCapacity: 0,
    constructor: () => new BuildingTetherPayloadUnit.create(),
    aiController: () => new CircleAI()
})
electron.abilities.add(
    new MoveLightningAbility(12, 9, 0.25, -2, 1, 4, Pal.lancerLaser)
)
electron.weapons.add(
    Object.assign(new Weapon(), {
        x: 0,
        y: -2,
        reload: 15,
        alwaysShooting: true,
        bullet: Object.assign(new LightningBulletType(), {
            damage: 12,
            lightningLength: 9,
            pierceArmor: true,
            ammoMultiplier: 1,
            shootEffect: Fx.sparkShoot,
            smokeEffect: Fx.shootBigSmoke,
        })
    })
)

let unitType = electron;
let reload = 20 * 60;

const hangar = new Block("hangar");
hangar.buildVisibility = BuildVisibility.shown,
    hangar.category = Category.effect,
    hangar.update = true,
    hangar.buildType = prov(() => extend(Building, {
        i: 0,
        updateTile() {
            this.i += Time.delta
            if (Units.canCreate(this.team, unitType) && this.i >= reload) {
                let unit = unitType.create(this.team);
                if (unit instanceof BuildingTetherc) {
                    unit.building(this);
                }
                unit.set(this.x, this.y)
                unit.rotation = 90
                unit.add()
                this.i = 0
            }
        },
        draw() {
            this.super$draw();
            if (Units.canCreate(this.team, unitType)) {
                Draw.draw(Layer.blockOver, () => {
                    Drawf.construct(this, unitType.fullIcon, 0, this.i, 1, this.i);
                });
            }
        }
    }))