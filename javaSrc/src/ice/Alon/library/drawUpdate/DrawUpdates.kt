package ice.Alon.library.drawUpdate

import arc.struct.Seq

class DrawUpdates {
    companion object {
        /**
         * 绘画数组 需要draw 或者Update的对象 添加 实现Draw接口重写即可
         */
        var updateSeq = Seq<DrawUpdate>()
    }

    interface DrawUpdate {
        /**
         * 进入游戏后开始Draw
         */
        fun draw(){}

        /**
         * 局内init
         */
        fun init() {}

        /**
         * 全局update如果要在局内使用记得判断
         */
        fun update() {}
    }
}