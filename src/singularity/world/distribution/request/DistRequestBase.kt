package singularity.world.distribution.request

import arc.Core
import arc.func.Boolf
import arc.func.Boolp
import arc.util.Nullable
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistributeNetwork

abstract class DistRequestBase(val sender: DistElementBuildComp) {
    private var updateMark: Long = 0
    private var executeMark: Long = 0
    protected var initialized: Boolean = false
    protected var sleeping: Boolean = false
    protected var killed: Boolean = false
    var isBlocked: Boolean = false
        protected set
    var waker: Boolf<out DistElementBuildComp?>? = null
    var target: DistributeNetwork? = null

    @Nullable
    var preHandleCallback: RequestTask? = null

    @Nullable
    var handleCallBack: RequestTask? = null

    @Nullable
    var afterHandleCallBack: RequestTask? = null


    open fun priority(): Int {
        return 0
    }

    fun finished(): Boolean {
        return killed || !sender.building.isAdded()
    }

    fun sleeping(): Boolean {
        return sleeping || updateMark < Core.graphics.getFrameId() - 1
    }

    fun block(blocked: Boolean) {
        this.isBlocked = blocked
    }

    fun executing(): Boolean {
        return executeMark == Core.graphics.getFrameId()
    }

    fun kill() {
        killed = true
    }

    fun sleep() {
        sleeping = true
    }

    fun weak() {
        sleeping = false
    }

    open fun init(target: DistributeNetwork) {
        this.target = target
        initialized = true
    }

    fun update(handle: RequestTask?) {
        update(null, handle, null)
    }

    fun update(pre: RequestTask?, after: RequestTask?) {
        update(pre, null, after)
    }

    fun update(pre: RequestTask?, handle: RequestTask?, after: RequestTask?) {
        preHandleCallback = pre
        handleCallBack = handle
        afterHandleCallBack = after
        update()
    }

    fun checkWaking() {
        if (waker != null) {
            if ((waker as Boolf<DistElementBuildComp?>).get(sender)) {
                if (sleeping) weak()
            } else if (!sleeping) sleep()
        }
    }

    fun update() {
        updateMark = Core.graphics.getFrameId()
    }

    fun checkStatus() {
        if (!initialized) throw RequestStatusException("handle a uninitialized request")
        if (killed) {
            throw RequestStatusException("handle a death request")
        }
    }

    fun preHandle(): Boolean {
        if (preHandleCallback != null) {
            return preHandleCallback!!.run(Boolp { this.preHandleTask() })
        } else return preHandleTask()
    }

    fun handle(): Boolean {
        if (handleCallBack != null) {
            return handleCallBack!!.run(Boolp { this.handleTask() })
        } else return handleTask()
    }

    fun afterHandle(): Boolean {
        if (afterHandleCallBack != null) {
            return afterHandleCallBack!!.run(Boolp { this.afterHandleTask() })
        } else return afterHandleTask()
    }

    protected abstract fun preHandleTask(): Boolean
    protected abstract fun handleTask(): Boolean
    protected abstract fun afterHandleTask(): Boolean

    fun resetCallBack() {
        preHandleCallback = null
        handleCallBack = null
        afterHandleCallBack = null
    }

    fun onExecute() {
        executeMark = Core.graphics.getFrameId()
    }

    class RequestStatusException(info: String?) : RuntimeException(info)

    interface RequestTask {
        fun run(callTask: Boolp?): Boolean
    }
}