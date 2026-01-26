package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Boolc
import arc.func.Cons
import arc.func.Cons2
import arc.func.Prov
import arc.math.geom.Point2
import arc.scene.event.Touchable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.CheckBox
import arc.scene.ui.ImageButton
import arc.scene.ui.Tooltip
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.OrderedMap
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Styles
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import universecore.util.Empties

open class AutoRecyclerComp(name: String) : DistNetBlock(name) {
  var usableRecycle: OrderedMap<DistBufferType<*>, Cons<Building>> = OrderedMap<DistBufferType<*>, Cons<Building>>()

  init {
    this.configurable = true
    config<IntSeq?, AutoRecyclerCompBuild?>(IntSeq::class.java, Cons2 { b: AutoRecyclerCompBuild?, c: IntSeq? ->
      val type: ContentType? = ContentType.entries[c!!.get(0)]
      val content = Vars.content.getByID<UnlockableContent?>(type, c.get(1))
      val set: ObjectSet<UnlockableContent?> = b!!.list.get(DistBufferType.typeOf(type), Prov { ObjectSet() })!!
      if (!set.add(content)) set.remove(content)
      b.flush = true
    })
    config(Int::class.javaObjectType, Cons2 { b: AutoRecyclerCompBuild?, i: Int? ->
      if (i == -1) {
        b!!.list.clear()
      } else if (i == 1) {
        b!!.isBlackList = !b.isBlackList
      }
      b!!.flush = true
    })
    buildType= Prov(::AutoRecyclerCompBuild)
  }

  fun <E : Building> setRecycle(type: DistBufferType<*>, recycle: Cons<E>) {
    this.usableRecycle.put(type, recycle as Cons<Building>?)
  }

