package Iceconent.content;

import mindustry.type.SectorPreset;

public class IceSectorPresets {
    public static SectorPreset s1;

    public static void load() {
        s1 = new SectorPreset("huoshan", IcePlanet.aDri, 272) {{
            alwaysUnlocked = true;/** 始终解锁  */
            addStartingItems = true;/** //添加起始物资  */
            overrideLaunchDefaults = true;/** 如果为 true，则改为使用该扇区的启动字段 */
            noLighting = true;/**  //无照明 */
            difficulty = 5;/** //难度  */
            startWaveTimeMultiplier = 1f;/**每次波长时间倍数*/
        }};
    }
}
