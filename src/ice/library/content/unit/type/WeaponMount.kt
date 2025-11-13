package ice.library.content.unit.type

import arc.Core
import arc.graphics.Blending
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import ice.library.content.unit.IceWeapon
import ice.vars.SettingValue
import mindustry.Vars
import mindustry.entities.part.DrawPart
import mindustry.entities.units.WeaponMount
import mindustry.gen.Player
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Weapon
import kotlin.math.atan

class WeaponMount(weapon: Weapon) : WeaponMount(weapon) {
    var warmups = 0f
    fun draw(unit: Unit, weapon: IceWeapon) {
        //apply layer offset, roll it back at the end
        val z = Draw.z()
        Draw.z(z + weapon.layerOffset)
        val rotation = unit.rotation - 90f
        val realRecoil = Mathf.pow(recoil, weapon.recoilPow) * weapon.recoil
        val weaponRotation = rotation + if (weapon.rotate) this.rotation else weapon.baseRotation
        val wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, -realRecoil)
        val wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, -realRecoil)

        if (weapon.shadow > 0) {
            Drawf.shadow(wx, wy, weapon.shadow)
        }

        if (weapon.top) {
            weapon.drawOutline(unit, this)
        }

        weapon.parts.forEach { part ->
            DrawPart.params.set(warmup, reload / weapon.reload, smoothReload, heat, recoil, charge, wx, wy,
                weaponRotation + 90)
            DrawPart.params.sideMultiplier = if (weapon.flipSprite) -1 else 1
            val recoils1 = part.recoilIndex >= 0 && recoils != null

            DrawPart.params.setRecoil(if (recoils1) recoils[part.recoilIndex] else recoil)
            if (!part.under) {
                unit.type.applyColor(unit)
                part.draw(DrawPart.params)
            }
        }
        val prev = Draw.xscl

        Draw.xscl *= -Mathf.sign(weapon.flipSprite)
        //fix color
        unit.type.applyColor(unit)

        if (weapon.region.found()) Draw.rect(weapon.region, wx, wy, weaponRotation)

        if (weapon.cellRegion.found()) {
            Draw.color(unit.type.cellColor(unit))
            Draw.rect(weapon.cellRegion, wx, wy, weaponRotation)
            Draw.color()
        }

        if (weapon.heatRegion.found() && heat > 0) {
            Draw.color(weapon.heatColor, heat)
            Draw.blend(Blending.additive)
            Draw.rect(weapon.heatRegion, wx, wy, weaponRotation)
            Draw.blend()
            Draw.color()
        }

        Draw.xscl = prev

        weapon.parts.forEach { part ->
            val recoils1 = part.recoilIndex >= 0 && recoils != null
            DrawPart.params.setRecoil(if (recoils1) recoils[part.recoilIndex] else recoil)
            if (!part.under) {
                unit.type.applyColor(unit)
                part.draw(DrawPart.params)
            }
        }

        Draw.xscl = 1f

        if (shoot && SettingValue.debugMode) {
            if (aimX.toInt() != 0 && aimY.toInt() != 0 && Mathf.len(aimX - wx, aimY - wy) <= 1200f) {
                Draw.z(z + 1f)
                Lines.stroke(1f)
                if (unit.controller() == Vars.player) {
                    Draw.color(Pal.accent)
                } else {
                    Draw.color(unit.team.color)
                }
                Draw.alpha(0.8f)
                Lines.line(wx, wy, aimX, aimY)

                if (unit.controller() !is Player || Core.settings.getInt("unitTargetType") == 0) {
                    Lines.spikes(aimX, aimY, 4f, 4f, 4,
                        ((atan((aimX - wx) / (aimY - wy) * Mathf.doubleRadDeg)) + 45f).toFloat())
                }

                Draw.reset()
            }
        }

        Draw.z(z)
    }
}