  open inner class AutoRecyclerCompBuild : DistNetBuild() {
    var list: ObjectMap<DistBufferType<*>?, ObjectSet<UnlockableContent?>?> = ObjectMap<DistBufferType<*>?, ObjectSet<UnlockableContent?>?>()
    var isBlackList: Boolean = true
    var flush: Boolean = false
    var rebuildItems: Runnable? = null
    var currType: DistBufferType<*>? = null

    fun updateConfig() {
      if (this.distributor.network.netStructValid()) {
        val config = TargetConfigure()
        config.priority = -65536
        val coreTile = this.distributor.network.core!!.tile
        val dx = this.tile.x - coreTile!!.x
        val dy = this.tile.y - coreTile.y
        config.offsetPos = Point2.pack(dx, dy)
        if (!this.isBlackList) {
          for (type in this@AutoRecyclerComp.usableRecycle.orderedKeys()) {
            for (content in Vars.content.getBy<Content?>(type.targetType())) {
              if (content is UnlockableContent) {
                val c = content
                if (!(this.list.get(type, Empties.nilSetO<UnlockableContent?>()) as ObjectSet<*>).contains(c)) {
                  config.set(GridChildType.container, c, byteArrayOf(-1))
                }
              }
            }
          }
        } else {
          val var5: ObjectMap.Values<*> = this.list.values().iterator()

          while (var5.hasNext()) {
            val counts: ObjectSet<UnlockableContent> = var5.next() as ObjectSet<UnlockableContent>
            val var7 = counts.iterator()

            while (var7.hasNext()) {
              val content = var7.next() as UnlockableContent
              config.set(GridChildType.container, content, byteArrayOf(-1))
            }
          }
        }

        this.distributor.network.core!!.matrixGrid.remove(this)
        this.distributor.network.core!!.matrixGrid.addConfig(config)
      }
    }

    public override fun updateTile() {
      super.updateTile()
      if (this.flush) {
        this.updateConfig()
        this.flush = false
      }

      val var1: ObjectMap.Values<*> = this@AutoRecyclerComp.usableRecycle.values().iterator()

      while (var1.hasNext()) {
        val rec: Cons<Building?> = var1.next() as Cons<*> as Cons<Building?>
        rec.get(this)
      }
    }

    override fun networkValided() {
      this.flush = true
    }

    override fun buildConfiguration(table: Table) {
      table.table(Tex.pane, Cons { main: Table? ->
        main!!.pane(Cons { items: Table? ->
          this.rebuildItems = Runnable {
            items!!.clearChildren()
            val itemSeq = Vars.content.getBy<UnlockableContent?>(this.currType!!.targetType())
            var counter = 0
            for (item in itemSeq) {
              if (item.unlockedNow()) {
                val button = items.button(Tex.whiteui, Styles.selecti, 30.0f, Runnable { this.configure(IntSeq.with(*intArrayOf(item.getContentType().ordinal, item.id.toInt()))) }).size(40.0f).get() as ImageButton
                button.getStyle().imageUp = TextureRegionDrawable(item.uiIcon)
                button.update(Runnable { button.setChecked((this.list.get(this.currType, Empties.nilSetO<UnlockableContent?>()) as ObjectSet<*>).contains(item)) })
                button.addListener(Tooltip(Cons { t: Table? -> (t!!.table(Tex.paneLeft).get() as Table).add(item.localizedName) }))
                if (counter++ != 0 && counter % 5 == 0) {
                  items.row()
                }
              }
            }
          }
          this.currType = this@AutoRecyclerComp.usableRecycle.orderedKeys().get(0) as DistBufferType<*>
          this.rebuildItems!!.run()
        }).size(225.0f, 160.0f)
        main.image().color(Pal.gray).growY().width(4.0f).colspan(2).padLeft(3.0f).padRight(3.0f).margin(0.0f)
        main.table(Cons { sideBar: Table? ->
          sideBar!!.pane(Cons { typesTable: Table? ->
            for (type in this@AutoRecyclerComp.usableRecycle.orderedKeys()) {
              typesTable!!.button(Cons { t: Button? -> t!!.add(Core.bundle.get("content." + type.targetType().name + ".name")) }, Styles.underlineb, Runnable {
                this.currType = type
                this.rebuildItems!!.run()
              }).growX().height(35.0f).update(Cons { b: Button? -> b!!.setChecked(this.currType === type) }).touchable(Prov { if (this.currType === type) Touchable.disabled else Touchable.enabled })
              typesTable.row()
            }
          }).size(120.0f, 100.0f)
          sideBar.row()
          sideBar.check("", this.isBlackList, Boolc { b: Boolean -> this.configure(1) }).update(Cons { c: CheckBox? -> c!!.setText(Core.bundle.get(if (this.isBlackList) "misc.blackListMode" else "misc.whiteListMode")) }).size(120.0f, 40.0f)
          sideBar.row()
          sideBar.button(Core.bundle.get("misc.reset"), Icon.cancel, Styles.cleart, Runnable { this.configure(-1) }).size(120.0f, 40.0f)
        }).fillX()
      }).fill()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(this.isBlackList)
      write.i(this.list.size)
      val var2: ObjectMap.Entries<*, *> = this.list.iterator()

      while (var2.hasNext()) {
        val entry: ObjectMap.Entry<DistBufferType<*>?, ObjectSet<UnlockableContent?>?> = var2.next() as ObjectMap.Entry<DistBufferType<*>?, ObjectSet<UnlockableContent?>?>
        write.i((entry.key as DistBufferType<*>).id)
        write.i((entry.value as ObjectSet<*>).size)
        val var4 = (entry.value as ObjectSet<*>).iterator()

        while (var4.hasNext()) {
          val content = var4.next() as UnlockableContent
          write.i(content.getContentType().ordinal)
          write.i(content.id.toInt())
        }
      }
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.isBlackList = read.bool()
      this.list.clear()
      val size = read.i()

      for (i in 0..<size) {
        val set: ObjectSet<UnlockableContent> = this.list.get(DistBufferType.all[read.i()], Prov { ObjectSet() }) as ObjectSet<UnlockableContent>
        val s = read.i()

        for (l in 0..<s) {
          set.add(Vars.content.getByID<Content?>(ContentType.all[read.i()], read.i()) as UnlockableContent?)
        }
      }
    }
  }
}