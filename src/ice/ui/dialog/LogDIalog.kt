package ice.ui.dialog

import ice.library.scene.tex.Colors

object LogDIalog {
    val cont = MenusDialog.cont
    fun show() {
        cont.add("距今约二十年前,一场毁灭性灾难席卷而来").color(Colors.b4).row()
        cont.add("北方极地突然涌现出诡异的红雾,并以不可阻挡之势向南蔓延.").color(Colors.b4).row()
        cont.add(
            "起初帝国当局选择漠视这一现象,直至红雾吞噬过半疆域才仓促应对,却为时已晚.所有进入红雾区域的人员皆在短时间内失联,再无音讯")
            .color(Colors.b4).row()
        cont.add("面对帝国的无能,枢机毅然率领追随者脱离统治,宣称获得神启,成功在红雾中开辟出纯净的庇护所.")
            .color(Colors.b4).row()
        cont.add("当流离失所的民众纷纷皈依这份新信仰时,帝国当局对此表达了强烈不满.").color(Colors.b4).row()
        cont.add(
            "命运的转折发生在某个平凡的日子,枢机亲自率领主教团寻访于我.可愿与我等同行?这简短的询问,这成为了我脱离帝国体制的最后契机")
            .color(Colors.b4).row()
        cont.add("如今,作为阿德里地区的主教,我肩负着净化异端的职责...").color(Colors.b4).row()
    }
}