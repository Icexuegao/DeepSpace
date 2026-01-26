package singularity.world.components.distnet

import arc.struct.ObjectMap
import arc.struct.OrderedSet
import mindustry.ctype.ContentType
import mindustry.world.Block
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers
import singularity.world.distribution.GridChildType

interface IOPointBlockComp {
  // @Annotations.BindField(value = "requestFactories", initialize = "new arc.struct.ObjectMap<>()")
  var requestFactories: ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>>
  var configTypes: OrderedSet<GridChildType>
  var supportContentType: OrderedSet<ContentType>

  fun setFactory(type: GridChildType?, contType: ContentType?, factory: RequestHandlers.RequestHandler<*>?) {
    requestFactories.get(type) { ObjectMap() }.put(contType, factory)
    configTypes.add(type)
    supportContentType.add(contType)
  }

  val block: Block
    get() = this as Block
}