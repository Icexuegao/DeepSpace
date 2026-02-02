package singularity.world.blocks.distribute.matrixGrid

import arc.func.Boolf
import arc.func.Boolp
import arc.struct.IntMap
import arc.struct.ObjectSet
import arc.struct.Seq
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.type.Item
import mindustry.type.ItemSeq
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import singularity.world.blocks.distribute.GenericIOPoint.GenericIOPPointBuild
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistMatrixUnitBuildComp

import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.buffers.ItemsBuffer.ItemPacket
import singularity.world.distribution.buffers.LiquidsBuffer
import singularity.world.distribution.buffers.LiquidsBuffer.LiquidPacket
import singularity.world.distribution.request.*
import java.util.*
import kotlin.math.min

/**包含了用于矩阵网格发出网络请求的一些辅助类 */
class RequestHandlers {
  /**矩阵网格发出网络请求使用的辅助类接口，为矩阵网格提供将配置转化为网络请求的帮助 */
  interface RequestHandler<R : DistRequestBase?> {
    /**添加一个要分析的io配置，所有配置项取求和
     * @param cfg 添加的配置项
     */
    fun addParseConfig(cfg: TargetConfigure)

    /**由标记的配置生成网络请求
     * @param sender 此请求的目的发出者
     */
    fun makeRequest(sender: DistMatrixUnitBuildComp): R?

    /**重置所有已添加的配置缓存 */
    fun reset(sender: DistMatrixUnitBuildComp)

    /**请求的处理前回调方法 */
    fun preCallBack(sender: DistMatrixUnitBuildComp?, request: R, task: Boolp): Boolean {
      return task.get()
    }

    fun callBack(sender: DistMatrixUnitBuildComp, request: R, task: Boolp): Boolean {
      return task.get()
    }

    fun afterCallBack(sender: DistMatrixUnitBuildComp?, request: R, task: Boolp): Boolean {
      return task.get()
    }
  }

  abstract class AbstractItemRequestHandler {
    protected var items: ItemSeq = ItemSeq()

    fun reset(sender: DistMatrixUnitBuildComp) {
      items.clear()

      val coreBuff: ItemsBuffer = sender.distributor.core()?.getBuffer(DistBufferType.itemBuffer)?:return
      for (packet in sender.getBuffer(DistBufferType.itemBuffer)!!) {
        val move = min(coreBuff.remainingCapacity(), packet.amount())

        if (move <= 0) continue
        packet.remove(move)
        coreBuff.put(packet.get(), move)

        coreBuff.bufferContAssign(sender.distributor.network, packet.get()!!, move)
      }
    }
  }

  abstract class AbstractLiquidRequestHandler {
    protected var liquids: IntMap<LiquidStack> = IntMap<LiquidStack>()
    protected var total: Float = 0f

    fun reset(sender: DistMatrixUnitBuildComp) {
      liquids.clear()
      total = 0f

      val coreBuff: LiquidsBuffer = sender.distributor.core().getBuffer(DistBufferType.liquidBuffer)
      for (packet in sender.getBuffer(DistBufferType.liquidBuffer)!!) {
        val move = min(coreBuff.remainingCapacity(), packet.amount())

        if (move <= 0) continue
        packet.remove(move)
        coreBuff.put(packet.get(), move)

        coreBuff.bufferContAssign(sender.distributor.network, packet.get(), move)
      }
    }

    protected fun addParseConfig(cfg: TargetConfigure, type: GridChildType?) {
      for (liquid in cfg.get(type, ContentType.liquid)!!) {
        val stack = liquids.get(liquid.id.toInt())
        if (stack == null) {
          liquids.put(liquid.id.toInt(), LiquidStack(liquid as Liquid, 1f))
        } else stack.amount += 1f
        total += 1f
      }
    }

    protected fun handleToOne() {
      for (stack in liquids.values()) {
        stack.amount /= total
      }
    }
  }

  /*
  * items
  * */
  class AcceptItemRequestHandler : AbstractItemRequestHandler(), RequestHandler<PutItemsRequest> {
    override fun addParseConfig(cfg: TargetConfigure) {
      for (item in cfg.get(GridChildType.acceptor, ContentType.item)!!) {
        items.add(item as Item, 1)
      }
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): PutItemsRequest? {
      return if (items.toSeq().isEmpty) null else PutItemsRequest(sender, sender.getBuffer(DistBufferType.itemBuffer)!!, items.toSeq())
    }
  }

