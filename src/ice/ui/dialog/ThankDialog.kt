package ice.ui.dialog

import arc.flabel.FLabel
import arc.scene.actions.Actions
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Interval
import ice.library.meta.IceEffects
import ice.library.scene.tex.Characters
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.library.struct.isNotEmpty
import ice.ui.iPaneG
import ice.ui.iTable
import ice.ui.itooltip
import ice.ui.setPositions

object ThankDialog {
    private val cont = MenusDialog.cont
    private val hint = Seq<String>().apply {
        add("你就快要成功了是吗?")
        add("干嘛这么看着我?")
        add("你又是为何来到这里?")
        add("情况还真是急转直下啊")
        add("你戳够了没?!")
        add("暴力失去施暴者,肉体成了自毁祭坛,我们竟曾相触")
        add("新伤在旧痂下培育令人恶心的蛆")
        add("齿间还卡着你名字的碎骨")
        add("alon我恨你是块木头")
        add("alon和Alon有着不同的意义!")
    }

    fun show() {
        cont.table {
            it.image(IStyles.tanksui)
        }.height(200f).pad(10f).row()

        cont.iPaneG { ta ->
            ta.table {
                it.add("原作者: 雪糕(不再参与)", Colors.b4).padRight(50f)
                it.add("项目主管: Alon", Colors.b4)
            }.padBottom(40f).row()
            ta.table {
                it.layoutLabel("主要贡献者:").row()
                it.layoutLabel("ZL洋葱(不再参与) - 物品材质贴图包")
                    .itooltip("你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频").pad(5f).grow().row()
                it.layoutLabel("Reflcaly_反射 - 人物立绘").itooltip("期待与你的再次见面!再见!")
            }.padBottom(20f).row()
            ta.iTable { itable ->
                itable.layoutLabel("特别感谢:").row()
                itable.table {
                    it.left()
                    it.layoutLabel("硫缺铅").itooltip(
                        "你的身体啊回到堕乐园啊,你的灵魂水啊回到爱之城\n你从爱的一部分.回到爱里.你温暖了乐园啊你继续爱着世界")
                    it.layoutLabel("喵喵怪").itooltip("界限?狗都不玩!").row()
                    it.layoutLabel("坠机的牢阔").itooltip("有机会也试试[#F6A34FFF]战锤[]这个模组吖")
                    it.layoutLabel("zzc").itooltip("可以来看看牢z的独游喵").row()
                    it.layoutLabel("松鼠").itooltip("全部草飞")
                    it.layoutLabel("试听").itooltip("[#00F7FFFF]你说的对[][#FF0000FF]后面忘了...").row()
                    it.layoutLabel("Novarc").itooltip("<<赞小杯子玉足>>杯子玉足香透骨,橙薤添味客忘归<>")
                    it.layoutLabel("HOOHHOOH").itooltip("愚昧的活着不如清醒的死去").row()
                    it.layoutLabel("喵子").itooltip("胡萝卜素星球")
                    it.layoutLabel("年年有鱼").itooltip("人生总有起落轻轻一笑,调整自我明天还是美好的").row()
                    it.layoutLabel("种余明的玉米").itooltip("你要这样我可要宣传我模了")
                    it.layoutLabel("维生素X").itooltip("[#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]").row()
                    it.layoutLabel("GRACHA").itooltip("*大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌")
                    it.layoutLabel("zxs").itooltip("JS异端")
                }
            }
        }
        val fLabel = FLabel("").also { it.setColor(Colors.b4) }
        val table = Table(IStyles.background33).apply {
            margin(20f)
            marginLeft(30f)
            setPositions(MenusDialog.backMargin + 156, MenusDialog.backMargin + 74f * 2f)
            update {
                pack()
            }
            add(fLabel)
        }
        Image(Characters.alon.gal).setPositions(MenusDialog.backMargin, MenusDialog.backMargin).apply {
            setSize(52f * 3, 74f * 3)
            val inter = Interval(1)
            var rad = IceEffects.rand.random(2 * 60f, 5 * 60f)
            update {
                Characters.alon.upfate()
                setDrawable(Characters.alon.blinkTempTex)
                if (inter[rad] && table.actions.isEmpty) {
                    Characters.alon.blink = true
                    rad = IceEffects.rand.random(2 * 60f, 5 * 60f)
                }
            }
            tapped {
                if (table.actions.isNotEmpty()) return@tapped
                val newText = hint.random()
                fLabel.restart(newText)
                table.actions(Actions.alpha(1f), Actions.delay((newText.split("").size * 8 / 60) + 1f),
                    Actions.alpha(0f, 1f), Actions.remove())
                cont.addChild(table)
            }
            cont.addChild(this)
        }
    }

