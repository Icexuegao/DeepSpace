require("2.9.5/scripts/other/简介");
//require("介绍");
require("2.9.5/scripts/other/区块编号");
require("2.9.5/scripts/other/资源显示");
require("2.9.5/scripts/other/作弊菜单");

require("2.9.5/scripts/base/地形");
require("2.9.5/scripts/planet/查尔星环");

//require("block/单位/高级装配模块");
require("2.9.5/scripts/block/单位/异种构造");
require("2.9.5/scripts/block/单位/载荷发射台");
require("2.9.5/scripts/block/单位/折跃枢纽");

//require("block/工厂/工厂模板");
require("2.9.5/scripts/block/工厂/焚烧制热炉");
require("2.9.5/scripts/block/工厂/铈提取器");
require("2.9.5/scripts/block/工厂/液体焚化炉");
require("2.9.5/scripts/block/工厂/以太封装器");
require("2.9.5/scripts/block/工厂/装卸器核心");
require("2.9.5/scripts/block/工厂/装卸器资源");

require("2.9.5/scripts/block/环境/失稳晶簇");

require("2.9.5/scripts/block/炮塔/test1");
require("2.9.5/scripts/block/炮塔/test2");
require("2.9.5/scripts/block/炮塔/test3");
require("2.9.5/scripts/block/炮塔/test4");
require("2.9.5/scripts/block/炮塔/处决");
require("2.9.5/scripts/block/炮塔/洪流");
require("2.9.5/scripts/block/炮塔/解脱");
require("2.9.5/scripts/block/炮塔/脉冲放射塔");
require("2.9.5/scripts/block/炮塔/脉冲星");
require("2.9.5/scripts/block/炮塔/解脱");
require("2.9.5/scripts/block/炮塔/暮光");
require("2.9.5/scripts/block/炮塔/霓虹");
require("2.9.5/scripts/block/炮塔/清算");
require("2.9.5/scripts/block/炮塔/散华");
require("2.9.5/scripts/block/炮塔/曙光");
require("2.9.5/scripts/block/炮塔/诏书");
require("2.9.5/scripts/block/炮塔/箴言");
require("2.9.5/scripts/block/炮塔/桎梏");
require("2.9.5/scripts/block/炮塔/罪碑");

//require("block/运输/分类桥");

require("2.9.5/scripts/block/杂项/拆墙器");
require("2.9.5/scripts/block/杂项/大摆锤");
require("2.9.5/scripts/block/杂项/定向加速器");
require("2.9.5/scripts/block/杂项/护盾");
require("2.9.5/scripts/block/杂项/偏转力场");
require("2.9.5/scripts/block/杂项/音乐盒");
require("2.9.5/scripts/block/杂项/炸弹");
require("2.9.5/scripts/block/杂项/炸矿机");
require("2.9.5/scripts/block/杂项/重生点");

require("2.9.5/scripts/block/超级源");
require("2.9.5/scripts/block/血肉建筑");
require("2.9.5/scripts/block/联通");

require("2.9.5/scripts/unit/单位升级");
//require("unit/斑蟒");
require("2.9.5/scripts/unit/沧溟");
require("2.9.5/scripts/unit/观察者");
require("2.9.5/scripts/unit/致胜");

require("2.9.5/scripts/weather/磁暴");
require("2.9.5/scripts/weather/闪电风暴");
require("2.9.5/scripts/base/tree");
require("2.9.5/scripts/蓄力");

Vars.maxSchematicSize = 128;

/*Events.on(ClientLoadEvent, cons((e) => {
	Vars.content.each(cons(e => {
		if (e instanceof UnitType) {
			e.envDisabled = Env.none;
		}
		if (e instanceof Planet) {
			e.hiddenItems.clear();
		}
	}));
})); //全单位解禁*/

/*function defense(u) {
	return u.health / (1 - Math.sqrt(u.hitSize + Math.pow(1 + u.armor / 100, 2)) * 0.01) / 15;
}
Events.on(ClientLoadEvent, () => {
	Vars.content.units().each(unit => {
		print(unit.localizedName + "： " + Strings.fixed(defense(unit), 1));
	})
})*/