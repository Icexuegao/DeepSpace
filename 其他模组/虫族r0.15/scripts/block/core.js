const item = require("zerg/item");
const status = require("zerg/status");
const ab = require('zerg/base/ability');
const insect = require('zerg/unit/insect');

const {frame, matrix} = require("zerg/block/other")

function Pay(block) {
    return new BuildPayload(block, Team.derelict)
}

function CoreBuild(build, block, liquid) {
    build.buildType = prov(() => {
        const p = Pay(block);
        return extend(CoreBlock.CoreBuild, build, {
            updateTile() {
                this.super$updateTile();
                if (p.build.team != this.team) {
                    p.build.team = this.team;
                }
                p.update(null, this);
                p.build.power.status = 1;
                if (liquid != null) p.build.handleLiquid(this, Liquids.water, 1);
                p.set(this.x, this.y, p.build.payloadRotation);
            },
            draw() {
                this.super$draw();
                p.draw();
            },
            drawSelect() {
                this.super$drawSelect();
                if (block.range != undefined) {
                    Drawf.dashCircle(this.x, this.y, block.range, Pal.accent);
                } else {
                    Drawf.dashCircle(this.x, this.y, block.radius, Pal.accent);
                }
            }
        })
    });
}

const election = new UnitType("election");
Object.assign(election, {
    aiController: UnitTypes.alpha.aiController,
    isEnemy: false,

    lowAltitude: true,
    flying: true,
    mineSpeed: 8.5,
    mineHardnessScaling: false,
    mineTier: 2,
    buildSpeed: 1,
    drag: 0.05,
    speed: 3,
    rotateSpeed: 15,
    accel: 0.1,
    itemCapacity: 30,
    health: 150,
    targetPriority: -1,
    engineOffset: 4,
    hitSize: 8,
    alwaysUnlocked: true,
    constructor: () => new UnitEntity.create(),
})
election.weapons.add(
    Object.assign(new Weapon("bugs-election-weapon"), {
        top: false,
        y: 5 / 4,
        x: 10 / 4,
        reload: 30,
        ejectEffect: Fx.none,
        recoil: 2,
        shootSound: Sounds.missile,
        inaccuracy: 3,
        alternate: true,
        bullet: Object.assign(new MissileBulletType(4, 17), {
            homingPower: 0.08,
            lifetime: 50,
            keepVelocity: false,
            hitSound: Sounds.none,
            shootEffect: Fx.sparkShoot,
            smokeEffect: Fx.shootBigSmoke,
            backColor: Pal.heal,
            trailColor: Pal.heal,
            frontColor: Color.white,
            hitEffect: Fx.hitLaser,
            despawnEffect: Fx.hitLaser,
            buildingDamageMultiplier: 0.001,
            healPercent: 2.5,
            collidesTeam: true,
        })
    })
)
election.abilities.add(
    Object.assign(new RegenAbility(), {
        percentAmount: 1 / (60 * 60) * 100,
    })
)

const atom = new UnitType("atom");
Object.assign(atom, {
    aiController: UnitTypes.alpha.aiController,
    isEnemy: false,

    lowAltitude: true,
    flying: true,
    mineSpeed: 10,
    mineHardnessScaling: false,
    mineTier: 2,
    buildSpeed: 1.25,
    drag: 0.05,
    speed: 3.3,
    rotateSpeed: 17,
    accel: 0.1,
    itemCapacity: 50,
    health: 170,
    targetPriority: -1,
    engineOffset: 6,
    hitSize: 9,
    alwaysUnlocked: true,
    constructor: () => new UnitEntity.create(),
})
atom.weapons.add(
    Object.assign(new Weapon("bugs-atom-weapon"), {
        reload: 60,
        x: 0,
        y: 0,
        shootY: 5,
        recoil: 1,
        top: false,
        layerOffset: -0.01,
        rotate: false,
        mirror: false,
        shoot: new ShootHelix(),

        bullet: Object.assign(new BasicBulletType(5, 42), {
            width: 7,
            height: 12,
            lifetime: 25,
            shootEffect: Fx.sparkShoot,
            smokeEffect: Fx.shootBigSmoke,
            backColor: Pal.heal,
            trailColor: Pal.heal,
            frontColor: Color.white,
            trailWidth: 1.5,
            trailLength: 5,
            hitEffect: Fx.hitLaser,
            despawnEffect: Fx.hitLaser,
            buildingDamageMultiplier: 0.001,
            healPercent: 7.5,
            collidesTeam: true,
        })
    })
)
atom.abilities.add(
    Object.assign(new RegenAbility(), {
        percentAmount: 1 / (60 * 60) * 100,
    }),
)

