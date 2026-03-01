package singularity.world.blocks.defence

import arc.Core
import arc.func.Cons
import arc.func.Floatf
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.world.IceBulletHandler
import mindustry.Vars
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Mover
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.entities.pattern.ShootPattern
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.StatusEffect
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.world.SglFx
import singularity.world.blocks.SglBlock
import singularity.world.gameoflife.Cell
import singularity.world.gameoflife.LifeGrid
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.components.blockcomp.FactoryBlockComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.cons.ConsumeItems
import universecore.world.consumers.cons.ConsumeLiquids
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

open class GameOfLife(name: String) : SglBlock(name) {
  var cellSize: Float = (4 * Vars.tilesize).toFloat()
  var gridFlushInterval: Float = 60f
  var gridSize: Int = 32
  var gridStoke: Float = 2f
  var rot45: Boolean = true
  var maxCellYears: Int = 5
  var cellSenescence: Boolean = true
  var warmupSpeed: Float = 0.025f
  var cellBornEffect: Effect? = null
  var cellDeathEffect: Effect? = SglFx.cellDeath
  var gridColor: Color = Color.white
  var bornTriggers: Seq<CellCaller?> = Seq<CellCaller?>()
  var deathTriggers: Seq<CellCaller> = Seq<CellCaller>()
  var cellRegion: Array<TextureRegion?>? = null
  var launchConsMulti: Floatf<GameOfLifeBuild?> = Floatf { e: GameOfLifeBuild? -> e!!.lifeCells / 4f }
  val launchCons: BaseConsumers = object : BaseConsumers(false) {
    init {
      setConsDelta { e: GameOfLifeBuild -> e.launchEff * Time.delta }
    }

    override fun <T : BaseConsume<out ConsumerBuildComp>> add(consume: T): T {
      consume.setMultiple(launchConsMulti)
      return super.add(consume)
    }
  }

  init {
    update = true
    solid = true
    sync = true

    canOverdrive = false

    configurable = true
    buildType = Prov(::GameOfLifeBuild)
    config(Point2::class.java) { e: GameOfLifeBuild?, p: Point2? ->
      val cell = e!!.grid!!.get(p!!.x, p.y) ?: throw RuntimeException("position out of bound")
      if (cell.isLife) {
        cell.kill()
      } else cell.born()
    }

    config(Int::class.javaObjectType) { e: GameOfLifeBuild?, i: Int? ->
      when (i) {
        0 -> {
          e!!.activity = false
          e.editing = true
          e.grid!!.resetYears()
        }

        1 -> {
          e!!.activity = !e.activity
        }

        2 -> {
          for (cell in e!!.grid!!) {
            cell.kill()
          }
        }
      }
    }

    config(IntSeq::class.java) { e: GameOfLifeBuild?, seq: IntSeq? ->
      for (cell in e!!.grid!!) {
        cell.kill()
      }
      for (i in 0..<seq!!.size) {
        val p = Point2.unpack(seq.get(i))
        e.grid!!.get(p.x, p.y).born()
      }
    }
  }

  override fun newConsume(): BaseConsumers {
    val res = super.newConsume()
    res.time(gridFlushInterval)
    return res
  }

  override fun load() {
    super.load()
    cellRegion = arrayOfNulls(maxCellYears + 1 + (if (cellSenescence) 1 else 0))
    for (i in cellRegion!!.indices) {
      cellRegion!![i] = Core.atlas.find(name + "_cell_" + i, Core.atlas.find(name + "_cell_death", Core.atlas.white()))
    }
  }

  override fun init() {
    for (consumer in consumers) {
      for (cons in launchCons.all()) {
        val filter = cons.filter()
        if (filter != null) {
          for (content in filter) {
            consumer.addToFilter(cons.type(), content)
          }
        }
      }
    }

    super.init()
    clipSize = cellSize * gridSize * (if (rot45) Mathf.sqrt2 else 1f)
  }

