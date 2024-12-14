package ice.ui

import ice.Ice
import java.util.*

object DisplayName {
    var massageRand: String

    private val me = """
                你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频<ZL洋葱>
                *大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌<GRACHA>
                [#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]<维生素X>
                你要这样我可要宣传我模了<种余明的玉米>
                人生总有起落轻轻一笑,调整自我明天还是美好的<年年有鱼>
                JS异端<zxs>
                胡萝卜素星球<喵子>
                已经变成晓伟的形状了qwq
                界限?狗都不玩!<喵喵怪>
                你知道吗?点击这条消息可以切换下一条!
                """.trimIndent().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    init {
        Ice.ice.meta.displayName = "Deep Space"
        Ice.ice.meta.author = Ice.author.random()
        Ice.ice.meta.version = "0.12"
        Ice.ice.meta.java = true
        massageRand = me[Random().nextInt(me.size)]
        Ice.ice.meta.description = """
                一个多方位内容的模组,从星球到建筑,摒弃过量数值内容,争取用机制来减少同质化问题
                """.trimIndent()
    }

    fun flun() {
        massageRand = me[Random().nextInt(me.size)]
    }
}