const lib = require("base/coflib");
const FX = require("base/Effect/fightFx");
const HX = require("base/Effect/hitFx");

let R = 120, r = 0.5 * R;
let trailLength = 80, trailWidth = 2, trailColor = Color.sky;

let st = lib.newBlock("大摆锤");
st.buildType = prov(() => {
    let xx = 0, yy = 0;
    return extend(Building, {
        updateTile() {
            this.super$updateTile();
            let angle0 = Time.time, angle1 = -1.1 * angle0;
            let xy = lib.AngleTrns(angle0, R);
            let xy2 = lib.AngleTrns(angle1, r);
            let xx = this.x + xy.x + xy2.x, yy = this.y + xy.y + xy2.y;
            if (!Vars.headless && trailLength > 0) {
                if (this.trail == null) this.trail = new Trail(trailLength);
                this.trail.length = trailLength;
                this.trail.update(xx, yy);
            }
        },
        afterDestroyed() {
            this.super$afterDestroyed();
            if (trailLength > 0 && this.trail != null && this.trail.size() > 0) {
                Fx.trailFade.at(xx, yy, trailWidth, trailColor, this.trail.copy());
            }
        },
        draw() {
            this.super$draw();
            Draw.color(lib.FF5845);
            Draw.z(Layer.effect);
            let angle0 = Time.time, angle1 = -1.1 * angle0;
            let xy = lib.AngleTrns(angle0, R);
            let xy2 = lib.AngleTrns(angle1, r);
            Lines.lineAngle(this.x, this.y, angle0, R);
            Lines.lineAngle(this.x + xy.x, this.y + xy.y, angle1, r);
            FX.LockPoint(this.x, this.y, 2, 1);
            if (this.timer.get(60)) {
                HX.shieldMaterializing.at(this.x + 240, this.y, Mathf.random(0, 90));
                FX.curveShoot.at(this.x, this.y + 240, Mathf.random(0, 90));
            }
            if (trailLength > 0 && this.trail != null) {
                let z = Draw.z();
                Draw.z(Layer.effect);
                this.trail.draw(trailColor, trailWidth);
                Draw.z(z);
            }
        }
    })
})