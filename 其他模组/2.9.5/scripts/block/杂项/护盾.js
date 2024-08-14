const my = require("base/物品");

function newDun(name, ItemStack) {
    let nD = new ForceProjector(name);
    nD.itemConsumer = nD.consumeItems(ItemStack).boost();
}

newDun("盾1", ItemStack.with(Items.silicon, 1))
newDun("盾3", ItemStack.with(Items.phaseFabric, 1))
newDun("盾5", ItemStack.with(my.导能回路, 2))