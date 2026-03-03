package ice.library.struct.graph

import ice.library.struct.EulerTourTree

class LinkedLayer<D : UserData<D>>(
    val level: Int,
    val vertex: Vertex<D>,
    userData: D? = null,
) {
    val node = EulerTourTree.Node(NodeData(level, vertex, userData))
    val treeLinks = HashMap<Vertex<D>, EulerTourTree.Node<NodeData<D>>>()
    val graphLinks = HashSet<Vertex<D>>()
}