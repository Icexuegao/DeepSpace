package ice.world.content.blocks.distribution.conveyor

import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.geom.Position
import arc.math.geom.QuadTree
import arc.math.geom.Rect
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.core.SettingValue
import ice.entities.IceRegister
import ice.library.IFiles
import mindustry.Vars
import mindustry.async.PhysicsProcess
import mindustry.content.Blocks
import mindustry.core.World
import mindustry.entities.Effect
import mindustry.entities.EntityCollisions
import mindustry.entities.EntityGroup
import mindustry.gen.*
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.io.TypeIO
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.modules.ItemModule
import kotlin.math.max
import kotlin.math.min

class PackStack() : Drawc, Hitboxc, Velc, Physicsc {
    @Transient
    var added: Boolean = false

    @Transient
    var id = EntityGroup.nextId()
    private var x = 0f
    private var y = 0f

    @Transient
    var indexDraw = -1

    @Transient
    var indexAll = -1
    var vel = Vec2(0f, 0f)

    @Transient
    private var lastX: Float = 0f

    @Transient
    private var lastY: Float = 0f

    @Transient
    private var deltaX: Float = 0f

    @Transient
    private var deltaY: Float = 0f
    var hitSize = 4f
    override fun clipSize(): Float = 200f
    var tex = IFiles.findPng("gradedConveyor-stack")
    var texShadow = IFiles.findPng("gradedConveyor-stack-shadow")
    var items = ItemModule()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entityc?> self(): T? = this as T?

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> `as`(): T? = this as T?
    override fun isAdded() = added
    override fun isLocal() = false
    override fun isRemote() = false
    override fun serialize() = true
    override fun classId(): Int = IceRegister.getId(this::class.java)
    override fun id(): Int {
        return id
    }

    override fun collides(p0: Hitboxc?) = true
    override fun deltaAngle(): Float {
        return Mathf.angle(deltaX, deltaY)
    }

    override fun deltaLen(): Float {
        return Mathf.len(deltaX, deltaY)
    }

    override fun deltaX() = deltaX
    override fun deltaY() = deltaY
    override fun hitSize() = hitSize
    override fun lastX() = lastX
    override fun lastY() = lastY
    override fun add() {
        if (added) return
        indexAll = Groups.all.addIndex(this)
        indexDraw = Groups.draw.addIndex(this)
        added = true
        updateLastPosition()
    }
    var deadEffect: Effect?=null
    override fun remove() {
        if (!added) return
        deadEffect?.at(this)
        Groups.all.remove(this)
        indexAll = -1
        Groups.draw.remove(this)
        indexDraw = -1
        added = false
        vel.setZero()
    }

    override fun afterRead() {
    }

    override fun afterReadAll() {

    }

    override fun collision(p0: Hitboxc?, p1: Float, p2: Float) {
    }

    override fun deltaX(p0: Float) {
        deltaX = p0
    }

    override fun deltaY(p0: Float) {
        deltaY = p0
    }

    override fun getCollisions(p0: Cons<QuadTree<*>?>?) {

    }

    override fun hitSize(p0: Float) {
        hitSize = p0
    }

    override fun hitbox(p0: Rect?) {
        p0?.setCentered(x, y, hitSize, hitSize)
    }

    override fun hitboxTile(p0: Rect?) {
        val size = min(hitSize * 0.66f, 7.8f)
        p0?.setCentered(x, y, size, size)
    }

    override fun lastX(lastX: Float) {
        this.lastX = lastX
    }

    override fun lastY(lastY: Float) {
        this.lastY = lastY
    }

    override fun beforeWrite() {

    }

    var time = 60 * 10f
    var shepos = -1
    override fun id(id: Int) {
        this.id = id
    }

    override fun draw() {

        Draw.z(Layer.block + 0.001f)
        Draw.color(Pal.shadow)
        if (SettingValue.启用包裹物品时限)Draw.alpha(time / (60 * 10f))
        Draw.rect(texShadow, x, y)
        Draw.color()
        Draw.alpha(time / (60 * 10f))
        Draw.scl()
        Draw.rect(tex, x, y)
        Draw.scl(0.5f)
        if (SettingValue.启用包裹物品绘制 && items.any()) {
            Draw.rect(items.first().uiIcon, x, y)
        }
        Draw.reset()
    }

    override fun read(read: Reads) {
        vel = TypeIO.readVec2(read, vel)
        items.read(read)
        this.x = read.f()
        this.y = read.f()
        shepos = read.i()
        time=read.f()
    }

