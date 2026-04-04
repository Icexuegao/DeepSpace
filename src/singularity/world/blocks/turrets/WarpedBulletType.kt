package singularity.world.blocks.turrets

import ice.entities.bullet.base.BulletType
import mindustry.Vars
import mindustry.ai.types.MissileAI
import mindustry.ctype.Content
import mindustry.entities.Mover
import mindustry.game.Team
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.world.blocks.ControlBlock
import universecore.util.handler.ObjectHandler

open class WarpedBulletType<Type :BulletType>(inst: Type) :BulletType() {
  private val inst: Type = inst.copy() as Type

  init {
    ObjectHandler.copyField(inst, this)
  }

  override fun init() {
    super.init()
    ObjectHandler.copyFieldAsBlack(this, this.inst, "inst")
  }

  override fun create(
    owner: Entityc?,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    damage: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    data: Any?,
    mover: Mover?,
    aimX: Float,
    aimY: Float
  ): Bullet? {
    if (this.spawnUnit != null) {
      if (!Vars.net.client()) {
        val spawned = this.spawnUnit.create(team)
        spawned.set(x, y)
        spawned.rotation = angle
        if (this.spawnUnit.missileAccelTime <= 0.0f) {
          spawned.vel.trns(angle, this.spawnUnit.speed)
        }

        val var15 = spawned.controller()
        if (var15 is MissileAI) {
          if (owner is Unit) {
            var15.shooter = owner
          }

          if (owner is ControlBlock) {
            var15.shooter = owner.unit()
          }
        }

        spawned.add()
      }

      return null
    } else {
      val bullet = Bullet.create()
      bullet.type = this
      bullet.owner = owner
      bullet.team = team
      bullet.time = 0.0f
      bullet.originX = x
      bullet.originY = y
      bullet.aimTile = Vars.world.tileWorld(aimX, aimY)
      bullet.aimX = aimX
      bullet.aimY = aimY
      bullet.initVel(angle, this.speed * velocityScl)
      bullet.set(x, y)
      bullet.lifetime = this.lifetime * lifetimeScl
      bullet.data = data
      bullet.hitSize = this.hitSize
      bullet.mover = mover
      bullet.damage = (if (damage < 0.0f) this.damage else damage) * bullet.damageMultiplier()
      if (bullet.trail != null) {
        bullet.trail.clear()
      }

      bullet.add()
      if (this.keepVelocity && owner is Velc) {
        bullet.vel.add(owner.vel())
      }

      return bullet
    }
  }

  override fun drawTrail(b: Bullet?) {
    this.inst.drawTrail(b)
  }

  override fun loadIcon() {
    this.inst.loadIcon()
  }

  override fun updateBulletInterval(b: Bullet?) {
    this.inst.updateBulletInterval(b)
  }

  override fun createIncend(b: Bullet?, x: Float, y: Float) {
    this.inst.createIncend(b, x, y)
  }

  override fun updateTrail(b: Bullet?) {
    this.inst.updateTrail(b)
  }

  override fun update(b: Bullet?) {
    this.inst.update(b)
  }

  override fun postInit() {
    this.inst.postInit()
  }

  override fun compareTo(c: Content?): Int {
    return this.inst.compareTo(c)
  }

  override fun damageMultiplier(b: Bullet?): Float {
    return this.inst.damageMultiplier(b)
  }

  override fun hasErrored(): Boolean {
    return this.inst.hasErrored()
  }

  override fun createSplashDamage(b: Bullet?, x: Float, y: Float) {
    this.inst.createSplashDamage(b, x, y)
  }

  override fun hit(b: Bullet?, x: Float, y: Float) {
    this.inst.hit(b, x, y)
  }

  override fun hit(b: Bullet?) {
    this.inst.hit(b)
  }

  override fun load() {
    this.inst.load()
  }

  override fun despawned(b: Bullet) {
    this.inst.despawned(b)
  }

  override fun createUnits(b: Bullet, x: Float, y: Float) {
    this.inst.createUnits(b, x, y)
  }

  override fun createFrags(b: Bullet, x: Float, y: Float) {
    this.inst.createFrags(b, x, y)
  }

  override fun create(owner: Entityc, team: Team, x: Float, y: Float, angle: Float): Bullet {
    return this.inst.create(owner, team, x, y, angle)
  }

  override fun create(owner: Teamc, x: Float, y: Float, angle: Float): Bullet {
    return this.inst.create(owner, x, y, angle)
  }

  override fun create(
    owner: Entityc?,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    mover: Mover?
  ): Bullet? {
    return this.inst.create(owner, team, x, y, angle, velocityScl, lifetimeScl, mover)
  }

