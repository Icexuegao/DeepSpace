package ice.world.content.unit.weapon

import arc.Core
import arc.func.Cons2
import arc.func.Func
import arc.graphics.Blending
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import ice.Ice
import ice.graphics.IceColor
import ice.world.content.unit.type.WeaponMount
import ice.world.meta.IceStatValues
import mindustry.Vars
import mindustry.audio.SoundLoop
import mindustry.entities.Predict
import mindustry.entities.Sized
import mindustry.entities.bullet.BulletType
import mindustry.entities.part.DrawPart
import mindustry.gen.Player
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min

open class IceWeapon(name: String = "") : Weapon(name) {
    var reloadInterp: Interp = Interp.one
    var interpTime = 2f
    var customDisplay: Cons2<BulletType, Table>? = null
    var override: Boolean = false

    init {
        mountType = Func {
            WeaponMount(it)
        }
    }

    override fun addStats(u: UnitType, t: Table) {
        if (inaccuracy > 0) {
            t.row()
            val text = "[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + inaccuracy.toInt() + " " + StatUnit.degrees.localized()
            t.add(text)
        }
        if (!alwaysContinuous && reload > 0 && !bullet.killShooter) {
            t.row()
            val text = "[lightgray]" + Stat.reload.localized() + ": " + (if (mirror) "2x " else "") + "[white]" + Strings.autoFixed(
                60f / reload * shoot.shots, 2
            ) + " " + StatUnit.perSecond.localized()
            t.add(text)
        }
        if (!override) {
            //UIUtils.buildAmmo(t, bullet);
        }
        customDisplay?.get(bullet, t)

        IceStatValues.ammo(ObjectMap.of(u, bullet)).display(t)
    }

