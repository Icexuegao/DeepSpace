const 阿德里 = require("阿德里/planets/阿德里");

// 创建主线区块
/**
    看源码SectorPreset.java:
        public SectorPreset(String name, Planet planet, int sector)
    可以知道SectorPreset的一个构造函数包含以下参数:
        name: 主线区块的name;
            这里的name决定主线区块地图的文件名字
            地图文件见<maps/>
        planet: 所在星球;
        sector: 所在区块id(这个的查看可以用我的mod<显示星球区块id>);
*/var 伊始 = Object.assign(new SectorPreset("伊始", 阿德里, 1), {
    alwaysUnlocked: true, // 是否默认解锁
    addStartingItems: true, // 待定
    description: "我们降落到了孢子冰层，周围有敌对生物在游荡，小心行事",
    isLastSector: false, // 是否是主线结尾区块 用于提示已经完成星球主线
    overrideLaunchDefaults: true, // 可配合allowLaunchSchematics
    allowLaunchSchematics: true, // 该区块是否能用核心蓝图 可配合overrideLaunchDefaults
    attackAfterWaves: false, // 波次完成后是否启用进攻模式
    noLighting: false, // 区块是否关闭昼夜更替
    captureWave: 30, // 占领波数
    difficulty:3, // 难度
    startWaveTimeMultiplier: 3, // 首波次准备时间倍率
});
exportPreset("伊始", 伊始);
function exportPreset(name, sectorPreset){
    module.exports[name] = sectorPreset;
}