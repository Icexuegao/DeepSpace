package ice.world.content.unit.entity.base

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Interval
import arc.util.Time
import arc.util.Tmp
import ice.entities.IceRegister
import ice.graphics.IceColor
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ICollideBlockerAbility
import mindustry.Vars
import mindustry.async.PhysicsProcess
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.EntityCollisions
import mindustry.entities.Leg
import mindustry.gen.*
import mindustry.graphics.Drawf
import mindustry.graphics.InverseKinematics
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.world.blocks.ConstructBlock
import mindustry.world.blocks.ConstructBlock.ConstructBuild
import mindustry.world.blocks.environment.Floor
import kotlin.math.abs
import kotlin.math.max

open class Entity : UnitEntity(), Legsc, Tankc {
  companion object {
    val straightVec: Vec2 = Vec2()
  }

  override fun collides(other: Hitboxc): Boolean {
    for (ability in abilities) {
      if (ability is ICollideBlockerAbility && ability.blockedCollides(this, other)) return false
    }

    return super.collides(other)
  }

  open fun drawShadow() {
    val e: Float = Mathf.clamp(elevation, type.shadowElevation, 1f) * type.shadowElevationScl * (1f - drownTime)
    val x: Float = x + UnitType.shadowTX * e
    val y: Float = y + UnitType.shadowTY * e
    val floor = Vars.world.floorWorld(x, y)
    val dest = if (floor.canShadow) 1f else 0f
    //yes, this updates state in draw()... which isn't a problem, because I don't want it to be obvious anyway
    shadowAlpha = if (shadowAlpha < 0) dest else Mathf.approachDelta(shadowAlpha, dest, 0.11f)
    Draw.color(Pal.shadow, Pal.shadow.a * shadowAlpha)
    drawShadowRegion(x, y, rotation - 90f)
    Draw.color()
  }

  open fun drawShadowRegion(x: Float, y: Float, rotation: Float) {
    Draw.rect(type.shadowRegion, x, y, rotation)
  }

  open fun drawBody() {
    type().applyColor(this)
    if (this is UnderwaterMovec) {
      Draw.alpha(1f)
      Draw.mixcol(floorOn().mapColor.write(Tmp.c1).mul(0.9f), 1f)
    }
    drawBodyRegion(rotation - 90f)
    Draw.reset()
  }

  open fun drawBodyRegion(rotation: Float) {
    Draw.rect(type().region, x, y, rotation)
  }

  override fun drawBuilding() {
    val active = activelyBuilding()
    if (!active && lastActive == null) return
    Draw.z(Layer.flyingUnit)
    val plan = if (active) buildPlan() else lastActive
    val tile = plan!!.tile()
    val core = team.core()
    if (tile == null || !within(
        plan, if (Vars.state.rules.infiniteResources) Float.MAX_VALUE else type.buildRange
      )
    ) {
      return
    }
    if (core != null && active && !isLocal && (tile.block() !is ConstructBlock)) {
      Draw.z(Layer.plans - 1.0f)
      drawPlan(plan, 0.5f)
      drawPlanTop(plan, 0.5f)
      Draw.z(Layer.flyingUnit)
    }
    if (type.drawBuildBeam) {
      val focusLen = type.buildBeamOffset + Mathf.absin(Time.time, 3.0f, 0.6f)
      val px = x + Angles.trnsx(rotation, focusLen)
      val py = y + Angles.trnsy(rotation, focusLen)
      drawBuildingBeam(px, py)
    }
  }

