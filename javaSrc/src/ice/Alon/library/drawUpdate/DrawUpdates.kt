package ice.Alon.library.drawUpdate

import arc.Events
import arc.struct.Seq
import mindustry.game.EventType

class DrawUpdates {
    companion object {
        fun load() {
            Events.run(EventType.Trigger.draw) { updateSeq.each(DrawUpdate::draw) }
            Events.run(EventType.Trigger.update) { updateSeq.each(DrawUpdate::update) }
        }

        /**
         * 绘画数组 需要draw 或者Update的对象 添加 实现Draw接口重写即可
         */
        val updateSeq = Seq<DrawUpdate>()
    }

    open class DrawUpdate {
        open var overall = false

        init {
            updateSeq.add(this)
        }

        /**
         * 进入游戏后开始Draw
         */
        open fun draw() {}

        /**
         * 全局update如果要在局内使用记得判断overall
         */
        open fun update() {
        }

        open fun kill() {
            updateSeq.remove(this)
        }
    }
}