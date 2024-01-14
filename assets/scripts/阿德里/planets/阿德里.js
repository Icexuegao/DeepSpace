//模板来自miner!
const colorSrc = Color.valueOf("00ffff");
const colorDst = Color.valueOf("e3f1ff");
var 阿德里 = extend(Planet, "阿德里", Planets.serpulo, 1, 3.7, {

    generator: extend(SerpuloPlanetGenerator, {
        defaultLoadout: Schematics.readBase64("bXNjaAF4nGNgZmBmZmDJS8xNZZDJKCkpKLbS16/MLy0p1UtK1XcNi/Q3cKwwyqkyYOBOSS1OLsosKMnMz2NgYGDLSUxKzSlmYIqOZWTgSs4vStUtzkgsSgFKMYIQkAAAhSEXTA=="),
        getColor(position) {
            // 种子 叠加数量 坚持(每次叠加振幅乘率) 频率 坐标
            let noise = Simplex.noise3d(this.seed,5,0.4, 1 / 3, position.x, position.y, position.z);

            if (noise > 0.3) {
                return colorDst;
            }
            let deep = Simplex.noise3d(this.seed, 6, 0.6, 1, position.x, position.y, position.z);
            return Tmp.c1.set(colorSrc).lerp(Color.black, deep);
        },
        generateSector(sector) {
            this.super$generateSector(sector);

            // TODO
        },
    }),


    // public HexSkyMesh(Planet planet, int seed, float speed, float radius, int divisions, Color color, int octaves, float persistence, float scl, float thresh)
    // 发射时核心携带资源倍率
    launchCapacityMultiplier: 1,
    sectorSeed: 0, // 区块生成种子
    allowWaves: true, // 是否允许生成区块波次
    allowWaveSimulation: true, // 是否允许后台自动挂波次
    allowSectorInvasion: true, // 是否允许区块被敌人进攻
    allowLaunchSchematics: true, // 是否允许使用核心蓝图
    enemyBuildSpeedMultiplier: 1, // 敌人建筑倍率 这里不做处理
    enemyCoreSpawnReplace: true, // 敌人最后的核心被摧毁后是否要生成一个出怪点
    defaultCore: Blocks.coreShard, // 默认的核心 这里不做处理, // 默认的核心 这里不做处理// 默认的核心 这里不做处理
    allowLaunchLoadout: true, // 是否允许发射时携带物资
    clearSectorOnLose: false, // 区块输了是否重置区块(一次过是吧)
    tidalLock: false, // 是否潮汐锁定(冷知识: erekir潮汐锁定)
    prebuildBase: false, // 是否需要像e星那样帅气的着陆建筑特效
    // ruleSetter: 当区块地图加载时会自动将地图规则作以下调整
    ruleSetter: r => {
        r.waveTeam = Team.crux;
        r.placeRangeCheck = false; // 恶心的建筑区
        r.showSpawns = false; // 在地图上不显示出怪点
    },
    iconColor: Color.valueOf("e3f1ff"), // 星球面板(PlanetDialog)上的颜色
    // 环境
    atmosphereColor: Color.valueOf("c6ffff"), // 环境色
    atmosphereRadIn: 0.02, atmosphereRadOut: 0.3,
    // 光照
    updateLighting: true, // 是否有昼夜更替
    lightSrcFrom: 0, // 光照数值 这里不做处理
    lightSrcTo: 0.8, lightDstFrom: 0.2, lightDstTo: 1,
    startSector: 1, // 开始区块(类似零号地区) (这个的查看可以用我的mod<显示星球区块id>)
    alwaysUnlocked: true, // 是否默认解锁
    landCloudColor: Object.assign(Pal.spore.cpy(), {a: 0.5}), // 核心着陆烟尘的颜色
    init() {
        this.super$init();
        this.meshLoader = () => new HexMesh(this,6);//星球网格大小
        //云层网格
        this.cloudMeshLoader = () => new MultiMesh(new HexSkyMesh(this, 11, 0.15, 0.13, 5, Object.assign(new Color().set(colorSrc).mul(0.9), {a: 0.75}), 2, 0.45, 0.9, 0.38), new HexSkyMesh(this, 1, 0.6, 0.16, 5, Object.assign(Color.white.cpy().lerp(colorDst, 0.55), {a: 0.75}), 2, 0.45, 1, 0.41));

        this.hiddenItems.addAll(), // 在星球上隐藏的物品,会决定该星球上能建造的方块
            this.unlockedOnLand.addAll(); // 着陆该星球就解锁某些物品
    },
});
module.exports = 阿德里;