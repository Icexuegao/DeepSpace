const amino = new Item("amino", Color.valueOf("d6dbe7"));
exports.amino = amino;
Object.assign(amino, {})

const biomass = new Item("biomass", Color.valueOf("84a94b"));
exports.biomass = biomass;
Object.assign(biomass, {
    flammability: 1.55,
    buildable: false,
})

const nickel = new Item("nickel", Color.valueOf("00c49b"));
exports.nickel = nickel
Object.assign(nickel, {
    hardness: 2,
    cost: 0.5,
    alwaysUnlocked: true
})

const manganese = new Item("manganese", Color.valueOf("ecaae2"))
exports.manganese = manganese;
Object.assign(manganese, {
    hardness: 3,
    cost: 1.2,
})

const crystal = new Item("crystal", Color.valueOf("7e8ae6"));
exports.crystal = crystal;
Object.assign(crystal, {
    hardness: 3,
    cost: 2,
    healthScaling: 0.5,
})

const chromium = new Item("chromium", Color.valueOf("e45018"));
exports.chromium = chromium;
Object.assign(chromium, {
    hardness: 4,
    cost: 1.5,
    healthScaling: 0.75,
})

const biomassSteel = new Item("biomass-steel", Color.valueOf("98ba53"));
exports.biomassSteel = biomassSteel;
Object.assign(biomassSteel, {
    cost: 1.25,
    healthScaling: 0.5,
})

const iridium = new Item("iridium", Color.valueOf("c9dae3"));
exports.iridium = iridium;
Object.assign(iridium, {
    cost: 1.5,
    healthScaling: 0.5,
})

const energic = new Item("energic", Color.valueOf("fa7f7f"));
exports.energic = energic
Object.assign(energic, {
    cost: 1.5,
    healthScaling: 1.5,
    explosiveness: 0.4,
    charge: 1,
})

const salt = new Item("salt", Color.valueOf("c3c1bb"));
exports.salt = salt;
Object.assign(salt, {
    buildable: false,
})

const sulfone = new Item("sulfone", Color.valueOf("ede892"));
exports.sulfone = sulfone;
Object.assign(sulfone, {
    flammability: 1.9,
    explosiveness: 0.4,
    buildable: false,
})