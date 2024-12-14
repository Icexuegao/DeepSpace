package ice.library.drawUpdate

import arc.Events
import arc.struct.Seq
import mindustry.game.EventType

object DrawUpdates {
    /**
     * 绘画数组 需要draw 或者Update的对象 添加 实现Draw接口重写即可
     */
    val updateSeq = Seq<DrawUpdate>()
    fun load() {
        //游戏退出时删除游戏内逻辑
        Events.on(ice.game.EventType.SaveExitEvent::class.java) { updateSeq.each { if (!it.overall) it.kill() } }
        Events.run(EventType.Trigger.draw) { updateSeq.each(DrawUpdate::draw) }
        Events.run(EventType.Trigger.update) { updateSeq.each(DrawUpdate::update) }
    }
}