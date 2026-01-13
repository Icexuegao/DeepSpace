package singularity.world.modules

import arc.struct.ObjectSet
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.world.modules.BlockModule
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import singularity.world.distribution.request.DistRequestBase
import universecore.util.colletion.TreeSeq
import java.util.function.Function

class DistCoreModule(entity: DistElementBuildComp) : BlockModule() {
    protected var taskStack: Array<DistRequestBase> = arrayOfNulls<DistRequestBase>(16) as Array<DistRequestBase>
    protected var taskQueue: Array<DistRequestBase> = arrayOfNulls<DistRequestBase>(16) as Array<DistRequestBase>
    var requestTasks: TreeSeq<DistRequestBase?> = TreeSeq<DistRequestBase?>(Comparator { a: DistRequestBase?, b: DistRequestBase? -> b!!.priority() - a!!.priority() })
    protected var queueLength: Int = 0
    var lastProcessed: Int = 0
    var calculatePower: Int = 0
    var executingAddress: Int = 0
    val core: DistNetworkCoreComp
    var process: Float = 0f

    init {
        core = entity as DistNetworkCoreComp
    }

    fun update() {
        val network = core.distributor()!!.network

        if (network.netValid()) {
            process += Time.delta * network.netEfficiency()
        }

        while (process >= 1) {
            process -= 1f

            lastProcessed = 0
            queueLength = 0
            blocked.clear()
            var runCounter = calculatePower
            if (requestTasks.removeIf(
                    Function { task: DistRequestBase? -> task!!.finished() || task.sender.distributor()!!.network !== network }
                )
            ) taskStack = requestTasks.toArray(EMP_TMP) as Array<DistRequestBase>

            if (!requestTasks.isEmpty()) {
                var firstAddress = -1
                while (runCounter > 0) {
                    executingAddress = (executingAddress + 1) % requestTasks.size()
                    if (executingAddress == firstAddress) break
                    if (firstAddress == -1) firstAddress = executingAddress

                    if (taskQueue.size <= queueLength) taskQueue = taskQueue.copyOf(taskQueue.size * 2) as Array<DistRequestBase>

                    taskQueue[queueLength] = taskStack[executingAddress]!!
                    queueLength++

                    taskStack[executingAddress]!!.onExecute()
                    taskStack[executingAddress]!!.checkWaking()
                    runCounter--
                    lastProcessed++
                }
            }

            for (i in 0..<queueLength) {
                val request = taskQueue[i]
                request.checkStatus()

                if (!request.sleeping()) {
                    if (!request.preHandle()) {
                        blocked.add(request)
                        request.block(true)
                    } else request.block(false)
                }
            }

            for (i in 0..<queueLength) {
                val request = taskQueue[i]
                if (!request.sleeping() && !blocked.contains(request)) {
                    if (!request.handle()) {
                        blocked.add(request)
                        request.block(true)
                    } else request.block(false)
                }
            }

            for (buffer in core.buffers()!!.values()) {
                buffer.bufferContAssign(network)
                buffer.update()
            }

            for (i in 0..<queueLength) {
                val request = taskQueue[i]
                if (!request.sleeping() && !blocked.contains(request)) {
                    request.block(!request.afterHandle())
                }
            }
        }

        for (i in 0..<queueLength) {
            val req = taskQueue[i]
            req.resetCallBack()
        }
    }

    fun receive(request: DistRequestBase?) {
        requestTasks.add(request)
        taskStack = requestTasks.toArray(EMPTY) as Array<DistRequestBase>
    }

    override fun read(read: Reads?) {
    }

    override fun write(write: Writes?) {
    }

    companion object {
        private val blocked = ObjectSet<DistRequestBase?>()
        val EMP_TMP: Array<DistRequestBase?> = arrayOfNulls<DistRequestBase>(0)
        val EMPTY: Array<DistRequestBase?> = arrayOfNulls<DistRequestBase>(0)
    }
}