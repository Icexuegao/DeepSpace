package universecore.world.consumers

import arc.struct.Seq
import mindustry.ctype.ContentType

@SuppressWarnings("unchecked")
open class ConsumeType<T : BaseConsume<*>> {
    companion object {
        private val allType: Seq<ConsumeType<*>> = Seq()

        fun all(): Array<ConsumeType<*>> = allType.toArray(ConsumeType::class.java)

        fun <Type : BaseConsume<*>> add(type: Class<Type>, cType: ContentType?): ConsumeType<out Type> {
            return ConsumeType(type, cType)
        }

        val power: ConsumeType<ConsumePower<*>> = add(ConsumePower::class.java, null) as ConsumeType<ConsumePower<*>>
        val item: ConsumeType<ConsumeItemBase<*>> = add(ConsumeItemBase::class.java, ContentType.item) as ConsumeType<ConsumeItemBase<*>>
        val liquid: ConsumeType<ConsumeLiquidBase<*>> = add(ConsumeLiquidBase::class.java, ContentType.liquid) as ConsumeType<ConsumeLiquidBase<*>>
        val payload: ConsumeType<ConsumePayload<*>> = add(ConsumePayload::class.java, null) as ConsumeType<ConsumePayload<*>>
    }

    private val id: Int
    private val type: Class<T>
    private val contType: ContentType?

    constructor(type: Class<T>, cType: ContentType?) {
        id = allType.size
        this.type = type
        contType = cType
        allType.add(this)
    }

    fun getType(): Class<T> = type

    fun cType(): ContentType? = contType

    fun id(): Int = id


}
