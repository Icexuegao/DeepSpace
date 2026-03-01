package singularity.world.distribution.request

import arc.Core
import arc.func.Boolf
import arc.func.Boolp
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

  var preHandleCallback: RequestTask? = null
  var handleCallBack: RequestTask? = null
  var afterHandleCallBack: RequestTask? = null

  open fun priority() = 0

  fun finished(): Boolean {
    return killed || !sender.building.isAdded
  }

  fun sleeping(): Boolean {
    return sleeping || updateMark < Core.graphics.frameId - 1
  }

  fun block(blocked: Boolean) {
    this.isBlocked = blocked
  }

  fun executing(): Boolean {
    return executeMark == Core.graphics.frameId
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
    updateMark = Core.graphics.frameId
  }

  fun checkStatus() {
    if (!initialized) throw RequestStatusException("handle a uninitialized request")
    if (killed) {
      throw RequestStatusException("handle a death request")
    }
  }

  fun preHandle(): Boolean {
    if (preHandleCallback != null) {
      return preHandleCallback!!.run { this.preHandleTask() }
    } else return preHandleTask()
  }

  fun handle(): Boolean {
    if (handleCallBack != null) {
      return handleCallBack!!.run { this.handleTask() }
    } else return handleTask()
  }

  fun afterHandle(): Boolean {
    if (afterHandleCallBack != null) {
      return afterHandleCallBack!!.run { this.afterHandleTask() }
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

  fun interface RequestTask {
    fun run(callTask: Boolp): Boolean
  }
}