    override fun write(write: Writes) {
        TypeIO.writeVec2(write, vel)
        items.write(write)
        write.f(x)
        write.f(y)
        write.i(shepos)
        write.f(time)
    }

    override fun update() {
        if (!Vars.net.client()) {
            if (SettingValue.启用包裹物品时限) {
                time -= Time.delta
                if (time <= 0f) remove()
            }else{
                time=60 * 10f
            }
            val px = x
            val py = y
            move(vel.x * Time.delta, vel.y * Time.delta)
            if (Mathf.equal(px, x)) vel.x = 0f
            if (Mathf.equal(py, y)) vel.y = 0f
            vel.scl(max(1.0f - drag * Time.delta, 0f))
            val buildWorld = Vars.world.buildWorld(x, y) as? PackStackConveyor.PackStackConveyorBuild
            buildWorld?.let {
                if (it.items.empty() && it.state == StackConveyor.stateLoad) {
                    items.each { item, amount ->
                        (1..amount).forEach { _ ->
                            it.handleItem(null, item)
                        }
                    }
                    remove()
                }
            }
        }
    }

    override fun vel(vel: Vec2) {
        this.vel = vel
    }

    override fun velAddNet(v: Vec2) {
        vel.add(v)
        if (this.isRemote()) {
            x += v.x
            y += v.y
        }
    }

    override fun velAddNet(vx: Float, vy: Float) {
        vel.add(vx, vy)
        if (this.isRemote()) {
            x += vx
            y += vy
        }
    }

    override fun updateLastPosition() {
        deltaX = x - lastX
        deltaY = y - lastY
        lastX = x
        lastY = y
    }

    override fun solidity(): EntityCollisions.SolidPred {
        return EntityCollisions.SolidPred { x, y ->
            EntityCollisions.solid(x, y)
        }
    }

    override fun canPassOn(): Boolean {
        return canPass(tileX(), tileY())
    }

    override fun canPass(tileX: Int, tileY: Int): Boolean {
        val s = this.solidity()
        return !s.solid(tileX, tileY)
    }

    override fun vel() = vel

    override fun ignoreSolids(): Boolean = false

    override fun moving() = (!this.vel.isZero(0.01f))
    var drag = 0.05f
    override fun drag() = drag

    override fun drag(drag: Float) {
        this.drag = drag
    }

    override fun move(v: Vec2) {
        move(v.x, v.y)
    }

    override fun move(cx: Float, cy: Float) {
        val check = solidity()
        Vars.collisions.move(this, cx, cy, check)
    }

    override fun floorOn(): Floor? {
        val tile = tileOn()
        return if (tile == null || tile.block() !== Blocks.air) Blocks.air as Floor else tile.floor()
    }

    override fun buildOn(): Building? = Vars.world.buildWorld(x, y)
    override fun onSolid(): Boolean {
        val tile = tileOn()
        return tile == null || tile.solid()
    }

    override fun getX() = x
    override fun getY() = y
    override fun x() = x
    override fun y() = y
    override fun tileX(): Int {
        return World.toTile(x)
    }

    override fun tileY(): Int {
        return World.toTile(y)
    }

    override fun blockOn(): Block? {
        val tile = tileOn()
        return if (tile == null) Blocks.air else tile.block()
    }

    override fun tileOn(): Tile? = Vars.world.tileWorld(x, y)
    override fun set(pos: Position) {
        set(pos.x, pos.y)
    }

    override fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun trns(pos: Position) {
        trns(pos.x, pos.y)
    }

    override fun trns(x: Float, y: Float) {
        set(this.x + x, this.y + y)
    }

    override fun x(x: Float) {
        // 将传入的x值赋给当前对象的x属性
        this.x = x
    }

    override fun y(y: Float) {
        this.y = y
    }

    override fun mass(): Float {
        return hitSize * hitSize * Math.PI.toFloat()
    }

    @Transient
    var physref: PhysicsProcess.PhysicRef? = null
    override fun physref(): PhysicsProcess.PhysicRef? {
        return physref
    }

    override fun impulse(v: Vec2) {
        impulse(v.x, v.y)
    }

    override fun impulse(x: Float, y: Float) {
        val mass = mass()
        vel.add(x / mass, y / mass)
    }

    override fun impulseNet(v: Vec2) {
        this.impulse(v.x, v.y)
        if (this.isRemote()) {
            val mass = this.mass()
            this.move(v.x / mass, v.y / mass)
        }
    }

    override fun physref(physref: PhysicsProcess.PhysicRef?) {
        this.physref = physref
    }

}