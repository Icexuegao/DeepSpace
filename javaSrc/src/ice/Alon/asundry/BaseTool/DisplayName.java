package ice.Alon.asundry.BaseTool;

import ice.Ice;
import mindustry.Vars;
import mindustry.mod.Mods;

import java.util.Random;


public class DisplayName {
    public static String massageRand;
    public static Mods.LoadedMod mod;

    public static void displayName() {

        mod = Vars.mods.getMod(Ice.class);
        String[] me = """                  
                你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频<ZL洋葱>@#@
                *大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌<GRACHA>@#@
                [#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]<维生素X>@#@
                你要这样我可要宣传我模了<种余明的玉米>@#@
                人生总有起落轻轻一笑,调整自我明天还是美好的<年年有鱼>@#@
                JS异端<zxs>@#@
                胡萝卜素星球<喵子>@#@
                已经变成晓伟的形状了qwq@#@
                界限?狗都不玩!<喵喵怪>
                """.split("@#@");
        massageRand = me[new Random().nextInt(me.length - 1)];
        mod.meta.displayName = Ice.displayName + " - " + massageRand;
        /*  mod.meta.displayName = Display_Name + " - " + me[me.length - 1];*/
        mod.meta.description = """
                a                aa
                   a             a
                        a        aaaa
                """;


    }
}
