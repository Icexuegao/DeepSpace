package ice.ui.scene.action

import arc.scene.Action
import arc.util.pooling.Pool


class UnitSpawnAction : Action() {
    var runnable: Runnable? = null
    private var ran = false
    override fun act(delta: Float): Boolean {
        if (!ran) {
            ran = true
            run()
        }
        return true
    }
    /** Called to run the runnable.  */
    fun run() {
        val pool: Pool<*> = pool
        setPool(null) // Ensure this action can't be returned to the pool inside the runnable.
        try {
            runnable!!.run()
        } finally {
            setPool(pool)
        }
    }
    override fun restart() {
        ran = false
    }
    override fun reset() {
        super.reset()
        runnable = null
    }
}