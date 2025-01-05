package ice.ui.menus

import arc.scene.ui.layout.Table
import ice.music.IceMusics

object ThankDialog {
    private val me = """
                你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频<ZL洋葱>
                *大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌<GRACHA>
                [#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]<维生素X>
                你要这样我可要宣传我模了<种余明的玉米>
                人生总有起落轻轻一笑,调整自我明天还是美好的<年年有鱼>
                JS异端<zxs>
                [#00F7FFFF]你说的对[][#FF0000FF]后面忘了...[]<试听>
                胡萝卜素星球<喵子>
                已经变成晓伟的形状了qwq
                愚昧的活着不如清醒的死去<[#FFFFFF]H-O-O-H[][#000000]H-O-O-H[]>
                界限?狗都不玩!<喵喵怪>
                你知道吗?点击这条消息可以切换下一条!
                有机会也试试[#F6A34FFF]战锤[]这个模组吖<坠机的牢阔>
                """.trimIndent().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    fun set(table: Table) {
        rebuild(table)
        IceMusics.toggle("thanks")
    }

    private fun rebuild(table: Table) {
        table.table { t1->
            me.forEach {
                t1.add(it).row()
            }
        }.grow()
    }
}