  class PutItemRequestHandler : AbstractItemRequestHandler(), RequestHandler<PutItemsRequest> {
    override fun addParseConfig(cfg: TargetConfigure) {
      for (item in cfg.get(GridChildType.input, ContentType.item)!!) {
        items.add(item as Item, 1)
      }
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): PutItemsRequest? {
      return if (items.toSeq().isEmpty) null else PutItemsRequest(sender, sender.getBuffer(DistBufferType.itemBuffer)!!, items.toSeq())
    }
  }

  class ReadItemRequestHandler : AbstractItemRequestHandler(), RequestHandler<ReadItemsRequest> {
    override fun addParseConfig(cfg: TargetConfigure) {
      for (item in cfg.get(GridChildType.output, ContentType.item)!!) {
        items.add(item as Item, 1)
      }
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): ReadItemsRequest? {
      val seq = items.toSeq()
      val result = if (seq.isEmpty()) null else ReadItemsRequest(sender, sender.getBuffer(DistBufferType.itemBuffer)!!, seq)
      if (result == null) return null
      result.waker = Boolf { e: DistMatrixUnitBuildComp ->
        for (point in e.ioPoints) {
          for (stack in seq) {
            if (point.valid(e, GridChildType.output, stack.item)) return@Boolf true
          }
        }
        false
      }
      return result
    }

    private val tmp = IntArray(Vars.content.items().size)
    private val tmpItems = ObjectSet<Item?>()

    override fun callBack(sender: DistMatrixUnitBuildComp, request: ReadItemsRequest, task: Boolp): Boolean {
      Arrays.fill(tmp, 0)
      tmpItems.clear()

      val buffer: ItemsBuffer = sender.getBuffer(DistBufferType.itemBuffer)!!
      for (packet in buffer) {
        tmp[packet.id()] = packet.amount()
        tmpItems.add(packet.get())
      }

      val taskStatus = task.get()

      for (entry in sender.matrixGrid.get<GenericIOPPointBuild>(GridChildType.output) { e, c -> e is GenericIOPPointBuild }) {
        if (entry.config == null) continue
        val ioPoint = entry.entity as GenericIOPPointBuild
        if (ioPoint.config == null) continue
        for (item in ioPoint.config!!.get(GridChildType.output, ContentType.item)!!) {
          if (buffer.get(item as Item) > 0 && ioPoint.acceptItemOut(sender.building, item)) {
            val amount = ioPoint.output(item, 1)
            if (amount == 0) continue
            buffer.remove(item, 1)
            buffer.deReadFlow(item, 1)

            tmpItems.add(item)
          }
        }
      }

      val coreBuffer: ItemsBuffer = sender.distributor.core().getBuffer(DistBufferType.itemBuffer)
      for (id in tmp.indices) {
        val packet = buffer.get<ItemPacket>(id)
        if (packet != null) {
          if (!tmpItems.contains(packet.get())) continue
          var transBack = min(packet.amount() - tmp[id], coreBuffer.remainingCapacity())
          transBack = (transBack - transBack % LiquidsBuffer.LiquidIntegerStack.packMulti).toInt()
          if (transBack <= 0) continue

          packet.remove(transBack)
          packet.dePut(transBack)
          packet.deRead(transBack)
          coreBuffer.put(packet.get(), transBack)
          coreBuffer.dePutFlow(packet.get(), transBack)
          coreBuffer.deReadFlow(packet.get(), transBack)

          coreBuffer.bufferContAssign(sender.distributor.network, packet.get()!!, transBack, true)
        }
      }

      return taskStatus
    }
  }

  /*
  * liquids
  * */
  class AcceptLiquidRequestHandler : AbstractLiquidRequestHandler(), RequestHandler<PutLiquidsRequest?> {
    override fun addParseConfig(cfg: TargetConfigure) {
      addParseConfig(cfg, GridChildType.acceptor)
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): PutLiquidsRequest? {
      handleToOne()
      return if (liquids.isEmpty()) null else PutLiquidsRequest(sender, sender.getBuffer(DistBufferType.liquidBuffer)!!, Seq(liquids.values().toArray()))
    }
  }

