package ice.game

import arc.Events
import arc.struct.Seq
import arc.struct.StringMap
import ice.DeepSpace
import ice.content.block.EffectBlocks
import ice.graphics.IceColor
import ice.ui.menusDialog.AchievementDialog
import mindustry.Vars
import mindustry.game.EventType
import mindustry.game.Schematic
import universecore.world.Load
import mindustry.game.Team
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.meta.BuildVisibility
import singularity.core.UpdateTiles

object IceTeam : Load {
  val 教廷: Team = Team.blue.apply {
    name = "afehs"
    setPalette(IceColor.b4, IceColor.b6, IceColor.b7)
  }
  val 血肉: Team = Team.all[93].apply {
    name = "flesh"
    color.set(IceColor.r2)
  }
  val 帝国: Team = Team.all[94].apply {
    name = "empire"
    color.set(IceColor.r5)
  }

  val none: Team = Team.get(255)
}

object Schematics: Load{
    val allSch = Seq<Schematic>()
    var 虔信方垒 = createSchematic(3, 3) { tiles, strings ->
        strings.put("name", "虔信方垒")
        tiles.add(Schematic.Stile(EffectBlocks.虔信方垒, 1, 1, EffectBlocks.虔信方垒.lastConfig, 0))
    }
  var 传颂核心 = createSchematic(4, 4) { tiles, strings ->
    strings.put("name", "传颂核心")
    tiles.add(Schematic.Stile(EffectBlocks.传颂核心, 1, 1, EffectBlocks.传颂核心.lastConfig, 0))
  }
  var 永耀天枢 = createSchematic(5, 5) { tiles, strings ->
    strings.put("name", "永耀天枢")
    tiles.add(Schematic.Stile(EffectBlocks.永耀天枢, 2, 2, EffectBlocks.永耀天枢.lastConfig, 0))
  }

   override fun init() {
        allSch.forEach { schematic ->
            schematic.mod = DeepSpace.mod
            Vars.schematics.all().add(schematic)
            checkLoadout(schematic)
        }
    }

    fun createSchematic(w: Int, h: Int, sc: (Seq<Schematic.Stile>, StringMap) -> Unit): Schematic {
        val ssq = Seq<Schematic.Stile>()
        val stm = StringMap()
        sc.invoke(ssq, stm)
        val schematic = Schematic(ssq, stm, w, h)
        allSch.add(schematic)
        return schematic
    }

    private fun checkLoadout(s: Schematic) {
        val core = s.tiles.find { t -> t.block is CoreBlock }
        if (core == null) return
        val cores = s.tiles.count { t: Schematic.Stile? -> t!!.block is CoreBlock }
        val maxSize: Int = Vars.schematics.getMaxLaunchSize(core.block)
        //确保存在核心，并且原理图足够小。
        if ((s.width > maxSize || s.height > maxSize || s.tiles.contains { t -> t.block.buildVisibility === BuildVisibility.sandboxOnly || !t.block.unlocked() } || cores > 1)) return
        //放入缓存中
        Vars.schematics.loadouts.get(core.block as CoreBlock, ::Seq).add(s)
        //保存非自定义装载

    }
}

object EventType :Load {
  class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)

  private val contentInitEvent = Seq<() -> Unit>()
  private val clientLoadEvent = Seq<() -> Unit>()
  private val atlasPackEvent = Seq<() -> Unit>()
  override fun setup() {
    Events.on(EventType.AtlasPackEvent::class.java) {
      atlasPackEvent.forEach { it() }
    }
    Events.on(EventType.ContentInitEvent::class.java) {
      contentInitEvent.forEach { it() }
    }
    Events.on(EventType.ClientLoadEvent::class.java) {
      clientLoadEvent.forEach { it() }
    }
    UpdateTiles.setup()
  }
  /** 添加内容初始化事件,在所以内容初始化以后调用*/
  fun addContentInitEvent(run: () -> Unit) {
    contentInitEvent.add(run)
  }
  /** 客户端游戏首次加载时调用,update第一次运行 */
  fun addClientLoadEvent(run: () -> Unit) {
    clientLoadEvent.add(run)
  }

  fun addAtlasPackEvent(run: () -> Unit) {
    atlasPackEvent.add(run)
  }
}