  override fun drawBuildingBeam(px: Float, py: Float) {
    val active = this.activelyBuilding()
    if (active || this.lastActive != null) {
      Draw.z(115.0f)
      val plan = if (active) this.buildPlan() else this.lastActive
      val tile = Vars.world.tile(plan!!.x, plan.y)
      if (tile != null && this.within(
          plan, if (Vars.state.rules.infiniteResources) Float.MAX_VALUE else this.type.buildRange
        )
      ) {
        val size = if (plan.breaking) (if (active) tile.block().size else this.lastSize) else plan.block.size
        val tx = plan.drawx()
        val ty = plan.drawy()
        Lines.stroke(1.0f, if (plan.breaking) Pal.remove else IceColor.b4)
        Draw.z(122.0f)
        Draw.alpha(this.buildAlpha)
        if (!active && tile.build !is ConstructBuild) {
          Fill.square(plan.drawx(), plan.drawy(), (size * 8).toFloat() / 2.0f)
        }

        Drawf.buildBeam(px, py, tx, ty, (8 * size).toFloat() / 2.0f)
        Fill.square(px, py, 1.8f + Mathf.absin(Time.time, 2.2f, 1.1f), this.rotation + 45.0f)
        Draw.reset()
        Draw.z(115.0f)
      }
    }
  }

  override fun collisionLayer(): Int {
    if (isFlying) return PhysicsProcess.layerFlying
    if (type.allowLegStep && type.legPhysicsLayer) PhysicsProcess.layerLegs
    return PhysicsProcess.layerGround
  }

  override fun classId(): Int {
    return IceRegister.getId(this::class.java)
  }

  override fun approach(vector: Vec2) {
    if (type.treadRegion.found() && !vector.isZero(0.001f)) {
      walked = true
    }

    vel.approachDelta(vector, type.accel * speed())
  }

  override fun solidity(): EntityCollisions.SolidPred? {
    if (isFlying) return null

    if (type.allowLegStep) {
      return EntityCollisions.SolidPred { x: Int, y: Int ->
        EntityCollisions.legsSolid(x, y)
      }
    }
    return EntityCollisions.SolidPred { x: Int, y: Int -> EntityCollisions.solid(x, y) }
  }

  override fun moveAt(vector: Vec2, acceleration: Float) {
    if (!vector.isZero(0.001f)) {
      walked = true
    }
    val t = tmp1.set(vector)
    tmp2.set(t).sub(vel).limit(acceleration * vector.len() * Time.delta)
    vel.add(tmp2)
  }

  override fun update() {
    super.update()
    if (type.legRegion.found()) updateLegs()
    if (type.treadRegion.found()) updateTank()
  }

  override fun walked(walked: Boolean) {
    this.walked = walked
  }

  @Transient
  var walked = false

  @Transient
  var treadEffectTime = 0f

  @Transient
  var treadTime = 0f
  var dawd = Interval()

  @Transient
  var lastSlowdown = 1f
  fun updateTank() {
    if ((walked || (Vars.net.client() && deltaLen() >= 0.01f)) && !Vars.headless && !inFogTo(
        Vars.player.team()
      )
    ) {
      treadEffectTime += Time.delta
      if (treadEffectTime >= 6.0f && type.treadRects.size > 0) {
        val treadRect = type.treadRects[0]
        val xOffset = (-(treadRect.x + treadRect.width / 2.0f)) / 4.0f
        val yOffset = (-(treadRect.y + treadRect.height / 2.0f)) / 4.0f
        for (i in Mathf.signs) {
          Tmp.v1.set(xOffset * i, yOffset - treadRect.height / 2.0f / 4.0f).rotate(rotation - 90)
          Effect.floorDustAngle(type.treadEffect, Tmp.v1.x + x, Tmp.v1.y + y, rotation + 180.0f)
        }
        treadEffectTime = 0.0f
      }
    }
    lastDeepFloor = null
    var anyNonDeep = false
    val r = max((hitSize * 0.6f / Vars.tilesize).toInt(), 0)
    var solids = 0
    val total = (r * 2 + 1) * (r * 2 + 1)
    for (dx in -r..r) {
      for (dy in -r..r) {
        val t = Vars.world.tileWorld(x + dx * Vars.tilesize, y + dy * Vars.tilesize)
        if (t == null || t.solid()) {
          solids++
        }
        if (t != null && t.floor().isDeep) {
          lastDeepFloor = t.floor()
        } else {
          anyNonDeep = true
        }

        if (type.crushDamage > 0 && !disarmed && (walked || deltaLen() >= 0.01f) && t != null && max(
            abs(dx), abs(dy)
          ) <= r //-1
        ) {
          if (t.build != null && t.build.team !== team) {
            t.build.damage(
              team, type.crushDamage * Time.delta * t.block().crushDamageMultiplier * Vars.state.rules.unitDamage(
                team
              )
            )
          } else if (t.block().unitMoveBreakable) {
            ConstructBlock.deconstructFinish(t, t.block(), this)
          }
        }
      }
    }
    if (anyNonDeep) {
      lastDeepFloor = null
    }
    lastSlowdown = Mathf.lerp(
      1.0f, type.crawlSlowdown, Mathf.clamp(solids.toFloat() / total / type.crawlSlowdownFrac)
    )
    if (walked || Vars.net.client()) {
      val len = deltaLen()
      treadTime += len
      walked = false
    }
  }

