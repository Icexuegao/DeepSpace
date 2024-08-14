const status = require("zerg/status");

Liquids.neoplasm.spreadTarget = null;
Liquids.neoplasm.viscosity = 0.95;

const acid = extend(Liquid, "acid", Color.valueOf("84a94b"), {
    update(puddle) {
        if (puddle.tile != null && puddle.tile.build != null) {
            puddle.tile.build.damage(0.2)

            puddle.amount -= 0.5

            if (Mathf.chanceDelta(0.05)) {
                Fx.mineSmall.at(puddle.x, puddle.y)
            }
        }
    }
});
exports.acid = acid;
Object.assign(acid, {
    effect: status.corroding,
    viscosity: 0.8,
    heatCapacity: 0.2,
    temperature: 0.54,
    flammability: 0,
    capPuddles: false,
    coolant: false,
})

const dissolvant = new Liquid("dissolvant", Color.valueOf("b3e5fa"))
exports.dissolvant = dissolvant;
Object.assign(dissolvant, {
    flammability: 1.35,
    viscosity: 0.3,
    coolant: false,
    effect: status.dissolved,
})

const acetylene = new Liquid("acetylene", Color.valueOf("848a86"))
exports.acetylene = acetylene;
Object.assign(acetylene, {
    gas: true,
    flammability: 1.5,
    explosiveness: 1,
    coolant: false,
})

const yperite = new Liquid("yperite", Color.valueOf("92ab11"))
exports.yperite = yperite;
Object.assign(yperite, {
    gas: true,
    explosiveness: 3,
    coolant: false,
})