  override fun create(owner: Entityc, team: Team?, x: Float, y: Float, angle: Float, velocityScl: Float, lifetimeScl: Float): Bullet? {
    return this.inst.create(owner, team, x, y, angle, velocityScl, lifetimeScl)
  }

  override fun create(parent: Bullet, x: Float, y: Float, angle: Float): Bullet? {
    return this.inst.create(parent, x, y, angle)
  }

  override fun create(parent: Bullet, x: Float, y: Float, angle: Float, velocityScl: Float, lifeScale: Float): Bullet? {
    return this.inst.create(parent, x, y, angle, velocityScl, lifeScale)
  }

  override fun create(parent: Bullet, x: Float, y: Float, angle: Float, velocityScl: Float): Bullet? {
    return this.inst.create(parent, x, y, angle, velocityScl)
  }

  override fun create(
    owner: Entityc?,
    shooter: Entityc?,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    damage: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    data: Any?,
    mover: Mover?,
    aimX: Float,
    aimY: Float,
    target: Teamc?
  ): Bullet? {
    return this.inst.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target)
  }

  override fun create(owner: Entityc, team: Team?, x: Float, y: Float, angle: Float, velocityScl: Float): Bullet? {
    return this.inst.create(owner, team, x, y, angle, velocityScl)
  }

  override fun create(
    owner: Entityc?,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    damage: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    data: Any?,
    mover: Mover?
  ): Bullet? {
    return this.inst.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover)
  }

  override fun create(
    owner: Entityc?,
    shooter: Entityc?,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    damage: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    data: Any?,
    mover: Mover?,
    aimX: Float,
    aimY: Float
  ): Bullet? {
    return this.inst.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY)
  }

  override fun create(
    owner: Entityc,
    team: Team?,
    x: Float,
    y: Float,
    angle: Float,
    damage: Float,
    velocityScl: Float,
    lifetimeScl: Float,
    data: Any?
  ): Bullet? {
    return this.inst.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data)
  }

  override fun buildingDamage(b: Bullet?): Float {
    return this.inst.buildingDamage(b)
  }

  override fun copy(): mindustry.entities.bullet.BulletType? {
    return this.inst.copy()
  }

  override fun drawLight(b: Bullet?) {
    this.inst.drawLight(b)
  }

  override fun hitTile(b: Bullet, build: Building?, x: Float, y: Float, initialHealth: Float, direct: Boolean) {
    this.inst.hitTile(b, build, x, y, initialHealth, direct)
  }

  override fun afterPatch() {
    this.inst.afterPatch()
  }

  override fun drawParts(b: Bullet) {
    this.inst.drawParts(b)
  }

  override fun init(b: Bullet) {
    this.inst.init(b)
  }

  override fun createPuddles(b: Bullet, x: Float, y: Float) {
    this.inst.createPuddles(b, x, y)
  }

  override fun continuousDamage(): Float {
    return this.inst.continuousDamage()
  }

  override fun updateHoming(b: Bullet) {
    this.inst.updateHoming(b)
  }

  override fun heals(): Boolean {
    return this.inst.heals()
  }

  override fun isVanilla(): Boolean {
    return this.inst.isVanilla
  }

  override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
    this.inst.hitEntity(b, entity, health)
  }

  override fun createNet(team: Team, x: Float, y: Float, angle: Float, damage: Float, velocityScl: Float, lifetimeScl: Float) {
    this.inst.createNet(team, x, y, angle, damage, velocityScl, lifetimeScl)
  }

  override fun draw(b: Bullet) {
    this.inst.draw(b)
  }

  override fun estimateDPS(): Float {
    return this.inst.estimateDPS()
  }

  override fun hit(b: Bullet, x: Float, y: Float, createFrags: Boolean) {
    this.inst.hit(b, x, y, createFrags)
  }

  override fun shieldDamage(b: Bullet): Float {
    return this.inst.shieldDamage(b)
  }

  override fun testCollision(bullet: Bullet, tile: Building): Boolean {
    return this.inst.testCollision(bullet, tile)
  }

  override fun isModded(): Boolean {
    return this.inst.isModded
  }

  override fun updateWeaving(b: Bullet) {
    this.inst.updateWeaving(b)
  }

  override fun removed(b: Bullet) {
    this.inst.removed(b)
  }

  override fun handlePierce(b: Bullet, initialHealth: Float, x: Float, y: Float) {
    this.inst.handlePierce(b, initialHealth, x, y)
  }

  override fun updateTrailEffects(b: Bullet) {
    this.inst.updateTrailEffects(b)
  }
}