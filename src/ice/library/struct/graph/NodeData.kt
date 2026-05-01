package ice.library.struct.graph

import ice.library.util.EulerTourTree
import ice.library.struct.UnsafeLinkedList

class NodeData<D : UserData<D>>(
    @Suppress("UNUSED") val level: Int,
    val vertex: Vertex<D>?,
    var userData: D? = null,
) : EulerTourTree.UserData<NodeData<D>>() {
    val treeVertexes = UnsafeLinkedList<Vertex<D>>()

    override fun maintain(left: NodeData<D>?, right: NodeData<D>?) {
        treeVertexes.clear()
        if (vertex != null) treeVertexes += vertex

        if (left != null) treeVertexes += left.treeVertexes
        if (right != null) treeVertexes += right.treeVertexes

        userData?.maintain(left?.userData, right?.userData)
    }
}