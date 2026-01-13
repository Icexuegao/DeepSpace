package singularity.world;

interface GameObject : Transform {
    var entityID: Int
    fun update()
}