  class PutLiquidRequestHandler : AbstractLiquidRequestHandler(), RequestHandler<PutLiquidsRequest?> {
    override fun addParseConfig(cfg: TargetConfigure) {
      addParseConfig(cfg, GridChildType.input)
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): PutLiquidsRequest? {
      handleToOne()
      return if (liquids.isEmpty()) null else PutLiquidsRequest(sender, sender.getBuffer(DistBufferType.liquidBuffer)!!, Seq(liquids.values().toArray()))
    }
  }

  class ReadLiquidRequestHandler : AbstractLiquidRequestHandler(), RequestHandler<ReadLiquidsRequest?> {
    override fun addParseConfig(cfg: TargetConfigure) {
      addParseConfig(cfg, GridChildType.output)
    }

    override fun makeRequest(sender: DistMatrixUnitBuildComp): ReadLiquidsRequest? {
      handleToOne()
      val seq = Seq<LiquidStack>(liquids.values().toArray())

      val result = if (liquids.isEmpty()) null else ReadLiquidsRequest(sender, sender.getBuffer(DistBufferType.liquidBuffer)!!, seq)
      if (result == null) return null
      result.waker = Boolf { e: DistMatrixUnitBuildComp? ->
        for (point in e!!.ioPoints) {
          for (stack in seq) {
            if (point.valid(e, GridChildType.output, stack.liquid)) return@Boolf true
          }
        }
        false
      }
      return result
    }

    private val tmp = FloatArray(Vars.content.liquids().size)
    private val tmpLiquids = ObjectSet<Liquid?>()

    override fun callBack(sender: DistMatrixUnitBuildComp, request: ReadLiquidsRequest?, task: Boolp): Boolean {
      Arrays.fill(tmp, 0f)
      tmpLiquids.clear()

      val buffer: LiquidsBuffer = sender.getBuffer(DistBufferType.liquidBuffer)!!
      for (packet in buffer) {
        tmp[packet.id()] = packet.amount()
        tmpLiquids.add(packet.get())
      }

      val taskStatus = task.get()

      for (entry in sender.matrixGrid.get<GenericIOPPointBuild>(GridChildType.output, { e, c -> e is GenericIOPPointBuild })) {
        if (entry.config == null) continue
        val ioPoint = entry.entity as GenericIOPPointBuild
        if (ioPoint.config == null) continue
        for (liquid in ioPoint.config!!.get(GridChildType.output, ContentType.liquid)!!) {
          val li = liquid as Liquid
          if (buffer.get(li) > 0 && ioPoint.acceptLiquidOut(sender.building, li)) {
            val all: Int = sender.matrixGrid.get<GenericIOPPointBuild>(GridChildType.output) { e, c -> c!!.get(GridChildType.output, liquid) }.size

            var amount = ioPoint.output(li, buffer.get(li) / all)
            amount -= amount % LiquidsBuffer.LiquidIntegerStack.packMulti
            if (amount <= 0) continue
            buffer.remove(li, amount)
            buffer.deReadFlow(li, amount)

            tmpLiquids.add(li)
          }
        }
      }

      val coreBuffer: LiquidsBuffer = sender.distributor.core().getBuffer(DistBufferType.liquidBuffer)
      for (id in tmp.indices) {
        val packet = buffer.get<LiquidPacket>(id)
        if (packet != null) {
          if (!tmpLiquids.contains(packet.get())) continue
          var transBack = min(packet.amount() - tmp[id], coreBuffer.remainingCapacity())
          transBack -= transBack % LiquidsBuffer.LiquidIntegerStack.packMulti
          if (transBack <= 0) continue

          packet.remove(transBack)
          packet.dePut(transBack)
          packet.deRead(transBack)
          coreBuffer.put(packet.get(), transBack)
          coreBuffer.dePutFlow(packet.get(), transBack)
          coreBuffer.deReadFlow(packet.get(), transBack)

          coreBuffer.bufferContAssign(sender.distributor.network, packet.get(), transBack, true)
        }
      }

      return taskStatus
    }
  }
}