const molecule = new UnitType("molecule");
Object.assign(molecule, {
    aiController: UnitTypes.alpha.aiController,
    isEnemy: false,

    lowAltitude: true,
    flying: true,
    mineSpeed: 12.5,
    mineHardnessScaling: false,
    mineTier: 2,
    buildSpeed: 1.5,
    drag: 0.05,
    speed: 3.4,
    rotateSpeed: 22,
    accel: 0.1,
    itemCapacity: 50,
    health: 220,
    targetPriority: -1,
    engineOffset: 7,
    hitSize: 11,
    alwaysUnlocked: true,
    constructor: () => new UnitEntity.create(),
})
molecule.weapons.add(
    Object.assign(new Weapon("bugs-molecule-weapon"), {
        top: false,
        mirror: false,
        rotate: true,
        y: -10 / 4,
        x: 0,
        reload: 50,
        ejectEffect: Fx.none,
        recoil: 2,
        shootSound: Sounds.missile,
        inaccuracy: 3,
        shoot: Object.assign(new ShootPattern(), {
            shots: 3,
            shotDelay: 5,
        }),
        bullet: Object.assign(new MissileBulletType(4, 9), {
            homingPower: 0.08,
            lifetime: 50,
            keepVelocity: false,
            smokeEffect: Fx.none,
            frontColor: Color.white,
            hitSound: Sounds.none,
            buildingDamageMultiplier: 0.001,
        })
    })
)
molecule.abilities.add(
    new ab.MendFieldAbility(125, 300, 8 * 8)
)

const ash = CoreBlock("ash");
exports.ash = ash
Object.assign(ash, {
    alwaysUnlocked: true,
    isFirstTier: true,
    unitType: election,
    health: 1500,
    itemCapacity: 5000,
    size: 3,
    unitCapModifier: 12,
    buildVisibility: BuildVisibility.editorOnly,
    category: Category.effect,
    requirements: ItemStack.with(
        item.nickel, 1000,
        Items.graphite, 500,
    )
})

const albus = CoreBlock("albus");
exports.albus = albus;
Object.assign(albus, {
    unitType: atom,
    health: 5500,
    armor: 3,
    itemCapacity: 10000,
    size: 4,

    unitCapModifier: 18,
    researchCostMultiplier: 0.1,

    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        item.nickel, 5000,
        Items.graphite, 2000,
        Items.silicon, 2000,
    )
})
CoreBuild(albus, frame, Liquids.water)

const annular = new CoreBlock("annular");
exports.annular = annular
Object.assign(annular, {
    unitType: molecule,
    size: 5,
    health: 8800,
    armor: 5,
    itemCapacity: 15000,

    unitCapModifier: 24,
    researchCostMultiplier: 0.1,

    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        item.nickel, 15000,
        Items.silicon, 5000,
        item.chromium, 4000,
    )
})
CoreBuild(annular, matrix, Liquids.water)

const nest = extend(CoreBlock, "nest", {
    canPlaceOn(tile, team, rotation) {
        return true
    },
});
Object.assign(nest, {
    update: true,
    solid: false,
    replaceable: true,
    size: 3,
    health: 1500,
    variants: 2,
    unitCapModifier: 0,
    itemCapacity: 0,
    buildVisibility: BuildVisibility.editorOnly,
    category: Category.effect,
})
nest.buildType = prov(() => extend(CoreBlock.CoreBuild, nest, {
    i: 0,
    updateTile() {
        this.i += Time.delta

        if (this.i >= 60 * 10) {
            let a = Math.random() * 5
            for (let i = 0; i < a; i++) {
                insect.egg.spawn(this.team, this.tile.worldx() + Math.random(), this.tile.worldy() + Math.random());
            }

            this.i = 0
        }
        this.heal(0.25);
        if (this.health < this.maxHealth && Mathf.chance(0.1)) Fx.neoplasmHeal.at(
            this.x + Mathf.range(11),
            this.y + Mathf.range(11),
        );
    }
}))

const column = new Block("column");
Object.assign(column, {
    buildVisibility: BuildVisibility.editorOnly,
    category: Category.effect,
    update: true,
})
column.buildType = prov(() => extend(Building, {
    updateTile() {
        this.tile.circle(5, cons(tile => {
            if (tile.block() == Blocks.coreShard) {
                tile.setBlock(ash, this.team);
            }
        }))
        this.team.core().items.add(item.nickel, 200)
        this.team.core().items.add(Items.graphite, 100)

        this.tile.setBlock(Blocks.air)
    }
}))