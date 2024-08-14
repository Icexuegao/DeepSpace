const item = require("zerg/item");

const message = new MessageBlock("message")
exports.message = message;
Object.assign(message, {
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.graphite, 5,
        item.nickel, 5,
    ),
})

const switchBlock = new SwitchBlock("switch");
exports.switchBlock = switchBlock;
Object.assign(switchBlock, {
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.graphite, 10,
        item.nickel, 5,
    ),
})

const processor = new LogicBlock("processor");
exports.processor = processor;
Object.assign(processor, {
    instructionsPerTick: 3,
    size: 1,
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.graphite, 20,
        Items.silicon, 30,
        item.nickel, 50,
    ),
})

const processorLarge = new LogicBlock("processor-large");
exports.processorLarge = processorLarge;
Object.assign(processorLarge, {
    instructionsPerTick: 13,
    range: 8 * 22,
    size: 2,
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.graphite, 100,
        Items.silicon, 150,
        item.nickel, 100,
        item.manganese, 75,
    ),
})

const memoryCell = new MemoryBlock("memory-cell");
exports.memoryCell = memoryCell;
Object.assign(memoryCell, {
    size: 1,
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.graphite, 30,
        Items.silicon, 30,
        item.nickel, 30,
    ),
})

const logicDisplay = new LogicDisplay("logic-display");
exports.logicDisplay = logicDisplay;
Object.assign(logicDisplay, {
    displaySize: 128,
    size: 5,
    buildVisibility: BuildVisibility.shown,
    category: Category.logic,
    requirements: ItemStack.with(
        Items.silicon, 30,
        item.nickel, 50,
        item.crystal, 75,
    ),
})