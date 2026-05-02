package universecore.struct.graph

import universecore.util.EulerTourTree

/**
 * ## Fully-dynamic Algorithms for Connectivity
 * It is implemented using the [___Holm, de Lichtenberg, Thorup___](https://dl.acm.org/doi/epdf/10.1145/502090.502095) algorithm.
 * @author azuma_soweren(sowie)
 */
class Graph<D : UserData<D>>(val edgeDataProv: (() -> D)? = null) {
    companion object {
        const val REBUILD_CHANGE = 2
        const val MAX_VERTEXES = 1 shl 30
    }

    private var infos = HashMap<Vertex<D>, VertexInfo<D>>()

    private var maxCount = 0
    private var maxCountLog = 0

    fun reset() {
        infos.clear()
        maxCount = 0
        maxCountLog = 0
    }

    private fun checkVertexes(u: Vertex<D>, v: Vertex<D>) {
        require(u != v) { "Self-loops are not allowed" }
    }

    fun ensureInfo(vertex: Vertex<D>): VertexInfo<D> {
        if (vertex in infos) return infos[vertex]!!

        if (infos.size == MAX_VERTEXES) throw IllegalStateException("Too many vertices. (max: $MAX_VERTEXES)")

        val info = VertexInfo(vertex, vertex.dataProv)
        infos[vertex] = info

        if (infos.size > 1 shl maxCountLog) maxCountLog++
        if (infos.size > maxCount) maxCount = infos.size

        return info
    }

    private fun remove(vertex: Vertex<D>) {
        infos -= vertex

        if (4 * infos.size <= maxCount && maxCount > 12) {
            infos = HashMap(infos)
            maxCount = infos.size
        }
        if (infos.size shl REBUILD_CHANGE <= 1 shl maxCountLog) rebuild()
    }

    private fun rebuild() {
        if (infos.isEmpty()) {
            maxCountLog = 0
            return
        }

        var deletions = 0
        while (infos.size shl 1 <= 1 shl maxCountLog) {
            maxCountLog--
            deletions++
        }
        if (deletions == 0) return

        // merge Layer_0 with Layer_top
        for ((u, uInfo) in infos)
            repeat(deletions) {
                if (uInfo.layers.size == 1) return@repeat
                val layer = uInfo.layers.last()

                val graphV = layer.graphLinks.iterator()
                while (graphV.hasNext()) {
                    val v = graphV.next()
                    val vInfo = infos[v]!!
                    uInfo.links[v]!!.level = 0
                    vInfo.links[u]!!.level = 0
                    removeFromGraphSafe(graphV, uInfo, vInfo, layer.level)
                    addToGraph(uInfo, vInfo)
                }

                val treeV = layer.treeLinks.iterator()
                while (treeV.hasNext()) {
                    val (v, _) = treeV.next()
                    val vInfo = infos[v]!!
                    uInfo.links[v]!!.level = layer.level - 1
                    vInfo.links[u]!!.level = layer.level - 1
                    removeFromTreeSafe(treeV, uInfo, vInfo, layer.level)
                }

                uInfo.layers.removeLast()
            }
    }

    fun link(u: Vertex<D>, v: Vertex<D>): Boolean {
        checkVertexes(u, v)

        val uInfo = ensureInfo(u)
        val vInfo = ensureInfo(v)
        if (v in uInfo.links) return false

        val isGraphEdge = EulerTourTree.connected(uInfo[0].node, vInfo[0].node)
        if (isGraphEdge) addToGraph(uInfo, vInfo) else addToTree(uInfo, vInfo)

        val type = if (isGraphEdge) LinkedType.GRAPH else LinkedType.TREE
        val data = LinkedData(type)
        uInfo.links[v] = data
        vInfo.links[u] = data

        return true
    }