  override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
    return Vars.indexer.findTile(team, tile.worldx(), tile.worldy(), cellSize * gridSize * Mathf.sqrt2 / 2) { b: Building? -> b!!.block === this } == null
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Draw.color(if (valid) Pal.placing else Pal.remove)
    Drawf.square(x * Vars.tilesize + offset, y * Vars.tilesize + offset, cellSize * gridSize / 2 * Mathf.sqrt2)

    if (!valid) {
      drawPlaceText(Core.bundle.get("infos.placeAreaInvalid"), x, y, false)
    }
  }

  override fun setBars() {
    super.setBars()

    addBar<GameOfLifeBuild>("cells") { e ->
      Bar({ Core.bundle.format("bar.cellCount", e.lifeCells) }, { Pal.items }, { 1f })
    }
    addBar<GameOfLifeBuild>("launch") { e ->
      Bar({ Core.bundle.format("bar.launchProgress", e.warmup * 100) }, { Pal.powerBar }, { e!!.warmup })
    }
  }

  override fun setStats() {
    super.setStats()

    stats.add(SglStat.recipes) { t ->
      t!!.left().row()
      for (i in 0..<consumers.size) {
        val cons = consumers.get(i)
        val details = Table()
        FactoryBlockComp.buildRecipe(details, cons, null)

        t.table(SglDrawConst.grayUIAlpha) { ta -> ta!!.add(details).pad(4f) }
        t.row()
      }
    }

    stats.remove(Stat.productionTime)
    stats.add(SglStat.flushTime, gridFlushInterval / 60f, StatUnit.seconds)
    stats.add(SglStat.maxCellYears, maxCellYears.toFloat())
    val s = Strings.autoFixed(gridSize * cellSize / Vars.tilesize, 1)
    stats.add(SglStat.gridSize, gridSize.toString() + "x" + gridSize + " - [gray]" + s + "x" + s + StatUnit.blocks.localized() + "[]")
    stats.add(SglStat.launchTime, launchCons.craftTime / 60f, StatUnit.seconds)
    stats.add(SglStat.launchConsume) { t ->
      val stat = Stats()
      launchCons.showTime = false
      launchCons.display(stat)

      t!!.row()
      FactoryBlockComp.buildStatTable(t, stat)
    }
    stats.add(SglStat.effect) { t ->
      t!!.row()
      for (i in 0..maxCellYears) {
        val stat = Stats()

        for (trigger in deathTriggers) {
          if (trigger.valid(i)) {
            trigger.setStats(stat)
          }
        }

        t.add(Core.bundle.format("infos.cellYears", i)).left().top().color(Pal.gray).fill()
        t.table(SglDrawConst.grayUIAlpha) { item: Table? ->
          item!!.defaults().grow().left()
          FactoryBlockComp.buildStatTable(item, stat)
        }.fill().pad(5f).left().margin(5f)
        t.row()
      }
      if (cellSenescence) {
        t.add().left().top()
        t.table(SglDrawConst.grayUIAlpha) { item: Table? ->
          item!!.defaults().grow().left()
          item.add(Core.bundle.get("infos.cellYearsOverflow"))
        }.fill().pad(5f).left().margin(5f)
        t.row()
      }
    }
  }

  fun addDeathTrigger(years: Int, caller: CellCaller) {
    addDeathTrigger(years, years, caller)
  }

  fun addDeathTrigger(minYears: Int, maxYears: Int, caller: CellCaller) {
    deathTriggers.add(object : CellCaller(minYears, maxYears) {
      override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
        if (valid(cell!!.years)) caller.call(build, cell, x, y)
      }

      override fun setStats(stats: Stats) {
        caller.setStats(stats)
      }
    })
  }

  inner class GameOfLifeBuild : SglBuilding() {
    var grid: LifeGrid? = null
    var lifeCells: Int = 0
    var launchEff: Float = 0f
    var depoly: Float = 0f
    var warmup: Float = 0f
    var progress: Float = 0f
    var activity: Boolean = false
    var launched: Boolean = false
    var editing: Boolean = false
    var invalid: Boolean = false

    override fun create(block: Block, team: Team): Building {
      super.create(block, team)
      grid = LifeGrid(gridSize)
      grid!!.maxYears = maxCellYears + (if (cellSenescence) 1 else 0)

      return this
    }

    override fun buildConfiguration(table: Table) {
      table.button(Icon.pencil, Styles.cleari) {
        table.clearChildren()
        configure(0)
      }.size(50f)

      table.button(Icon.play, Styles.cleari) { configure(1) }.update { b: ImageButton? -> b!!.style.imageUp = if (activity) Icon.pause else Icon.play }.size(50f)

      table.button(Icon.cancel, Styles.cleari) { configure(2) }.size(50f)
    }

    override fun onConfigureTapped(x: Float, y: Float): Boolean {
      if (editing) {
        val cell: Cell? = untrns(x, y)
        if (cell == null) {
          editing = false
          return false
        }

        configure(Point2(cell.x, cell.y))

        return true
      } else {
        val b = Vars.world.buildWorld(x, y)
        return b === this
      }
    }

    override fun drawConfigure() {
      if (invalid) {
        drawPlaceText(Core.bundle.format("infos.placeAreaInvalid", Core.bundle.get("infos.areaOverlaped")), tileX(), tileY(), false)
        return
      }

      if (!editing) return
      val width = cellSize - gridStoke * 2
      for (cell in grid!!) {
        val c = trns(cell)
        if (cell.isLife) {
          Draw.color(gridColor)
          Draw.alpha(0.75f + Mathf.absin(4f, 0.25f))
          Fill.square(c.x, c.y, width / 2, (if (rot45) 45 else 0).toFloat())
        } else {
          Draw.color(Pal.accent)
          Draw.alpha(0.3f + Mathf.absin(4f, 0.3f))
          Fill.square(c.x, c.y, width / 2, (if (rot45) 45 else 0).toFloat())
        }
      }

      drawLaunchConsume()
    }

    fun drawLaunchConsume() {
      val mult = launchConsMulti.get(this)
      for (cons in launchCons.all()) {
        var line = 1
        drawPlaceText(Core.bundle.format("infos.firstCells", lifeCells), tileX(), tileY(), true)
        if (cons is ConsumeItems<*>) {
          for (stack in cons.consItems!!) {
            val width = drawPlaceText(Core.bundle.format("infos.gridLaunchCons", Mathf.round(stack.amount * mult)), tileX(), tileY() - line, true)
            val dx = x * Vars.tilesize + offset - width / 2f - 4f
            val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
            Draw.mixcol(Color.darkGray, 1f)
            Draw.rect(stack.item.uiIcon, dx, dy - 1)
            Draw.reset()
            Draw.rect(stack.item.uiIcon, dx, dy)
            line++
          }
        } else if (cons is ConsumeLiquids<*>) {
          for (stack in cons.consLiquids!!) {
            val width = drawPlaceText(Core.bundle.format("infos.gridLaunchCons", stack.amount * mult) + StatUnit.perSecond, tileX(), tileY() - line, true)
            val dx = x * Vars.tilesize + offset - width / 2f - 4f
            val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
            Draw.mixcol(Color.darkGray, 1f)
            Draw.rect(stack.liquid.uiIcon, dx, dy - 1)
            Draw.reset()
            Draw.rect(stack.liquid.uiIcon, dx, dy)
            line++
          }
        }
      }
    }

    override fun updateTile() {
      invalid = false
      Vars.indexer.allBuildings(x, y, cellSize * gridSize * Mathf.sqrt2 / 2, Cons { b: Building? ->
        if (b === this || b!!.team !== team || invalid) return@Cons
        if (b.block === block) {
          invalid = true
        }
      })

      lifeCells = 0
      for (cell in grid!!) {
        if (cell.isLife) lifeCells++
      }

      depoly = Mathf.lerpDelta(depoly, (if (consumeValid()) 1 else 0).toFloat(), warmupSpeed)

      launchEff = 0f
      if (activity) {
        launchEff = 1f
        for (cons in launchCons.all()) {
          val cons1 = cons as BaseConsume<ConsumerBuildComp>
          launchEff *= cons1.efficiency(this)
        }
      }

      if (launchEff >= 0.001f && depoly >= 0.99f) {
        if (!launched) {
          for (cons in launchCons.all()) {
            val cons1 = cons as BaseConsume<ConsumerBuildComp>
            cons.update(this)
          }

          warmup = Mathf.approachDelta(warmup, 1f, 1 / launchCons.craftTime * launchEff)

          if (warmup >= 0.99f) {
            launched = true
            warmup = 1f
            launched()
          }
        }
      }

      if (depoly < 0.99f || !activity) {
        launched = false
        warmup = Mathf.approachDelta(warmup, 0f, 0.02f)
      }

      if (updateValid()) {
        progress += 1 / gridFlushInterval * Time.delta * consEfficiency()

        if (progress >= 1) {
          progress %= 1f

          grid!!.flush({ cell: Cell? ->
            val v = trns(cell!!)
            cellBorn(cell, v.x, v.y)
          }, { cell: Cell? ->
            val v = trns(cell!!)
            cellDeath(cell, v.x, v.y)
          })
        }
      }
    }

    override fun consumeValid(): Boolean {
      return super.consumeValid() && !invalid
    }

    fun launched() {
      progress = 0f

      for (cell in grid!!) {
        val dis = max(abs(cell.x - grid!!.offset), abs(cell.y - grid!!.offset))
        val v = trns(cell)
        val cx = v.x
        val cy = v.y
        Time.run(dis * 5) { SglFx.cellScan.at(cx, cy, (if (rot45) 45 else 0).toFloat(), gridColor, block) }
      }
      for (cons in launchCons.all()) {
        cons as BaseConsume<ConsumerBuildComp>
        cons.consume(this)
      }
    }

    override fun status(): BlockStatus? {
      if (!activity) return BlockStatus.noOutput
      if (launchEff < 0.01f) return BlockStatus.noInput

      return super.status()
    }

    fun cellDeath(cell: Cell?, x: Float, y: Float) {
      if (cellDeathEffect != null) cellDeathEffect!!.at(x, y, (if (rot45) 45 else 0).toFloat(), gridColor, block)
      for (death in deathTriggers) {
        death.call(this, cell, x, y)
      }
    }

    fun cellBorn(cell: Cell?, x: Float, y: Float) {
      if (cellBornEffect != null) cellBornEffect!!.at(x, y, (if (rot45) 45 else 0).toFloat(), gridColor, block)
      for (born in bornTriggers) {
        born?.call(this, cell, x, y)
      }
    }

    override fun updateValid(): Boolean {
      return shouldConsume() && consumeValid() && warmup >= 0.99f && !invalid
    }

    override fun shouldConsume(): Boolean {
      return super.shouldConsume() && activity && !invalid && lifeCells > 0
    }

    override fun draw() {
      super.draw()
      val size = cellSize * gridSize / 2
      var edgeLerp = Mathf.clamp(depoly / 0.6f)
      edgeLerp = 1 - Mathf.pow(1 - edgeLerp, 3f)
      val gridLerp = Mathf.clamp((depoly - 0.6f) / 0.4f)

      Draw.z(Layer.effect)
      Lines.stroke(gridStoke * edgeLerp, gridColor)
      Lines.square(x, y, size * edgeLerp * Mathf.sqrt2, (if (rot45) 45 else 0).toFloat())
      Draw.z(Layer.flyingUnit + 5)
      val c = gridSize / 2
      val step = 1f / c
      Draw.alpha(0.45f + 0.3f * warmup)
      for (i in 0..<c) {
        val off = (gridSize % 2 * cellSize / 2) + size * i * step
        val lerp = Mathf.clamp((gridLerp - step * i) / step)

        for (p in Geometry.d4) {
          val dx = off * p.x
          val dy = off * p.y

          if (p.x == 0) {
            Tmp.v1.set(gridSize * cellSize / 2, dy).rotate((if (rot45) 45 else 0).toFloat())
            Tmp.v2.set(-gridSize * cellSize / 2, dy).rotate((if (rot45) 45 else 0).toFloat())
          } else {
            Tmp.v1.set(dx, gridSize * cellSize / 2).rotate((if (rot45) 45 else 0).toFloat())
            Tmp.v2.set(dx, -gridSize * cellSize / 2).rotate((if (rot45) 45 else 0).toFloat())
          }

          Lines.stroke(gridStoke * lerp)
          Lines.line(x + Tmp.v1.x, y + Tmp.v1.y, x + Tmp.v2.x, y + Tmp.v2.y)
        }
      }

      for (cell in grid!!) {
        Draw.alpha(warmup)
        val v = trns(cell)
        if (cell.isLife) {
          drawCell(cell, v.x, v.y)
        } else {
          drawDeathCell(cell, v.x, v.y)
        }
      }
    }

    fun drawDeathCell(cell: Cell, x: Float, y: Float) {
      val width = (cellSize - gridStoke * 3) / 2
      val dis = max(abs(cell.x - grid!!.offset), abs(cell.y - grid!!.offset))
      val step = 720f / grid!!.size / 2

      Draw.alpha(0.35f * Mathf.sinDeg(Time.time * 1.6f - dis * step) * Draw.getColor().a)
      Fill.square(x, y, width, (if (rot45) 45 else 0).toFloat())
    }

    fun drawCell(cell: Cell, x: Float, y: Float) {
      val width = cellSize - gridStoke * 3

      Draw.rect(cellRegion!![cell.years], x, y, width, width, (if (rot45) 45 else 0).toFloat())
    }

    fun trns(cell: Cell): Vec2 {
      val x = (cell.x * cellSize) - grid!!.offset * cellSize
      val y = (cell.y * cellSize) - grid!!.offset * cellSize

      return tmp.set(x, y).rotate((if (rot45) 45 else 0).toFloat()).add(this.x, this.y)
    }

    fun untrns(x: Float, y: Float): Cell? {
      var x = x
      var y = y
      x -= this.x
      y -= this.y

      tmp.set(x, y).rotate((if (rot45) -45 else 0).toFloat())

      x = tmp.x
      y = tmp.y
      val dx = (x / cellSize + grid!!.offset).roundToInt()
      val dy = Mathf.round(y / cellSize + grid!!.offset)

      return grid!!.get(dx, dy)
    }

    override fun config(): Any {
      val res = IntSeq()
      for (cell in grid!!) {
        if (!cell.isLife) continue

        res.add(Point2.pack(cell.x, cell.y))
      }

      return res
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(launched)
      write.bool(activity)

      write.f(progress)
      write.f(depoly)
      write.f(warmup)

      write.i(grid!!.size * grid!!.size)
      for (cell in grid!!) {
        write.i(Point2.pack(cell.x, cell.y))
        write.i(if (cell.isLife) cell.years else -1)
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      launched = read.bool()
      activity = read.bool()

      progress = read.f()
      depoly = read.f()
      warmup = read.f()
      val size = read.i()
      for (i in 0..<size) {
        val p = Point2.unpack(read.i())
        val n = read.i()
        val c = grid!!.get(p.x, p.y)

        if (n <= -1) {
          c.kill()
        } else {
          c.born()
          c.years = n
        }
      }
    }
  }

  abstract class CellCaller {
    val minYears: Int
    val maxYears: Int

    constructor() {
      minYears = Int.MIN_VALUE
      maxYears = Int.MAX_VALUE
    }

    constructor(minYears: Int, maxYears: Int) {
      this.minYears = minYears
      this.maxYears = maxYears
    }

    fun valid(years: Int): Boolean {
      return years in minYears..maxYears
    }

    abstract fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float)

    abstract fun setStats(stats: Stats)
  }

  companion object {
    private val tmp = Vec2()
    fun fx(effect: Effect): CellCaller {
      return object : CellCaller() {
        override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
          effect.at(x, y, (if ((build.block as GameOfLife).rot45) 45 else 0).toFloat(), (build.block as GameOfLife).gridColor, build.block)
        }

        override fun setStats(stats: Stats) {
          //no action
        }
      }
    }

    fun shootBullet(bullet: BulletType, pattern: ShootPattern): CellCaller {
      return object : CellCaller() {
        override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
          val a = IceBulletHandler { offX: Float, offY: Float, rotation: Float, delay: Float, move: Mover? ->
            bullet.create(build, build.team, x + offX, y + offY, rotation, bullet.damage, 1f, 1f, null, move)
          }
          pattern.shoot(0, a)
        }

        override fun setStats(stats: Stats) {
          stats.add(Stat.ammo) { t: Table? ->
            t!!.table { bt: Table? ->
              bt!!.defaults().left()
              if (pattern.shots > 1) {
                bt.add(Core.bundle.format("infos.shots", pattern.shots))
                bt.row()
              }
              UIUtils.buildAmmo(bt, bullet)
            }.padTop(-9f).padLeft(0f).left().get().background(Tex.underline)
            t.row()
          }
        }
      }
    }

    fun damage(damage: Float, range: Float, sclBase: Float): CellCaller {
      return object : CellCaller() {
        override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
          Damage.damage(build.team, x, y, range, damage * Mathf.pow(sclBase, cell!!.years.toFloat()))
        }

        override fun setStats(stats: Stats) {
          stats.add(Stat.damage, damage.toString() + "*" + sclBase + "^years ~ [gray]" + Core.bundle.get("misc.range") + Strings.autoFixed(range / Vars.tilesize, 1) + StatUnit.blocks.localized() + "[]")
        }
      }
    }

    fun effectEnemy(effect: StatusEffect, range: Float, duration: Float): CellCaller {
      return object : CellCaller() {
        override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
          Damage.status(build.team, x, y, range, effect, duration * cell!!.years, true, true)
        }

        override fun setStats(stats: Stats) {
          stats.add(SglStat.effect) { t: Table? ->
            t!!.defaults().left()
            t.row()
            t.add(Core.bundle.get("misc.range") + ": " + Strings.autoFixed(range / Vars.tilesize, 1) + StatUnit.blocks.localized())
            t.row()
            t.table { e: Table? ->
              e!!.image(effect.uiIcon).size(25f).scaling(Scaling.fit)
              e.add(Core.bundle.get("misc.toEnemy") + "[stat]" + effect.localizedName + "[lightgray] ~ " + "[stat]" + Strings.autoFixed(duration / 60f, 1) + "*years[lightgray] " + Core.bundle.get("unit.seconds"))
              e.row()
            }
            t.row()
          }
        }
      }
    }

    fun effectAllies(effect: StatusEffect, range: Float, duration: Float): CellCaller {
      return object : CellCaller() {
        override fun call(build: GameOfLifeBuild, cell: Cell?, x: Float, y: Float) {
          Units.nearby(build.team, x, y, range * 2, range * 2, Cons { entity: Unit? ->
            if (!entity!!.hittable() || !entity.within(x, y, range)) {
              return@Cons
            }
            entity.apply(effect, duration * cell!!.years)
          })
        }

        override fun setStats(stats: Stats) {
          stats.add(SglStat.effect) { t: Table? ->
            t!!.defaults().left()
            t.row()
            t.add(Core.bundle.get("misc.range") + ": " + Strings.autoFixed(range / Vars.tilesize, 1) + StatUnit.blocks.localized())
            t.row()
            t.table { e: Table? ->
              e!!.image(effect.uiIcon).size(25f)
              e.add(Core.bundle.get("misc.toTeam") + "[stat]" + effect.localizedName + "[lightgray] ~ " + "[stat]" + Strings.autoFixed(duration / 60f, 1) + "*years[lightgray] " + Core.bundle.get("unit.seconds"))
              e.row()
            }
            t.row()
          }
        }
      }
    }
  }
}