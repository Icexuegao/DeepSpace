package ice.library.struct.graph

import java.util.Random

class Vertex<D : UserData<D>>(random: Random, val dataProv: (() -> D)? = null) {
    private val hash = random.nextInt()

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vertex<D>

        return hash == other.hash
    }
}