  fun drawTank() {
    type.apply {
      applyColor(this@Entity)
      Draw.rect(treadRegion, this@Entity.x, this@Entity.y, this@Entity.rotation - 90)
      if (treadRegion.found()) {
        val frame = (this@Entity.treadTime()).toInt() % treadFrames
        for (i in treadRects.indices) {
          val region = treadRegions[i][frame]
          val treadRect = treadRects[i]
          val xOffset = -(treadRect.x + treadRect.width / 2f)
          val yOffset = -(treadRect.y + treadRect.height / 2f)

          for (side in Mathf.signs) {
            Tmp.v1.set(xOffset * side, yOffset).rotate(this@Entity.rotation - 90)
            Draw.rect(
              region, this@Entity.x + Tmp.v1.x / 4f, this@Entity.y + Tmp.v1.y / 4f, treadRect.width / 4f, region.height * region.scale / 4f, this@Entity.rotation - 90
            )
          }
        }
      }
    }
  }

  fun updateLegs() {
    if (Mathf.dst(deltaX(), deltaY()) > 0.001f) {
      baseRotation = Angles.moveToward(baseRotation, Mathf.angle(deltaX(), deltaY()), type.rotateSpeed)
    }
    if (type.lockLegBase) {
      baseRotation = rotation
    }
    val legLength = type.legLength
    if (legs.size != type.legCount) {
      resetLegs()
    }
    val moveSpeed = type.legSpeed
    val div = (legs.size / type.legGroupSize).coerceAtLeast(2)
    moveSpace = legLength / 1.6f / (div / 2.0f) * type.legMoveSpace
    totalLength += if (type.legContinuousMove) type.speed * speedMultiplier * Time.delta else Mathf.dst(
      deltaX(), deltaY()
    )
    val trns = moveSpace * 0.85f * type.legForwardScl
    val moving = moving()
    var moveOffset = if (!moving) Tmp.v4.setZero() else Tmp.v4.trns(Angles.angle(deltaX(), deltaY()), trns)
    moveOffset = curMoveOffset.lerpDelta(moveOffset, 0.1f)
    lastDeepFloor = null
    var deeps = 0
    for (i in 0..<legs.size) {
      val dstRot = legAngle(i)
      val baseOffset = legOffset(Tmp.v5, i).add(x, y)
      val l: Leg = legs[i]!!
      l.joint.sub(baseOffset).clampLength(type.legMinLength * legLength / 2.0f, type.legMaxLength * legLength / 2.0f).add(baseOffset)
      l.base.sub(baseOffset).clampLength(type.legMinLength * legLength, type.legMaxLength * legLength).add(baseOffset)
      val stageF = (totalLength + i * type.legPairOffset) / moveSpace
      val stage = stageF.toInt()
      val group = stage % div
      val move = i % div == group
      var side = i < legs.size / 2
      val backLeg = abs((i + 0.5f) - legs.size / 2.0f) <= 0.501f
      if (backLeg && type.flipBackLegs) side = !side
      if (type.flipLegSide) side = !side
      l.moving = move
      l.stage = if (moving) stageF % 1.0f else Mathf.lerpDelta(l.stage, 0.0f, 0.1f)
      val floor = Vars.world.floorWorld(l.base.x, l.base.y)
      if (floor.isDeep) {
        deeps++
        lastDeepFloor = floor
      }
      if (l.group != group) {
        if (!move && (moving || !type.legContinuousMove) && i % div == l.group) {
          if (!Vars.headless && !inFogTo(Vars.player.team())) {
            if (floor.isLiquid) {
              floor.walkEffect.at(l.base.x, l.base.y, type.rippleScale, floor.mapColor)
              floor.walkSound.at(x, y, 1.0f, floor.walkSoundVolume)
            } else {
              Fx.unitLandSmall.at(l.base.x, l.base.y, type.rippleScale, floor.mapColor)
            }
            if (type.stepShake > 0) {
              Effect.shake(type.stepShake, type.stepShake, l.base)
            }
          }
          if (type.legSplashDamage > 0 && !disarmed) {
            Damage.damage(
              team, l.base.x, l.base.y, type.legSplashRange, type.legSplashDamage * Vars.state.rules.unitDamage(team), false, true
            )
            val tile = Vars.world.tileWorld(l.base.x, l.base.y)
            if (tile != null && tile.block().unitMoveBreakable) {
              ConstructBlock.deconstructFinish(tile, tile.block(), this)
            }
          }
        }
        l.group = group
      }
      val legDest = Tmp.v1.trns(dstRot, legLength * type.legLengthScl).add(baseOffset).add(moveOffset)
      val jointDest = Tmp.v2
      InverseKinematics.solve(
        legLength / 2.0f, legLength / 2.0f, Tmp.v6.set(l.base).sub(baseOffset), side, jointDest
      )
      jointDest.add(baseOffset)
      Tmp.v6.set(baseOffset).lerp(l.base, 0.5f)
      if (move) {
        val moveFract = stageF % 1.0f
        l.base.lerpDelta(legDest, moveFract)
        l.joint.lerpDelta(jointDest, moveFract / 2.0f)
      }
      l.joint.lerpDelta(jointDest, moveSpeed / 4.0f)
      l.joint.sub(baseOffset).clampLength(type.legMinLength * legLength / 2.0f, type.legMaxLength * legLength / 2.0f).add(baseOffset)
      l.base.sub(baseOffset).clampLength(type.legMinLength * legLength, type.legMaxLength * legLength).add(baseOffset)
    }
    if (deeps != legs.size || !floorOn().isDeep) {
      lastDeepFloor = null
    }
  }

