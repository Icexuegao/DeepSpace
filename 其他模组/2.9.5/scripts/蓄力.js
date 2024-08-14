function AroundBulletType(speed, damage, lifetime, power) {
    return extend(BasicBulletType, {
        speed: speed,
        damage: damage,
        lifetime: 3600,
        ammoMultiplier: 1,
        update(b) {
            this.super$update(b);
            let e = b.owner;
            let angle0 = Time.time, angle1 = -1.1 * angle0;
            let x1 = Angles.trnsx(angle0, 120), y1 = Angles.trnsy(angle0, 120);
            let x2 = Angles.trnsx(angle1, 60), y2 = Angles.trnsy(angle1, 60);
            let xx = e.x + x1 + x2, yy = e.y + y1 + y2;
            /*if (e.isShooting() && e.canConsume()) b.vel.setAngle(Angles.moveToward(b.rotation(), Mathf.angle(e.targetPos.getX(), e.targetPos.getY()), Time.delta * power));
            else */
            b.vel.setAngle(Angles.moveToward(b.rotation(), Mathf.angle(xx, yy), Time.delta * power));
            print(xx)
            print(yy)
            print(Mathf.angle(xx, yy) + "\n")
        }
    })
}

let b = Object.assign(AroundBulletType(6, 50, 6), {
    trailLength: 32,
    trailWidth: 2,
    trailColor: Color.sky
});

let xl = new PowerTurret("蓄力");
xl.reload = 60;
xl.category = Category.turret;
xl.buildVisibility = BuildVisibility.shown;
xl.shootType = Blocks.salvo.ammoTypes.get(Items.thorium);
xl.buildType = prov(() => {
    let timer = 300;
    return extend(PowerTurret.PowerTurretBuild, xl, {
        updateTile() {
            this.super$updateTile();
            if (this.canConsume()) {
                timer--
                if (timer == 0) {
                    b.create(this, this.x, this.y, this.rotation);
                    timer = 300;
                }
            }
        }
    })
})