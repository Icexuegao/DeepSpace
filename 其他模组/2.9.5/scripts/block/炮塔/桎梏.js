const my = require("base/物品");
const lib = require("base/coflib");
const bullet = require("base/bullet");
const status = require("base/status");
const FX = require("base/Effect/fightFx");

let 桎梏 = new ItemTurret("桎梏");
桎梏.ammo(
    my.铈凝块, bullet.VerticalBulletType(2400, 300, 240)
)

lib.setBuilding(ItemTurret.ItemTurretBuild, 桎梏, {
    handleBullet(bullet, offsetX, offsetY, angleOffset) {
        Fx.launchPod.at(this);
        this.items.clear();
        let b = this.peekAmmo();
        FX.launchUp.at(this.x, this.y, 0, b.hitColor, {
            region: lib.region("相位发射台-pod"),
            timer: new Interval()
        });
        let x = this.targetPos.x, y = this.targetPos.y;
        let distance = Mathf.dst(this.x, this.y, x, y) / this.block.range;
        if (distance > 1) {
            let ang = Angles.angle(this.x, this.y, x, y);
            let xy = lib.AngleTrns(ang, this.block.range);
            x = this.x + xy.x;
            y = this.y + xy.y;
        }
        Time.run(FX.launchUp.lifetime + b.lifetime * distance, () => {
            FX.launchDown.at(x, y, 0, b.hitColor, {
                region: lib.region("相位发射台-pod"),
                timer: new Interval()
            });
            Time.run(FX.launchDown.lifetime, () => {
                Damage.damage(this.team, x, y, b.splashDamageRadius, b.splashDamage);
                Fx.titanSmoke.at(x, y, b.hitColor);
                Effect.shake(8, 8, x, y);
            });
        });
    },
    needItem(item) {
        return this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item) && !this.block.ammoTypes.get(item);
    },
    acceptItem(source, item) {
        let type = this.block.ammoTypes.get(item);
        return this.needItem(item) || (type != null && this.totalAmmo + type.ammoMultiplier <= this.block.maxAmmo);
    },
    acceptStack(item, amount, source) {
        let type = this.block.ammoTypes.get(item);
        if (this.needItem(item)) return Math.min(this.getMaximumAccepted(item) - this.items.get(item), amount);
        this.super$acceptStack(item, amount, source);
    },
    handleItem(source, item) {
        if (this.block.ammoTypes.get(item)) this.super$handleItem(source, item);
        else if (this.needItem(item)) this.items.add(item, 1);
    },
    removeStack(item, amount) {
        if (this.items == null) return 0;
        amount = Math.min(amount, this.items.get(item));
        this.items.remove(item, amount);
        return amount;
    }
})
exports.桎梏 = 桎梏;