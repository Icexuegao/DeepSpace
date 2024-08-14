const item = require("zerg/item");

const resurrection = new MendProjector("resurrection");
exports.resurrection = resurrection;
Object.assign(resurrection, {
    size: 3,
    reload: 120,
    range: 120,
    phaseRangeBoost: 60,
    healPercent: 8,
    phaseBoost: 2,
    health: 720,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 200,
        Items.graphite, 150,
        item.nickel, 250,
        item.manganese, 175,
    )
})
resurrection.consumePower(5.5);
resurrection.consumeItem(Items.silicon, 1).boost();

const catalyzer = new OverdriveProjector("catalyzer");
exports.catalyzer = catalyzer;
Object.assign(catalyzer, {
    reload: 60,
    range: 80,
    speedBoost: 1.35,
    speedBoostPhase: 0.35,
    useTime: 120,
    phaseRangeBoost: 0,
    hasBoost: true,
    size: 2,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 75,
        item.nickel, 100,
        item.manganese, 75,
    )
})
catalyzer.consumeItem(item.manganese, 1).boost();
catalyzer.consumePower(2.5);

const prophet = new OverdriveProjector("prophet");
exports.prophet = prophet;
Object.assign(prophet, {
    reload: 60,
    range: 120,
    speedBoost: 2.1,
    speedBoostPhase: 0,
    useTime: 120,
    phaseRangeBoost: 0,
    hasBoost: true,
    size: 3,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 125,
        item.manganese, 275,
        item.chromium, 75,
        item.biomassSteel, 50,
    )
})
prophet.consumePower(4.2);

const frame = new ForceProjector("frame");
exports.frame = frame;
Object.assign(frame, {
    radius: 32,
    shieldHealth: 400,
    consumeCoolant: true,
    hasLiquids: true,
    cooldownNormal: 40 / 60,
    cooldownLiquid: 20 / 60,
    coolantConsumption: 0.05,
    cooldownBrokenBase: 20 / 60,
    size: 1,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 25,
        item.nickel, 50,
        item.crystal, 25,
    )
})
frame.consumePower(0.8);

const matrix = new ForceProjector("matrix");
exports.matrix = matrix;
Object.assign(matrix, {
    radius: 64,
    shieldHealth: 1200,
    consumeCoolant: true,
    hasLiquids: true,
    cooldownNormal: 80 / 60,
    cooldownLiquid: 40 / 60,
    coolantConsumption: 0.1,
    cooldownBrokenBase: 40 / 60,
    size: 2,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 75,
        item.nickel, 100,
        item.manganese, 50,
        item.crystal, 125,
    )
})
matrix.consumePower(2.4)

const clan = new ForceProjector("clan");
exports.clan = clan
Object.assign(clan, {
    radius: 128,
    shieldHealth: 2400,
    consumeCoolant: true,
    hasLiquids: true,
    cooldownNormal: 150 / 60,
    cooldownLiquid: 60 / 60,
    coolantConsumption: 0.15,
    cooldownBrokenBase: 100 / 60,
    size: 3,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 125,
        item.nickel, 120,
        item.manganese, 75,
        item.chromium, 25,
        item.crystal, 205,
    )
})
clan.consumePower(3.4)

const box = new StorageBlock("box");
exports.box = box;
Object.assign(box, {
    size: 2,
    itemCapacity: 375,
    scaledHealth: 65,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        item.nickel, 25,
        item.manganese, 75,
    )
})

const unloader = new Unloader("unloader");
exports.unloader = unloader;
Object.assign(unloader, {
    speed: 60 / 16,
    group: BlockGroup.transportation,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 20,
        item.manganese, 30,
    )
})

const launchPad = new LaunchPad("launch-pad");
exports.launchPad = launchPad;
Object.assign(launchPad, {
    size: 3,
    itemCapacity: 120,
    launchTime: 60 * 20,
    hasPower: true,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.silicon, 150,
        item.nickel, 550,
        item.manganese, 150,
    ),
})
launchPad.consumePower(4);

const lamp = extend(LightBlock, "lamp", {
    drawPlace(x, y, rotation, valid) {
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, this.radius * 2, Pal.accent);
    },
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.shootRange, (this.radius / 8) * 2, StatUnit.blocks);
    },
    size: 2,
    brightness: 0.9,
    radius: 120,
    buildVisibility: BuildVisibility.shown,
    category: Category.effect,
    requirements: ItemStack.with(
        Items.graphite, 12,
        item.nickel, 8,
    )
});
exports.lamp = lamp;
lamp.buildType = prov(() => {
    return extend(LightBlock.LightBuild, lamp, {
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, this.block.radius * 2, Pal.accent);
        }
    })
});
lamp.consumePower(0.1);