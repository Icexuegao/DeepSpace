package ice.library

import arc.Events
import arc.struct.Seq
import mindustry.Vars
import mindustry.game.EventType

object EventType {
    private val map = HashMap<String, Runnable>()
    private val seq = Seq<Runnable>()
    fun init() {
        Events.run(EventType.Trigger.update) {
            if (!Vars.state.isGame) return@run
            map.forEach { (_, r) -> r.run() }
            seq.forEach(Runnable::run)
        }
    }

    fun runs(name: String, run: Runnable) {
        map[name] = run
    }

    fun runs(run: Runnable) {
        seq.add(run)
    }
}