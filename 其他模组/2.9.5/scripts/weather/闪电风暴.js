const lib = require("base/coflib");

const ef = new Effect(60, e => {
    e.lifetime = 60 + Mathf.randomSeed(e.id, 8 + e.data.offset)
    let x = Core.camera.position.x,
        y = Core.camera.position.y
    Draw.z(Layer.end + 0.01)
    Draw.color(Color.white)
    Draw.alpha(e.data.offset / 32 * 0.5 * e.fout())
    Fill.square(x, y, Core.graphics.getWidth(), Core.graphics.getHeight())
})

function lightning(tx, ty, rotation, length, amount, stroke, offset) {
    let x = tx,
        y = ty,
        rotation = rotation,
        hitRange = length,
        lines = new Seq(),
        random = new Rand()
    for (let i = 0; i < amount; i++) {
        lines.add(new Vec2(x, y))
        rotation += random.range(offset)
        x += Angles.trnsx(rotation, hitRange)
        y += Angles.trnsy(rotation, hitRange)
    }

    const lightn = new Effect(60, 500, e => {
        if (!(e.data instanceof Seq)) return;
        let lines = e.data
        Lines.stroke(stroke * e.fout())
        Draw.color(e.color, Color.white, e.fin())
        for (let i = 0; i < lines.size - 1; i++) {
            let cur = lines.get(i),
                next = lines.get(i + 1)
            Lines.line(cur.x, cur.y, next.x, next.y, false)
        }
        lines.each(p => {
            Fill.circle(p.x, p.y, Lines.getStroke() / 2)
        })
    })
    lightn.at(tx, ty, rotation, Pal.lancerLaser, lines)
}

const hitLightning = new Effect(40, e => {
    Draw.color(e.color)
    Lines.stroke(e.fout() * e.rotation / 4)
    Angles.randLenVectors(e.id, 18 * Math.round(e.rotation / 4), e.finpow() * e.rotation * 10, 0, 360, (x, y) => {
        let ang = Mathf.angle(x, y)
        Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 6 + 1)
    })
})

function bigLightning(x, y, offset, damage, status) {
    let cx = Core.camera.position.x,
        cy = Core.camera.position.y
    ef.at(cx, cy, 0, Color.white, {
        tx: x,
        ty: y,
        offset: offset
    })
    lightning(x, y, 90, Mathf.random(48, 80) * offset / 8, 5, offset, 10)
    hitLightning.at(x, y, offset, Pal.lancerLaser)
    if (damage) Damage.damage(Team.derelict, x, y, offset * 1.25, offset * 80, true)
    if (status) Units.nearby(Team.derelict, x, y, offset * 80, cons(other => {
        other.apply(StatusEffects.shocked, 10 * offset)
    }))
}

const we = extend(ParticleWeather, "闪电风暴", {
    drawOver(state) {
        this.drawRain(10, 40, 8, 10, 1000, state.intensity, 0.85, this.rainColor)
        if (Vars.state.isPlaying() && Mathf.chanceDelta(0.04 * state.intensity / 2)) {
            let wx = Mathf.random(0, Vars.world.width()) * 8,
                wy = Mathf.random(0, Vars.world.height()) * 8,
                st = Mathf.random(8, 32 + state.intensity * 4),
                block = Vars.indexer.findEnemyTile(Team.derelict, wx, wy, 800, b => b.block instanceof PowerBlock),
                bl = Vars.indexer.findEnemyTile(Team.derelict, wx, wy, 1200, b => b.block == blz);
            Units.nearbyEnemies(Team.derelict, wx, wy, 600, cons(un => {
                if (bl != null) {
                    bigLightning(bl.x, bl.y, st, false, true)
                } else if (block != null) {
                    bigLightning(block.x, block.y, st, true, true)
                } else if (un != null) {
                    bigLightning(un.x, un.y, st, true, true)
                    un.apply(StatusEffects.electrified, 5 * st)
                } else {
                    bigLightning(wx, wy, st, true, true)
                }
            }))
        }
    }
})
we.attrs.set(Attribute.light, -0.8)
we.attrs.set(Attribute.water, 0.9)
we.rainColor = lib.Color("7A95EA")
we.useWindVector = true;
we.sizeMin = 75;
we.sizeMax = 125;
we.minAlpha = 0.02;
we.maxAlpha = 0.12;
we.baseSpeed = 12;
we.force = 1;
we.density = 20000;
we.padding = 16;
we.status = StatusEffects.wet;
we.sound = Sounds.rain;
we.soundVol = 0.9;
we.splashes = [];

const blz = lib.newBlock("避雷针", {})
blz.size = 2;
blz.health = 30;
blz.setupRequirements(
    Category.effect,
    BuildVisibility.sandboxOnly,
    ItemStack.with(Items.copper, 200, Items.lead, 150)
);