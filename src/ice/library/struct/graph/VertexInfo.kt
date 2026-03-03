package ice.library.struct.graph

class VertexInfo<D : UserData<D>>(val vertex: Vertex<D>, dataProv: (() -> D)? = null) {
    val links = HashMap<Vertex<D>, LinkedData>()

    val layers = mutableListOf<LinkedLayer<D>>()

    init {
        layers += LinkedLayer(0, vertex, dataProv?.invoke())
    }

    operator fun get(index: Int): LinkedLayer<D> {
        if (index >= layers.size)
            for (i in layers.size..index) layers += LinkedLayer(i, vertex)
        return layers[index]
    }
}