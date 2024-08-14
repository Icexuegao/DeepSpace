package ice.asundry.Content;

import ice.content.IceBlocks;
import ice.asundry.world.planet.ADriPlanetGenerator;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.ItemStack;
import mindustry.type.Planet;

public class IcePlanets {

    public static Planet aDri, blueGiant;

    public IcePlanets() {
    }

    public static void load() {
        blueGiant = new Planet("blueGiant", null, 16f) {{
            bloom = true;
            accessible = false;
            meshLoader = () ->

                    new SunMesh(this, 4, 5, 0.3, 1.7, 1.2, 1, 1.1f,

                            Color.valueOf("bfdfff"),

                            Color.valueOf("bfdfff"),

                            Color.valueOf("bfdfff"),

                            Color.valueOf("bfdfff"),

                            Color.valueOf("ecf6ff"),

                            Color.valueOf("ecf6ff")

                    );
        }};
        aDri = new Planet("aDri", blueGiant, /**星球大小*/1F,/**区块大小也决定区块数量*/3) {{
            orbitOffset = 1;/** 轨道偏移  */
            generator = new ADriPlanetGenerator();
            meshLoader = () -> new HexMesh(aDri, 6); /** 星球网格大小   */
            cloudMeshLoader = () -> new MultiMesh(

                    new HexSkyMesh(this, 3, 0.45f, 0.15f, 5, Color.valueOf("CE8B8BFF"), 2, 0.45f, 0.9f, 0.38f),

                    new HexSkyMesh(this, 11, 0.9f, 0.13f, 5, Color.valueOf("ff3c5c90"), 2, 0.45f, 0.9f, 0.38f),

                    new HexSkyMesh(this, 1, 0.36f, 0.16f, 5, Color.valueOf("fe869a90"), 2, 0.45f, 1f, 0.41f)

            );
            sectorSeed = 121;/** 这个星球上扇区基地生成的种子。-1 使用基于 ID 的随机值。*/
            allowWaves = true;/** 是否允许生成区块波次*/
            orbitRadius = 60f;/** 围绕太阳的轨道半径。除非您确切知道自己在做什么，否则请勿更改。*/
            allowWaveSimulation = true;/**是否允许后台自动挂波次*/
            allowSectorInvasion = true;/**是否允许区块被敌人进攻*/

            allowLaunchSchematics = true;/**是否允许使用核心蓝图 */
            allowLaunchLoadout = true;/**是否允许发射时携带物资 */
            defaultCore= IceBlocks.fleshAndBloodhinge;

            iconColor = Color.valueOf("ff7070");/**星球面板(PlanetDialog)上的颜色*/
            enemyCoreSpawnReplace = true;/** 敌人最后的核心被摧毁后是否要生成一个出怪点  */


            ruleSetter = (R) -> {/** 当区块地图加载时会自动将地图规则作以下调整  */
                R.loadout = new Seq<>(ItemStack.with(Items.copper, 12, Items.graphite, 12));
                R.defaultTeam = Team.sharded;
                R.waveTeam = Team.crux;/** 敌方队伍  */
                R.coreIncinerates = true;/** 核心是否在满时焚烧物品，就像在战役中一样。*/
                R.placeRangeCheck = false;/**  恶心的建筑区  */
                R.showSpawns = false;/** 在地图上不显示出怪点  */
            };
            atmosphereColor = Color.valueOf("613C3CFF");/**环境色 */
            atmosphereRadIn = 0.02f;/** 大气层大气半径 */
            atmosphereRadOut = 0.3f;
            startSector = 1;/**开始区块(类似零号地区)*/
            alwaysUnlocked = true;/**是否默认解锁*/
            lightDstFrom = 0;/**日夜更周 */
            lightDstTo = 1f;
            lightSrcFrom = 0;
            lightSrcTo = 0.8f;
            updateLighting = true;/** 是否有昼夜更替   */
            tidalLock = false;/** 是否潮汐锁定(冷知识: erekir潮汐锁定)  */
            clearSectorOnLose = false;/** 区块输了是否重置区块(一次过是吧)  */
            enemyBuildSpeedMultiplier = 1;/**  敌人建筑倍率 这里不做处理 */
            landCloudColor = Color.valueOf("ffffff");/** 核心着陆烟尘的颜色  */
            prebuildBase = true;/**  是否需要像e星那样帅气地着陆建筑特效 */
             hiddenItems.add(Items.serpuloItems).add(Items.erekirItems);
        }};
    }
}