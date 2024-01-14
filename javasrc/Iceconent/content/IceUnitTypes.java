package Iceconent.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.SapBulletType;
import mindustry.gen.EntityMapping;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;

public class IceUnitTypes {
    public static UnitType ars;

    public static void load() {
        ars = new UnitType("ars") {{
            constructor = EntityMapping.map(29);/** 构造函数  */
            drag = 0.1f;/** 子弹每帧降低速度  */
            speed = 0.62f;/** 单位移动速度  */
            hitSize = 23f;/**命中大小*/
            health = 8000;/**生命值*/
            armor = 6f;/**护甲*/
            rotateSpeed = 2.7f;/**旋转速度*/
            legCount = 6;/**腿数*/
            legMoveSpace = 1f;/**腿移动空间 */
            legPairOffset = 3;/**腿对偏移量*/
            legLength = 30f;/** 腿长  */
            legExtension = -15;/** 腿部伸展  */
            legBaseOffset = 10f;/** 腿基偏移  */
            stepShake = 1f;/** 腿部摇晃  */
            legLengthScl = 0.96f;
            rippleScale = 2f;/** 波纹尺度  */
            legSpeed = 0.2f;/** 腿移动速度  */
            ammoType = new PowerAmmoType(2000);/** 子弹类型  */
            legSplashDamage = 32;/** 踩踏溅伤  */
            legSplashRange = 30;/** 腿部溅射范围  */
            drownTimeMultiplier = 2f;/** 淹没时间乘数  */
            hovering = true;/** 盘旋  */
            shadowElevation = 0.65f;/** 阴影高程  */
            groundLayer = Layer.legUnit;/** 贴图层数  */
            BulletType sapper = new SapBulletType() {{
                sapStrength = 0.85f;/**子弹吸血*/
                width = 0.55f;/** 宽度  */
                length = 55f;/** 长度  */
                lifetime = 30f;/** 子弹持续时间  */
                damage = 40;/** 伤害  */
                shootEffect = Fx.shootSmall;/**  射击效果 */
                hitColor = color = Color.valueOf("bf92f9");/** 命中颜色  */
                despawnEffect = Fx.none;/** 消失效果  */
                knockback = -1f;/** 击退  */
            }};
            weapons.add(new Weapon("spiroct-weapon") {{
                reload = 9f;/** 装弹时间  */
                x = 4f;  /** 水平位置  */
                y = 8f;/** 竖直位置  */
                rotate = true;/** 武器是否可以旋转  */
                bullet = sapper;/** 子弹类型  */
                shootSound = Sounds.sap;/** 击中音效  */
            }}, new Weapon("spiroct-weapon") {{
                reload = 14f;
                x = 9f;
                y = 6f;
                rotate = true;
                bullet = sapper;
                shootSound = Sounds.sap;
            }}, new Weapon("spiroct-weapon") {{
                reload = 22f;
                x = 14f;
                y = 0f;
                rotate = true;
                bullet = sapper;
                shootSound = Sounds.sap;
            }}, new Weapon("large-purple-mount") {{
                y = -7f;
                x = 9f;
                shootY = 7f;
                reload = 45;
                shake = 3f;
                rotateSpeed = 2f;
                ejectEffect = Fx.casing1;
                shootSound = Sounds.artillery;
                rotate = true;
                shadow = 8f;
                recoil = 3f;
                bullet = new ArtilleryBulletType(2f, 12) {{
                    hitEffect = Fx.sapExplosion;
                    knockback = 0.8f;
                    lifetime = 70f;
                    width = height = 19f;
                    collidesTiles = true;
                    ammoMultiplier = 4f;
                    splashDamageRadius = 70f;
                    splashDamage = 65f;
                    backColor = Pal.sapBulletBack;
                    frontColor = lightningColor = Pal.sapBullet;
                    lightning = 3;
                    lightningLength = 10;
                    smokeEffect = Fx.shootBigSmoke2;
                    shake = 5f;
                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10;
                }};
            }});
        }};
    }
}
