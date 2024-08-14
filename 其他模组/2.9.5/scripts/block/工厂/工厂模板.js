const yE = require("block/工厂/Effect");

let craftTime = 180;

function newCraft(name, ME) {
    let nC = extend(GenericCrafter, name, {});
    nC.size = 3;
    nC.health = 480;
    nC.hasLiquids = false;
    nC.consumePower(6);
    nC.consumeItems(ItemStack.with(
        Items.coal, 5,
        Items.sand, 5));
    nC.craftTime = craftTime;
    nC.craftEffect = ME;
    nC.outputItem = new ItemStack(Items.silicon, 10);
    nC.requirements = ItemStack.with(
        Items.graphite, 1
    );
    nC.itemCapacity = 20;
    nC.buildVisibility = BuildVisibility.shown;
    nC.category = Category.crafting;
    exports[name] = nC;
    return nC
};


let C1 = newCraft("工厂1", MultiEffect(yE.yuanEffect(craftTime, 64, 4, 90), yE.yuanEffect(craftTime, 32, 4, 90, 1)));
let C2 = newCraft("工厂2", MultiEffect(yE.shiziEffect(craftTime, 3, 8, 4), yE.shiziEffect(craftTime, 1.5, 4, 2)));
let C3 = newCraft("工厂3", yE.zhunxin(craftTime, 8, 90, 4));
let C31 = newCraft("工厂3-", yE.zhunxin(craftTime, 6, 90, 4, 1, 1));
let C4 = newCraft("工厂4", yE.yuanshizi(craftTime, 64, 90, 1));
let C41 = newCraft("工厂4-", yE.yuanshizi(craftTime, 32, 90));
let C5 = newCraft("工厂5", yE.zhunxin(craftTime, 6, 90, 6, 1, 0, 1));