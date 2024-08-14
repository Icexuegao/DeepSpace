function newItem(name) {
    exports[name] = new Item(name);
}

function newLiquid(name, type) {
    exports[name] = extend(type ? CellLiquid : Liquid, name, {});
}

newItem("铱板");
newItem("导能回路");
newItem("低温化合物");
newItem("铌");
newItem("铈");
newItem("铈凝块");
newItem("陶钢");
newItem("生物钢");
newItem("以太能");
newItem("肃正协议");
newLiquid("血水", true);

/*function newItem(name, color, obj) {
	exports[name] = Object.assign(new Item(name, Color.valueOf(color)), obj);
}
newItem("物品", "123123", {
	cost: 1
});*/

/*物品权重
沙240
铜216
铅144
石墨120
玻璃114
钛102
钍90
铱板78
铈66
硅72
塑钢66
铌57
导能回路54
合金42
陶钢36
布24
生物钢3
以太能1*/