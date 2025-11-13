package ice

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
import ice.library.entities.IceRegister
import ice.library.struct.log
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.core.World
import mindustry.entities.EntityGroup
import mindustry.entities.Units
import mindustry.gen.*
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import kotlin.math.max
import kotlin.math.min

class DFW : Drawc, Hitboxc {
    @Transient
    var added: Boolean = false
    var id = EntityGroup.nextId()
    private var x = 0f
    private var y = 0f
    var index__draw = -1
    var index__all = -1
    var vel = Vec2(0f, 0f)
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var deltaX: Float = 0f
    private var deltaY: Float = 0f
    private var hitSize = 8f
    override fun clipSize(): Float = 200f
    override fun draw() {
        Draw.z(60f)
        Draw.rect(Items.copper.uiIcon, x, y)
    }

    override fun <T : Entityc?> self(): T? = this as T?
    override fun <T : Any?> `as`(): T? = this as T?
    override fun isAdded() = added
    override fun isLocal() = true
    override fun isRemote() = false
    override fun serialize() = true
    override fun classId(): Int = IceRegister.getId(DFW::class.java)
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
        index__all = Groups.all.addIndex(this)
        index__draw = Groups.draw.addIndex(this)
        added = true
        updateLastPosition()
    }

    override fun afterRead() {

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

    override fun afterReadAll() {

    }

    override fun beforeWrite() {

    }

    override fun id(id: Int) {
        this.id = id
    }


    override fun remove() {
        if (!added) return
        Groups.all.removeIndex(this, index__all)
        index__all = -1
        Groups.draw.removeIndex(this, index__draw)
        index__draw = -1
        added = false
    }

    val temp = Vec2()
    override fun update() {
        Units.nearby(Rect(x - 8 / 2, y - 8f / 2, 8f, 8f)) {
            vel.set(this).sub(it).scl(0.05f)
            log {
                vel
            }
        }
        val px = x
        val py = y
        move(vel.x * Time.delta, vel.y * Time.delta)
        if (Mathf.equal(px, x)) vel.x = 0f
        if (Mathf.equal(py, y)) vel.y = 0f
        vel.scl(max(1.0f - 0.2f * Time.delta, 0f))
    }

    override fun updateLastPosition() {
        deltaX = x - lastX
        deltaY = y - lastY
        lastX = x
        lastY = y
    }

    fun move(cx: Float, cy: Float) {
        x += cx
        y += cy
    }

    override fun read(read: Reads) {
        this.x = read.f()
        this.y = read.f()
    }

    override fun write(write: Writes) {
        write.f(this.x)
        write.f(this.y)
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

    override fun trns(p0: Position?) {

    }

    override fun trns(p0: Float, p1: Float) {

    }

    override fun x(x: Float) {
        this.x = x
    }

    override fun y(y: Float) {
        this.y = y
    }
}