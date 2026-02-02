package ice.library

import arc.struct.Seq
import arc.struct.StringMap
import ice.DeepSpace
import ice.content.block.EffectBlocks
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.Schematic
import mindustry.game.Schematic.Stile
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.meta.BuildVisibility

object Schematics: Load{
    val allSch = Seq<Schematic>()
    var 虔信方垒 = createSchematic(3, 3) { tiles, strings ->
        strings.put("name", "虔信方垒")
        tiles.add(Stile(EffectBlocks.虔信方垒, 1, 1, EffectBlocks.虔信方垒.lastConfig, 0))
    }

   override fun init() {
        allSch.forEach { schematic ->
            schematic.mod = DeepSpace.mod
            Vars.schematics.all().add(schematic)
            checkLoadout(schematic)
        }
    }

    fun createSchematic(w: Int, h: Int, sc: (Seq<Stile>, StringMap) -> Unit): Schematic {
        val ssq = Seq<Stile>()
        val stm = StringMap()
        sc.invoke(ssq, stm)
        val schematic = Schematic(ssq, stm, w, h)
        allSch.add(schematic)
        return schematic
    }

    private fun checkLoadout(s: Schematic) {
        val core = s.tiles.find { t -> t.block is CoreBlock }
        if (core == null) return
        val cores = s.tiles.count { t: Stile? -> t!!.block is CoreBlock }
        val maxSize: Int = Vars.schematics.getMaxLaunchSize(core.block)
        //确保存在核心，并且原理图足够小。
        if ((s.width > maxSize || s.height > maxSize || s.tiles.contains { t -> t.block.buildVisibility === BuildVisibility.sandboxOnly || !t.block.unlocked() } || cores > 1)) return
        //放入缓存中
        Vars.schematics.loadouts.get(core.block as CoreBlock, ::Seq).add(s)
        //保存非自定义装载

    }
}