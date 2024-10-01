package ice.Alon

import arc.Events
import ice.Alon.File.settings.textJava
import ice.Alon.Text.Text
import ice.Alon.async.CeProcess
import ice.Alon.content.IceItems
import ice.Alon.content.IceLiquids
import ice.Alon.content.IceStatus
import ice.Alon.content.IceUnitTypes
import ice.Alon.content.blocks.IceBlocks
import ice.Alon.library.drawUpdate.DrawUpdates.Companion.updateSeq
import ice.Alon.library.drawUpdate.DrawUpdates.DrawUpdate
import ice.Alon.library.tool.Tool
import ice.Alon.music.IceMusics
import ice.Alon.ui.Ui
import ice.Ice
import mindustry.Vars
import mindustry.game.EventType
import mindustry.world.meta.BuildVisibility

/**
 * 用于辅助加载content内容防止主类紊乱
 */
internal class AlonContentLoad {
    /**来点单例*/
    companion object Content {
        fun init() {
            Vars.asyncCore.processes.clear().add(CeProcess())
            Events.run(EventType.Trigger.draw) { updateSeq.each(DrawUpdate::draw) }
            Events.run(EventType.Trigger.update) { updateSeq.each(DrawUpdate::update) }
            Events.on(EventType.WorldLoadEndEvent::class.java) { updateSeq.each(DrawUpdate::init) }
            Ui.load()
            Tool.load()
        }

        fun load() {
            IceMusics.load()
            IceItems.load()
            IceLiquids.load()
            IceStatus.load()
            IceBlocks.load()
            IceUnitTypes.load()
            ice.Alon.asundry.Content.IceBlocks.load()
            Text("1223")
            Vars.content.blocks().select { it.minfo.mod === Ice.ice }.forEach {
                it.buildVisibility = BuildVisibility.shown
            }
            textJava.load()
        }
    }
}

