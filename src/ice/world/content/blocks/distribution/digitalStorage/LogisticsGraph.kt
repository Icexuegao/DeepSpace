package ice.world.content.blocks.distribution.digitalStorage

import arc.Events
import arc.math.Rand
import ice.library.struct.graph.Graph
import ice.library.struct.graph.Vertex
import mindustry.game.EventType
import mindustry.world.blocks.production.GenericCrafter

object LogisticsGraph {
    val graphRandom = Rand()

    private val crafterVertexes = HashMap<GenericCrafter.GenericCrafterBuild, Vertex<LogisticsData>>()
    private val graph = Graph(::LogisticsData)

    private fun ensureVertex(u: GenericCrafter.GenericCrafterBuild): Vertex<LogisticsData> {
        if (u in crafterVertexes) return crafterVertexes[u]!!

        val vertex = Vertex(graphRandom) { LogisticsData(crafter = u) }
        crafterVertexes[u] = vertex

        return vertex
    }

    fun link(u: LogisticsBlock.LogisticsBuild, v: LogisticsBlock.LogisticsBuild) {
        graph.link(u.graphVertex, v.graphVertex)
    }

    fun link(u: LogisticsBlock.LogisticsBuild, v: GenericCrafter.GenericCrafterBuild) {
        graph.link(u.graphVertex, ensureVertex(v))
    }

    fun cut(u: LogisticsBlock.LogisticsBuild, v: LogisticsBlock.LogisticsBuild) {
        graph.cut(u.graphVertex, v.graphVertex)
    }

    fun cut(u: LogisticsBlock.LogisticsBuild, v: GenericCrafter.GenericCrafterBuild) {
        val vVertex = crafterVertexes[v]!!
        val links = graph.ensureInfo(vVertex).links
        graph.cut(u.graphVertex, vVertex)
        if (links.isEmpty()) crafterVertexes -= v
    }

    fun connected(u: LogisticsBlock.LogisticsBuild, v: LogisticsBlock.LogisticsBuild) =
        graph.connected(u.graphVertex, v.graphVertex)

    fun connected(u: LogisticsBlock.LogisticsBuild, v: GenericCrafter.GenericCrafterBuild) =
        v in crafterVertexes && graph.connected(u.graphVertex, crafterVertexes[v]!!)

    fun getHub(u: LogisticsBlock.LogisticsBuild) = graph.ensureInfo(u.graphVertex)[0].node.root.data!!.userData!!.hub

    fun each(src: LogisticsBlock.LogisticsBuild, cons: (LogisticsBlock.LogisticsBuild) -> Unit) {
        val data = graph.ensureInfo(src.graphVertex)[0].node.root.data!!
        val userData = data.userData!!
        if (userData.hub != null) cons(userData.hub!!)
        userData.conduits.each(cons)
        userData.inputs.each(cons)
        userData.outputs.each(cons)
    }

    fun eachCrafter(src: LogisticsBlock.LogisticsBuild, cons: (GenericCrafter.GenericCrafterBuild) -> Unit) {
        val data = graph.ensureInfo(src.graphVertex)[0].node.root.data!!
        val userData = data.userData!!
        userData.crafterLinks.each(cons)
    }

    fun init() {
        Events.run(EventType.Trigger.newGame, graph::reset)
    }

    fun reset() {
        crafterVertexes.clear()
        graph.reset()
    }
}