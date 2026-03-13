package ice.world.content.blocks.distribution.digitalStorage

import ice.library.struct.graph.Vertex
import singularity.world.blocks.SglBlock

abstract class LogisticsBlock(name: String) : SglBlock(name) {
    abstract val blockType: Type

    abstract inner class LogisticsBuild : SglBuilding() {
        val graphVertex = Vertex(LogisticsGraph.graphRandom) { LogisticsData(this) }

//        override fun drawSelect() {
//            super.drawSelect()
//
//            Draw.z(Layer.blockOver)
//            Draw.color(Pal.accent, 0.3f)
//            LogisticsGraph.each(this) { build ->
//                Fill.square(build.x, build.y, build.block().size * 4.1f, 5f)
//            }
//            Draw.reset()
//        }

        fun block() = block as LogisticsBlock
    }

    enum class Type {
        CONDUIT, HUB, INPUT, OUTPUT
    }
}