package Iceconent.content;

import Iceconent.World.Planet.IcePlanetGenerator;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.Planet;

public class IcePlanet {

    public static Planet aDri, iceSun;

    public IcePlanet() {
    }

    public static void load() {
        iceSun = new Planet("iceSun", null, 17f) {{
            bloom = true;
            accessible = false;
            meshLoader = () -> new SunMesh(this, 4, 5, 0.3, 1.7, 1.2, 1, 1.1f, Color.valueOf("000000"), Color.valueOf("000000"), Color.valueOf("000000"), Color.valueOf("000000"), Color.valueOf("ff9638"), Color.valueOf("ffc64c"), Color.valueOf("ffc64c"), Color.valueOf("ffe371"), Color.valueOf("f4ee8e"));
        }};

        aDri = new Planet("aDri", iceSun, /**星球大小*/2.0F,/**区块大小*/4) {{
            icon = "aDri";/**星球左上角贴图*/
            generator = new IcePlanetGenerator();
            meshLoader = () -> new HexMesh(aDri, 6); /** 星球网格大小   */
            cloudMeshLoader = () -> new MultiMesh(new HexSkyMesh(this, 2, 0.15F, 0.14F, 5, Color.valueOf("eba768").a(0.75F), 2, 0.42F, 1.0F, 0.43F), new HexSkyMesh(this, 3, 0.6F, 0.15F, 5, Color.valueOf("eea293").a(0.75F), 2, 0.42F, 1.2F, 0.45F));
            launchCapacityMultiplier = 0.5F;
            sectorSeed = 2;
            allowWaves = true;/** 是否允许生成区块波次*/
            orbitRadius = 60f;
            allowWaveSimulation = true;/**是否允许后台自动挂波次*/
            allowSectorInvasion = false;/**是否允许区块被敌人进攻*/
            allowLaunchSchematics = true;/**   // 是否允许使用核心蓝图 */
            iconColor = Color.white;/**星球面板(PlanetDialog)上的颜色*/
            enemyCoreSpawnReplace = true;/** 敌人最后的核心被摧毁后是否要生成一个出怪点  */
            allowLaunchLoadout = true;/**是否允许发射时携带物资 */
            prebuildBase = false;

            ruleSetter = (R) -> {/** 当区块地图加载时会自动将地图规则作以下调整  */
                R.waveTeam = Team.blue;
                R.coreIncinerates = true;
                R.placeRangeCheck = false;/**  恶心的建筑区  */
                R.showSpawns = false;/** 在地图上不显示出怪点  */
            };
            atmosphereColor = Color.valueOf("3c1b8f");/**环境色 */
            atmosphereRadIn = 2F;/** 大气层 */
            atmosphereRadOut = 0.3F;
            defaultCore = Blocks.coreBastion;/**默认的核心 这里不做处理, */
            startSector = 0;/** 开始区块(类似零号地区) (这个的查看可以用我的mod<显示星球区块id>)  */
            alwaysUnlocked = true;/**是否默认解锁 */
            lightDstFrom = 0;/**日夜更周 */
            lightDstTo = 1f;
            lightSrcFrom = 0;
            lightSrcTo = 0.8f;
            updateLighting = true;/** 是否有昼夜更替   */
            tidalLock = false;/** 是否潮汐锁定(冷知识: erekir潮汐锁定)  */
            clearSectorOnLose = false;/** 区块输了是否重置区块(一次过是吧)  */
            enemyBuildSpeedMultiplier = 1;/**  敌人建筑倍率 这里不做处理 */
            landCloudColor = Pal.spore.cpy().a(0.5F);/** 核心着陆烟尘的颜色  */
            prebuildBase = true;/**  是否需要像e星那样帅气地着陆建筑特效 */
            //  hiddenItems.add(Items.copper, Items.lead);
        }};
    }
}