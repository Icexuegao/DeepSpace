const item = require('zerg/item');

const nickelPump = new Pump("nickel-pump");
exports.nickelPump = nickelPump;
Object.assign(nickelPump, {
    pumpAmount: 9 / 60,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.nickel, 15,
        item.crystal, 10,
    )
})

const screwPump = new Pump("screw-pump");
exports.screwPump = screwPump;
Object.assign(screwPump, {
    size: 2,
    pumpAmount: 18 / 60,
    health: 240,
    liquidCapacity: 30,
    hasPower: true,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        Items.silicon, 20,
        item.nickel, 70,
        item.manganese, 35,
        item.crystal, 50,
    )
})
screwPump.consumePower(0.35);

const waterExtractor = new SolidPump("water-extractor")
exports.waterExtractor = waterExtractor;
Object.assign(waterExtractor, {
    result: Liquids.water,
    pumpAmount: 0.11,
    attribute: Attribute.water,
    size: 2,
    liquidCapacity: 30,
    rotateSpeed: 1.4,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        Items.graphite, 30,
        item.nickel, 30,
        item.crystal, 30
    )
})
waterExtractor.consumePower(1.5);

const crystalConduit = new Conduit("crystal-conduit");
exports.crystalConduit = crystalConduit;
Object.assign(crystalConduit, {
    health: 45,
    liquidCapacity: 10,
    liquidPressure: 1.05,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.crystal, 1,
    )
})

const manganeseConduit = new Conduit("manganese-conduit");
exports.manganeseConduit = manganeseConduit;
Object.assign(manganeseConduit, {
    health: 90,
    liquidCapacity: 16,
    liquidPressure: 1.25,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.manganese, 2,
        item.crystal, 1
    )
})

const armoredConduit = new ArmoredConduit("armored-conduit");
exports.armoredConduit = armoredConduit;
Object.assign(armoredConduit, {
    health: 260,
    liquidCapacity: 16,
    liquidPressure: 1.25,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.chromium, 2,
        item.crystal, 1,
        item.biomassSteel, 1,
    )
})

const crystalLiquidRouter = new LiquidRouter("crystal-liquid-router");
exports.crystalLiquidRouter = crystalLiquidRouter;
Object.assign(crystalLiquidRouter, {
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        Items.graphite, 4,
        item.crystal, 2
    ),
    liquidCapacity: 30
});

const crystalLiquidContainer = new LiquidRouter("crystal-liquid-container");
exports.crystalLiquidContainer = crystalLiquidContainer;
Object.assign(crystalLiquidContainer, {
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.manganese, 10,
        item.crystal, 15
    ),
    liquidCapacity: 850,
    size: 2,
})

const crystalLiquidTank = new LiquidRouter("crystal-liquid-tank");
exports.crystalLiquidTank = crystalLiquidTank;
Object.assign(crystalLiquidTank, {
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        item.manganese, 30,
        item.crystal, 45
    ),
    liquidCapacity: 2100,
    size: 3
})

const crystalLiquidJunction = new LiquidJunction("crystal-liquid-junction");
exports.crystalLiquidJunction = crystalLiquidJunction;
Object.assign(crystalLiquidJunction, {
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        Items.graphite, 4,
        item.crystal, 8
    ),
})

const crystalConduitBridge = new LiquidBridge("crystal-conduit-bridge");
exports.crystalConduitBridge = crystalConduitBridge;
Object.assign(crystalConduitBridge, {
    fadeIn: false,
    moveArrows: false,
    hasPower: false,
    range: 6,
    arrowSpacing: 6,
    buildVisibility: BuildVisibility.shown,
    category: Category.liquid,
    requirements: ItemStack.with(
        Items.graphite, 8,
        item.crystal, 15,
    ),
})

crystalConduit.junctionReplacement = crystalLiquidJunction;
crystalConduit.bridgeReplacement = crystalConduitBridge;
manganeseConduit.junctionReplacement = crystalLiquidJunction;
manganeseConduit.bridgeReplacement = crystalConduitBridge;
armoredConduit.junctionReplacement = crystalLiquidJunction;
armoredConduit.bridgeReplacement = crystalConduitBridge;