  fun drawLegs() {
    type().apply {
      applyColor(this@Entity)
      Tmp.c3.set(Draw.getMixColor())
      val legs = legs()
      val ssize = footRegion.width * footRegion.scl() * 1.5f
      val rotation = baseRotation()
      val invDrown = 1f - drownTime

      if (footRegion.found()) {
        for (leg in legs) {
          leg!!
          Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown)
        }
      }
      //腿先画在前面
      for (j in legs.indices.reversed()) {
        val i = (if (j % 2 == 0) j / 2 else legs.size - 1 - j / 2)
        val leg = legs[i]!!
        val flip = i >= legs.size / 2f
        val flips = Mathf.sign(flip)
        val position: Vec2 = legOffset(IceUnitType.legOffsetIce, i).add(this@Entity)

        Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension)

        if (footRegion.found() && leg.moving && shadowElevation > 0) {
          val scl: Float = shadowElevation * invDrown
          val elev = Mathf.slope(1f - leg.stage) * scl
          Draw.color(Pal.shadow)
          Draw.rect(
            footRegion, leg.base.x + UnitType.shadowTX * elev, leg.base.y + UnitType.shadowTY * elev, position.angleTo(leg.base)
          )
          Draw.color()
        }

        Draw.mixcol(Tmp.c3, Tmp.c3.a)

        if (footRegion.found()) {
          Draw.rect(footRegion, leg.base.x, leg.base.y, position.angleTo(leg.base))
        }

        if (legBaseUnder) {
          Lines.stroke(legBaseRegion.height * legRegion.scl() * flips)
          Lines.line(
            legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false
          )

          Lines.stroke(legRegion.height * legRegion.scl() * flips)
          Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false)
        } else {
          Lines.stroke(legRegion.height * legRegion.scl() * flips)
          Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false)