    fun cut(u: Vertex<D>, v: Vertex<D>): Boolean {
        checkVertexes(u, v)

        if (u !in infos || v !in infos) return false

        val uInfo = infos[u]!!
        val data = uInfo.links[v] ?: return false
        val vInfo = infos[v]!!
        val dataLevel = data.level

        uInfo.links -= v
        vInfo.links -= u
        when (data.type) {
            LinkedType.GRAPH -> removeFromGraph(uInfo, vInfo, dataLevel)

            LinkedType.TREE -> {
                for (level in dataLevel downTo 0) removeFromTree(uInfo, vInfo, level)

                levelDown@ for (level in dataLevel downTo 0) {
                    val uRoot = uInfo[level].node.root
                    val vRoot = vInfo[level].node.root
                    val root = if (uRoot.data!!.treeVertexes.size < vRoot.data!!.treeVertexes.size) uRoot else vRoot
                    val nextLevel = level + 1

                    if (root.data!!.treeVertexes.size > 1) copyTreeEdges(root, level)

                    for (u in root.data!!.treeVertexes) {
                        val uInfo = infos[u]!!

                        val graphV = uInfo[level].graphLinks.iterator()
                        while (graphV.hasNext()) {
                            val v = graphV.next()
                            val vInfo = infos[v]!!

                            val linkedData = uInfo.links[v]!!
                            val uNode = uInfo[level].node
                            val vNode = vInfo[level].node
                            removeFromGraphSafe(graphV, uInfo, vInfo, level)
                            if (EulerTourTree.connected(uNode, vNode)) {
                                addToGraph(uInfo, vInfo, nextLevel)
                                linkedData.level = nextLevel
                            } else {
                                for (level in linkedData.level downTo 0) addToTree(uInfo, vInfo, level)
                                linkedData.type = LinkedType.TREE

                                break@levelDown
                            }
                        }
                    }
                }
            }
        }

        if (uInfo.links.isEmpty()) remove(u)
        if (vInfo.links.isEmpty()) remove(v)

        return true
    }

    fun connected(u: Vertex<D>, v: Vertex<D>): Boolean {
        checkVertexes(u, v)

        if (u !in infos || v !in infos) return false

        val uInfo = infos[u]!!
        val vInfo = infos[v]!!

        return EulerTourTree.connected(uInfo[0].node, vInfo[0].node)
    }

    private fun copyTreeEdges(root: EulerTourTree.Node<NodeData<D>>, level: Int) {
        val nextLevel = level + 1
        val vertexes = root.data!!.treeVertexes
        for (u in vertexes) {
            val uInfo = infos[u]!!
            for ((v, _) in uInfo[level].treeLinks) {
                val vInfo = infos[v]!!
                val data = uInfo.links[v]!!
                if (data.level > level) continue
                addToTree(uInfo, vInfo, nextLevel)
                data.level = nextLevel
            }
        }
    }

    private fun addToTree(uInfo: VertexInfo<D>, vInfo: VertexInfo<D>, level: Int = 0) {
        val u = uInfo.vertex
        val v = vInfo.vertex
        if (u in vInfo[level].treeLinks) return
        val prov = if (level == 0) edgeDataProv else null
        val uv = EulerTourTree.Node(NodeData(level, null, prov?.invoke()))
        val vu = EulerTourTree.Node(NodeData(level, null, prov?.invoke()))
        val uNode = uInfo[level].node
        val vNode = vInfo[level].node
        uInfo[level].treeLinks[v] = uv
        vInfo[level].treeLinks[u] = vu
        EulerTourTree.link(uNode, vNode, uv, vu)
    }

    private fun removeFromTree(uInfo: VertexInfo<D>, vInfo: VertexInfo<D>, level: Int = 0) {
        val u = uInfo.vertex
        val v = vInfo.vertex
        if (u !in vInfo[level].treeLinks) return
        val uv = uInfo[level].treeLinks[v]!!
        val vu = vInfo[level].treeLinks[u]!!
        uInfo[level].treeLinks -= v
        vInfo[level].treeLinks -= u
        EulerTourTree.cut(uv, vu)
    }

    private fun removeFromTreeSafe(
        itv: MutableIterator<MutableMap.MutableEntry<Vertex<D>, EulerTourTree.Node<NodeData<D>>>>,
        uInfo: VertexInfo<D>,
        vInfo: VertexInfo<D>,
        level: Int = 0
    ) {
        val u = uInfo.vertex
        val v = vInfo.vertex
        if (u !in vInfo[level].treeLinks) return
        val uv = uInfo[level].treeLinks[v]!!
        val vu = vInfo[level].treeLinks[u]!!
        itv.remove()
        vInfo[level].treeLinks -= u
        EulerTourTree.cut(uv, vu)
    }

    private fun addToGraph(uInfo: VertexInfo<D>, vInfo: VertexInfo<D>, level: Int = 0) {
        val u = uInfo.vertex
        val v = vInfo.vertex
        if (u in vInfo[level].graphLinks) return
        uInfo[level].graphLinks += v
        vInfo[level].graphLinks += u
    }

    private fun removeFromGraph(uInfo: VertexInfo<D>, vInfo: VertexInfo<D>, level: Int = 0) {
        val u = uInfo.vertex
        val v = vInfo.vertex
        uInfo[level].graphLinks -= v
        vInfo[level].graphLinks -= u
    }

    // maybe safe?
    private fun removeFromGraphSafe(
        itv: MutableIterator<Vertex<D>>,
        uInfo: VertexInfo<D>,
        vInfo: VertexInfo<D>,
        level: Int = 0
    ) {
        itv.remove()
        vInfo[level].graphLinks -= uInfo.vertex
    }
}