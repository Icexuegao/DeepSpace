package ice.library.drawUpdate

import ice.library.drawUpdate.DrawUpdates.updateSeq

open class DrawUpdate {
    /**是否是全局逻辑*/
    var overall = false

    init {
        updateSeq.add(this)
    }

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