          Lines.stroke(legBaseRegion.height * legRegion.scl() * flips)
          Lines.line(
            legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false
          )
        }

        if (jointRegion.found()) {
          Draw.rect(jointRegion, leg.joint.x, leg.joint.y)
        }
      }
      //base joints are drawn after everything else
      if (baseJointRegion.found()) {
        for (j in legs.indices.reversed()) {
          //TODO does the index / draw order really matter?
          val position: Vec2 = legOffset(
            IceUnitType.legOffsetIce, (if (j % 2 == 0) j / 2 else legs.size - 1 - j / 2)
          ).add(this@Entity)
          Draw.rect(baseJointRegion, position.x, position.y, rotation)
        }
      }

      if (baseRegion.found()) {
        Draw.rect(baseRegion, x, y, rotation - 90)
      }

      Draw.reset()
    }
  }

  @Transient
  var baseRotation: Float = 0f

  @Transient
  var curMoveOffset: Vec2 = Vec2()

  @Transient
  var legs = arrayOf<Leg?>()

  @Transient
  var moveSpace: Float = 0f

  @Transient
  var totalLength: Float = 0f

  @Transient
  var lastDeepFloor: Floor? = null
  override fun legAngle(index: Int): Float {
    if (type.legStraightness > 0) {
      return Mathf.slerp(
        defaultLegAngle(index), (if (index >= legs.size / 2) -90f else 90.0f) + baseRotation, type.legStraightness
      )
    }
    return defaultLegAngle(index)
  }

  override fun curMoveOffset(): Vec2 = curMoveOffset
  override fun legOffset(out: Vec2, index: Int): Vec2 {
    out.trns(defaultLegAngle(index), type.legBaseOffset)
    if (type.legStraightness > 0) {
      straightVec.trns(defaultLegAngle(index) - baseRotation, type.legBaseOffset)
      straightVec.y = Mathf.sign(straightVec.y) * type.legBaseOffset * type.legStraightLength
      straightVec.rotate(baseRotation)
      out.lerp(straightVec, type.baseLegStraightness)
    }
    return out
  }

  override fun baseRotation(): Float = baseRotation
  override fun defaultLegAngle(index: Int): Float {
    return baseRotation + 360.0f / legs.size * index + (360.0f / legs.size / 2.0f)
  }

  override fun moveSpace(): Float = moveSpace
  override fun totalLength(): Float = totalLength
  override fun legs(): Array<out Leg?> = legs
  override fun walked() = walked
  override fun treadTime() = treadTime
  override fun lastDeepFloor(): Floor? = lastDeepFloor
  override fun baseRotation(baseRotation: Float) {
    this.baseRotation = baseRotation
  }

  override fun curMoveOffset(curMoveOffset: Vec2) {
    this.curMoveOffset = curMoveOffset
  }

  override fun lastDeepFloor(lastDeepFloor: Floor?) {
    this.lastDeepFloor = lastDeepFloor
  }

  override fun treadTime(treadTime: Float) {
    this.treadTime = treadTime
  }

  override fun legs(legs: Array<Leg?>) {
    this.legs = legs
  }

  override fun moveSpace(moveSpace: Float) {
    this.moveSpace = moveSpace
  }

  override fun resetLegs() {
    resetLegs(type.legLength)
  }

  override fun resetLegs(legLength: Float) {
    val count = type.legCount
    this.legs = arrayOfNulls(count)
    if (type.lockLegBase) {
      baseRotation = rotation
    }
    for (i in legs.indices) {
      val l = Leg()
      val dstRot = legAngle(i)
      val baseOffset = legOffset(Tmp.v5, i).add(x, y)
      l.joint.trns(dstRot, legLength / 2.0f).add(baseOffset)
      l.base.trns(dstRot, legLength).add(baseOffset)
      legs[i] = l
    }
    totalLength = Mathf.random(100.0f)
  }

  override fun totalLength(totalLength: Float) {
    this.totalLength = totalLength
  }
}