    override fun draw(unit: Unit, mount: mindustry.entities.units.WeaponMount) {

        //apply layer offset, roll it back at the end
        val z = Draw.z()
        Draw.z(z + layerOffset)

        val rotation = unit.rotation - 90
        val realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil
        val weaponRotation = rotation + (if (rotate) mount.rotation else baseRotation)
        val wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0f, -realRecoil)
        val wy = unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0f, -realRecoil)

        if (shadow > 0) {
            Drawf.shadow(wx, wy, shadow)
        }

        if (top) {
            drawOutline(unit, mount)
        }

        if (parts.size > 0) {
            DrawPart.params.set(mount.warmup, mount.reload / reload, mount.smoothReload, mount.heat, mount.recoil, mount.charge, wx, wy, weaponRotation + 90)
            DrawPart.params.sideMultiplier = if (flipSprite) -1 else 1

            for (i in 0..<parts.size) {
                val part = parts.get(i)
                DrawPart.params.setRecoil(if (part.recoilIndex >= 0 && mount.recoils != null) mount.recoils[part.recoilIndex] else mount.recoil)
                if (part.under) {
                    unit.type.applyColor(unit)
                    part.draw(DrawPart.params)
                }
            }
        }

        val prev = Draw.xscl

        Draw.xscl *= -Mathf.sign(flipSprite).toFloat()

        //fix color
        unit.type.applyColor(unit)

        if (region.found()) Draw.rect(region, wx, wy, weaponRotation)

        if (cellRegion.found()) {
            Draw.color(unit.type.cellColor(unit))
            Draw.rect(cellRegion, wx, wy, weaponRotation)
            Draw.color()
        }

        if (heatRegion.found() && mount.heat > 0) {
            Draw.color(heatColor, mount.heat)
            Draw.blend(Blending.additive)
            Draw.rect(heatRegion, wx, wy, weaponRotation)
            Draw.blend()
            Draw.color()
        }

        Draw.xscl = prev

        if (parts.size > 0) {
            //TODO does it need an outline?
            for (i in 0..<parts.size) {
                val part = parts.get(i)
                DrawPart.params.setRecoil(if (part.recoilIndex >= 0 && mount.recoils != null) mount.recoils[part.recoilIndex] else mount.recoil)
                if (!part.under) {
                    unit.type.applyColor(unit)
                    part.draw(DrawPart.params)
                }
            }
        }

        Draw.xscl = 1f

        if (mount.shoot && Ice.configIce.启用调试模式) {
            if (mount.aimX != 0f && mount.aimY != 0f && Mathf.len(mount.aimX - wx, mount.aimY - wy) <= 1200f) {
                Draw.z(z + 1f)
                Lines.stroke(1f)
                if (unit.controller() === Vars.player) {
                    Draw.color(IceColor.b4)
                } else {
                    Draw.color(unit.team.color)
                }
                Draw.alpha(0.8f)
                Lines.line(wx, wy, mount.aimX, mount.aimY)
                if (unit.controller() !is Player || Core.settings.getInt("unitTargetType") == 0) Lines.spikes(mount.aimX, mount.aimY, 4f, 4f, 4, (atan((mount.aimX - wx) / (mount.aimY - wy) * Mathf.doubleRadDeg)).toFloat() + 45f)
                Draw.reset()
            }
        }

        Draw.z(z)
    }

    open fun init(unit: Unit, mount: WeaponMount) {

    }

    override fun update(unit: Unit, mount: mindustry.entities.units.WeaponMount) {
        mount as WeaponMount
        if (!mount.init) {
            mount.init = true
            init(unit, mount)
        }
        val can = unit.canShoot()
        val lastReload = mount.reload
        if (mount.reload > 0) {
            mount.warmups = min(mount.warmups + (1 / (60f * interpTime)) * Time.delta, 1f)
        } else {
            mount.warmups = max(mount.warmups - (1 / (60f * interpTime)) * Time.delta, 0f)
        }
        mount.reload = max(mount.reload - Time.delta * unit.reloadMultiplier * reloadInterp.apply(mount.warmups), 0f)
        mount.recoil = Mathf.approachDelta(mount.recoil, 0f, unit.reloadMultiplier / recoilTime)
        if (recoils > 0) {
            if (mount.recoils == null) mount.recoils = FloatArray(recoils)
            for (i in 0..<recoils) {
                mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0f, unit.reloadMultiplier / recoilTime)
            }
        }
        mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed)
        mount.charge = if (mount.charging && shoot.firstShotDelay > 0) Mathf.approachDelta(
            mount.charge, 1f, 1 / shoot.firstShotDelay
        ) else 0f
        val warmupTarget = if ((can && mount.shoot) || (continuous && mount.bullet != null) || mount.charging) 1f else 0f
        if (linearWarmup) {
            mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed)
        } else {
            mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed)
        }
        val mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
        val mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)
        //寻找新目标
        if (!controllable && autoTarget) {
            if ((Time.delta.let { mount.retarget -= it; mount.retarget }) <= 0f) {
                mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround)
                mount.retarget = if (mount.target == null) targetInterval else targetSwitchInterval
            }

            if (mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)) {
                mount.target = null
            }
            var shoot = false

            if (mount.target != null) {
                val target = mount.target
                shoot = mount.target.within(
                    mountX, mountY, bullet.range + abs(shootY) + (if (target is Sized) target.hitSize() / 2f else 0f)
                ) && can

                if (predictTarget) {
                    val to = Predict.intercept(unit, mount.target, bullet.speed)
                    mount.aimX = to.x
                    mount.aimY = to.y
                } else {
                    mount.aimX = mount.target.x()
                    mount.aimY = mount.target.y()
                }
            }

            mount.rotate = shoot
            mount.shoot = mount.rotate
            //note that shooting state is not affected, as these cannot be controlled
            //logic will return shooting as false even if these return true, which is fine
        }
        //旋转（如果适用）
        if (rotate && (mount.rotate || mount.shoot) && can) {
            val axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
            val axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta)
            if (rotationLimit < 360) {
                val dst = Angles.angleDist(mount.rotation, baseRotation)
                if (dst > rotationLimit / 2f) {
                    mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit / 2f)
                }
            }
        } else if (!rotate) {
            mount.rotation = baseRotation
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY)
        }
        val weaponRotation = unit.rotation - 90 + (if (rotate) mount.rotation else baseRotation)
        val bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY)
        val bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY)
        val shootAngle = bulletRotation(unit, mount, bulletX, bulletY)

        if (alwaysShooting) mount.shoot = true
        //更新连续状态
        if (continuous && mount.bullet != null) {
            if (!mount.bullet.isAdded || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type !== bullet) {
                mount.bullet = null
            } else {
                mount.bullet.rotation(weaponRotation + 90)
                mount.bullet.set(bulletX, bulletY)
                mount.reload = reload
                mount.recoil = 1f
                unit.vel.add(Tmp.v1.trns(mount.bullet.rotation() + 180f, mount.bullet.type.recoil * Time.delta))
                if (shootSound !== Sounds.none && !Vars.headless) {
                    if (mount.sound == null) mount.sound = SoundLoop(shootSound, 1f)
                    mount.sound.update(bulletX, bulletY, true)
                }
                //target length of laser
                val shootLength = min(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY), range())
                //current length of laser
                val curLength = Mathf.dst(bulletX, bulletY, mount.bullet.aimX, mount.bullet.aimY)
                //resulting length of the bullet (smoothed)
                val resultLength = Mathf.approachDelta(curLength, shootLength, aimChangeSpeed)
                //actual aim end point based on length
                Tmp.v1.trns(shootAngle, resultLength.also { mount.lastLength = it }).add(bulletX, bulletY)

                mount.bullet.aimX = Tmp.v1.x
                mount.bullet.aimY = Tmp.v1.y

                if (alwaysContinuous && mount.shoot) {
                    mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup
                    mount.bullet.keepAlive = true

                    unit.apply(shootStatus, shootStatusDuration)
                }
            }
        } else {
            //heat decreases when not firing
            mount.heat = max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0f)

            if (mount.sound != null) {
                mount.sound.update(bulletX, bulletY, false)
            }
        }
        //翻转武器射击侧以交替武器
        val wasFlipped = mount.side
        if (otherSide != -1 && alternate && mount.side == flipSprite && mount.reload <= reload / 2f && lastReload > reload / 2f) {
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side
            mount.side = !mount.side
        }
        //如果适用，请拍摄
        if (mount.shoot &&  //must be shooting
            can &&  //must be able to shoot
            !(bullet.killShooter && mount.totalShots > 0) &&  //if the bullet kills the shooter, you should only ever be able to shoot once
            (!useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&  //check ammo
            (!alternate || wasFlipped == flipSprite) && mount.warmup >= minWarmup &&  //must be warmed up
            unit.vel.len() >= minShootVelocity &&  //check velocity requirements
            (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) &&  //reload has to be 0, or it has to be an always-continuous weapon
            (alwaysShooting || Angles.within(
                if (rotate) mount.rotation else unit.rotation + baseRotation, mount.targetRotation, shootCone
            )) //has to be within the cone
        ) {
            shoot(unit, mount, bulletX, bulletY, shootAngle)

            mount.reload = reload

            if (useAmmo) {
                unit.ammo--
                if (unit.ammo < 0) unit.ammo = 0f
            }
        }
    }
}