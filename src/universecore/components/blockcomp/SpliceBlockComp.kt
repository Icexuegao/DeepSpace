package universecore.components.blockcomp

/**拼接方块的建筑组件，记录一些[拼接方块建筑][SpliceBuildComp]必须的属性
 *
 * **这是个不稳定的API，后续可能会调整为更加通用且高效的形式，这会造成API变更，慎用**
 *
 * @since 1.5
 * @author EBwilson
 */
interface SpliceBlockComp : ChainsBlockComp {
    //@Annotations.BindField("interCorner")
    var  interCorner: Boolean

    // @Annotations.BindField("negativeSplice")
    var negativeSplice: Boolean
}