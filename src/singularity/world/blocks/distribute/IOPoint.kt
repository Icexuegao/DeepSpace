package singularity.world.blocks.distribute

import arc.func.Cons
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.world.Edges
import mindustry.world.meta.StatValue
import singularity.contents.DistributeBlocks
import singularity.world.blocks.SglBlock
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.components.distnet.IOPointBlockComp
import singularity.world.components.distnet.IOPointComp
import singularity.world.meta.SglStat
import universecore.util.DataPackable

abstract class IOPoint(name: String) : SglBlock(name), IOPointBlockComp {
    init {
        update = true
        buildCostMultiplier = 0f

        schematicPriority = -10
    }

    public override fun init() {
        super.init()

        setupRequestFact()
    }

    public override fun setStats() {
        super.setStats()
        stats.add(SglStat.componentBelongs, StatValue { t: Table? ->
            t!!.defaults().left()
            t.image(DistributeBlocks.matrix_controller!!.fullIcon).size(35f).padRight(8f)
            t.add(DistributeBlocks.matrix_controller!!.localizedName)
        })
    }

    public override fun parseConfigObjects(e: SglBuilding?, obj: Any?) {
        if (obj is TargetConfigure && e is IOPointBuild) {
            val tile = e.nearby(-Point2.x(obj.offsetPos), -Point2.y(obj.offsetPos))
            if (tile !is DistMatrixUnitBuildComp) {
                e.parent(null)
                e.gridConfig(null)
            } else {
                //校准坐标...
                val offX = e.tileX() - tile.tileX()
                val offY = e.tileY() - tile.tileY()

                obj.offsetPos = Point2.pack(offX, offY)

                e.parent(tile)
                e.gridConfig(obj)
                e.parent()!!.addIO(e)
            }
        }
    }

    override fun pointConfig(config: Any?, transformer: Cons<Point2?>): Any? {
        if (config is ByteArray && DataPackable.readObject<DataPackable?>(config) is TargetConfigure) {
            var cfg=DataPackable.readObject<DataPackable?>(config) as TargetConfigure
            cfg.configHandle(transformer)
            return cfg.pack()
        }
        return config
    }

    abstract fun setupRequestFact()

    abstract inner class IOPointBuild : SglBuilding(), IOPointComp {
        var parentMat: DistMatrixUnitBuildComp? = null
        var config: TargetConfigure? = null

        override fun onProximityAdded() {
            super.onProximityAdded()
            if (config != null) {
                val tile = nearby(-Point2.x(config!!.offsetPos), -Point2.y(config!!.offsetPos))
                if (tile !is DistMatrixUnitBuildComp) {
                    parentMat = null
                    config = null
                } else {
                    parentMat = tile
                    parentMat!!.addIO(this)
                }
            }
        }

        override fun updateTile() {
            if (parentMat == null || !parentMat!!.building.isAdded()) {
                return
            }
            if (parentMat!!.gridValid()) {
                resourcesDump()
                resourcesSiphon()
                transBack()
            }
        }

        override fun onRemoved() {
            if (parentMat != null) parentMat!!.removeIO(this)
            super.onRemoved()
        }

        public override fun config(): ByteArray? {
            return if (config == null) NULL else config!!.pack()
        }

        public override fun write(write: Writes) {
            super.write(write)

            if (config == null) {
                write.i(-1)
            } else {
                write.i(1)
                config!!.write(write)
            }
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)

            if (revision >= 3) {
                if (read.i() > 0) {
                    config = TargetConfigure()
                    config!!.read(read)
                }
            }
        }

        fun getDirectBit(e: Building): Byte {
            val dir = relativeTo(Edges.getFacingEdge(e, this))
            return (if (dir.toInt() == 0) 1 else if (dir.toInt() == 1) 2 else if (dir.toInt() == 2) 4 else if (dir.toInt() == 3) 8 else 0).toByte()
        }

        protected abstract fun transBack()

        protected abstract fun resourcesSiphon()

        protected abstract fun resourcesDump()
    }

    companion object {
        val NULL: ByteArray = ByteArray(0)
    }
}