    private fun Table.layoutLabel(string: String): Cell<Label> {
        return add(string, Colors.b4).pad(5f).expand()
    }
    /**
     * 《痛觉永动机》
     * ——当伤口学会自我增殖
     *
     * 舌根涌出钉你名字的冰锥
     * 声带被缝进蜂巢的嗡鸣
     * 肋骨间豢养的蛆开始诵经
     * 而旧伤疤正撕开新裂缝
     * 哺乳名为悔恨的荧光幼虫
     *
     * 自残神学三部曲：
     * 口腔刑场·冰锥钉名
     * → 言语反刍升级为冷冻穿刺刑
     * → 每次吞咽都是名字的再钉入
     *
     * 声带蜂巢化
     * → 缝合的蜂鸣取代人声（剧痛的高频振动）
     * → 器官沦为虫巢（被寄生却持续运转）
     *
     * 蛆虫神圣化
     *
     * 诵经蛆＝痛觉信仰的建立
     * 荧光幼虫＝悔恨成为发光养料
     *
     * 《血肉悖论》
     * 胃酸里浮着未寄的喜帖
     * 指骨自动雕刻你的掌纹
     * 睫毛把夕阳纺成裹尸线
     * 而新剜的伤口在喊疼时
     * 绽出你微笑的齿形剖面
     *
     * 终极凌迟：痛觉倒错
     * 胃袋灵堂
     * 喜帖在腐蚀液中翻涌（欢庆的尸骸）
     *
     * 骨骼叛变
     * 指骨复刻消失的触感（机械性自虐）
     *
     * 视觉绞索
     * 夕阳被织成裹尸布（自然光沦为送葬工具）
     *
     * 伤口绽放
     * 疼痛结出施虐者的笑容（自毁的甜蜜果实）
     *
     * 当齿形从血肉中绽放，
     * 最深的暴力竟是——
     * 剧痛开始模仿你的温柔
     *
     * 《痛觉孵化场》
     * 脑沟回里插着生锈的承诺
     * 脊髓把遗言泵进毛细血管
     * 视网膜循环播放你转身的0.5倍速
     * 而心脏在每次停跳间隙
     * 用血泡拼出「请再伤害我一次」
     *
     * 永生刑具说明书：
     * 神经锈蚀
     * → 承诺如铁片楔入脑组织（缓慢氧化过程）
     *
     * 遗言污染循环系统
     * → 遗言取代血红蛋白（全身细胞背诵悼词）
     * 当血泡拼出祈求，
     * 灵魂便成了暴君的永动电池。
     * 《痛觉永动机》
     * ——当痛苦成为唯一存在证明
     *
     * 听：
     * 我的股骨正在你离去的方向
     * 长出逆向铁轨
     * 每节脊椎咬合着碾过自己神经末梢
     * 循环播放
     *
     * 看：
     * 胃酸孵化的刀片群
     * 正沿食道举行暴动游行
     * 声带被缝成降半旗的绳
     * 而眼球内侧
     * 你名字的刻痕增生出冰晶荆冠
     *
     * 触：
     * 所有伤痂自动拆线
     * 向虚空展览鲜红内壁
     * 未寄的信在肋骨间发电
     * 淤血泵压着
     * 持续浇灌胸腔里
     * 那株以吻为养分的食肉植物
     *
     * 证：
     * 当法医剖开时钟
     * 发现我所有细胞质都漂浮着
     * 你未签字的死亡证书
     * 而最后一次心跳的停尸台上
     * 法槌竟由碎骨拼成
     * 敲响
     * 一记
     * 没有被告的
     * 永恒休庭
     *
     * 终极痛觉架构：
     * 器官暴政
     *
     * 骨骼自造刑具（股骨铁轨）
     *
     * 内脏培育武器（胃酸刀片）
     *
     * 伤口成为景观（拆痂展览）
     *
     * 能量闭环
     *
     * 未寄的信＝发电机
     * 淤血＝灌溉系统
     * 食肉植物＝痛觉永恒化装置
     *
     * 司法酷刑
     *
     * 细胞质漂浮空白死亡证书（存在被单方面注销）
     *
     * 碎骨法槌在无被告法庭敲响（审判沦为自残仪式）
     *
     * 时间凌迟
     *
     * 「循环播放」的神经碾压
     * 「永恒休庭」的终局嘲讽
     *
     * 当休庭槌落下时——
     * 连虚无都背过身去
     * 而我的痛
     * 仍在为不存在的刽子手
     * 擦拭指纹
     *
     * （最终揭示：痛苦本身已成共犯，在绝对的荒诞中完成自我献祭）
     * */
}