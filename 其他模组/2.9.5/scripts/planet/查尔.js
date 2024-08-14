//require("planet/透视");
const lib = require("base/coflib");
const DX = require("base/地形");
const Char = new Planet("查尔", Planets.sun, 1, 4);
Char.meshLoader = prov(() => new HexMesh(Char, 6));
Char.cloudMeshLoader = prov(() => new MultiMesh(
    new HexSkyMesh(Char, 2, 0.15, 0.14, 5, lib.Color("EBA768C0"), 2, 0.42, 1, 0.43),
    new HexSkyMesh(Char, 3, 0.6, 0.15, 5, lib.Color("EEA293C0"), 2, 0.42, 1.2, 0.45)
));
exports.Char = Char;

Char.generator = extend(SerpuloPlanetGenerator, {
    arr: [Blocks.basalt, DX.boneFloor, DX.bloodScab, DX.bloodSand, DX.bloodFloor, DX.curseFloor, DX.血池, DX.血池浅],
    genTile(position, tile) {
        let block = this.getBlock(position);
        tile.floor = block;
        if (this.vec.y < 0.5 || this.vec.y > 0.6 || this.vec.z > 0.7 || this.vec.z < 0.3) {
            tile.block = tile.floor.asFloor().wall
        }
    },
    getColor(position) {
        return Tmp.c1.set(this.getBlock(position).mapColor);
    },
    getSizeScl() {
        return 7200
    },
    getBlock(position) {
        const noise = (amount, a, b, c) => {
            return Simplex.noise3d(this.seed + amount, a, b, c, position.x, position.y, position.z);
        };
        this.vec = new Vec3(noise(1, 16, 0.2, 8 / 3), noise(6, 72, 0.8, 9 / 2), noise(3, 2, 0.4, 3 / 2));
        let amo = Mathf.round(Mathf.clamp(this.vec.x * this.arr.length, 0, this.arr.length - 1));
        return this.arr[amo]
    },
    vec: 0
});

Char.visible = Char.bloom = Char.accessible = Char.alwaysUnlocked = Char.allowLaunchLoadout = Char.allowSectorInvasion = true; //可见 启用Bloom渲染效果 在行星菜单显示 总是解锁 自定义初始物资 临近区块入侵
Char.prebuildBase = Char.clearSectorOnLose = false; //落地建造 重置战败区块
Char.startSector = 0; //初始区块
Char.orbitRadius = 40; //公转半径
//Char.orbitTime = 180 * 60; //公转一圈时间
Char.rotateTime = 60 * 60; //自转一圈时间
Char.lightSrcFrom = 0;
Char.lightSrcTo = 0.04; //0.1
Char.lightDstFrom = 0.01; //0.05
Char.lightDstTo = 0.05; //0.15
Char.launchCapacityMultiplier = 0.25;
Char.defaultCore = Blocks.coreNucleus;
Char.atmosphereRadIn = 0.03; //进入大气层距离
Char.atmosphereRadOut = 0.45; //离开大气层距离
Char.icon = "curse-of-flesh-Char";
Char.atmosphereColor = Char.iconColor = lib.Color("D75B6E"); //大气层颜色
Char.hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);

const map1 = new SectorPreset("着陆区", Char, 0);
map1.difficulty = 3;
map1.addStartingItems = true;
exports.map1 = map1;

const map2 = new SectorPreset("灾变河谷", Char, 467);
map2.difficulty = 6;
map2.captureWave = 70;
exports.map2 = map2;

const map3 = new SectorPreset("冷寂林地", Char, 347);
map3.difficulty = 7;
map3.captureWave = 80;
exports.map3 = map3;

const map4 = new SectorPreset("环形山", Char, 169);
map4.difficulty = 8;
map4.captureWave = 85;
exports.map4 = map4;

const mapX = new SectorPreset("实验室遗迹", Char, 72);
mapX.difficulty = 13;
mapX.captureWave = 90;
exports.mapX = mapX;

const map5 = new SectorPreset("血湖矿区", Char, 581);
map5.difficulty = 11;
exports.map5 = map5;

const map6 = new SectorPreset("骸骨冰原", Char, 747);
map6.difficulty = 13;
map6.captureWave = 200;
exports.map6 = map6;

const map7 = new SectorPreset("亡骨壕沟", Char, 12);
map7.difficulty = 15;
map7.captureWave = 150;
exports.map7 = map7;

const map8 = new SectorPreset("落日要塞", Char, 600);
map8.difficulty = 20;
exports.map8 = map8;