const lib = require("base/coflib");
let p = Math.PI, pro = DrawPart.PartProgress;
let pulsar = new PowerTurret("脉冲星");
pulsar.drawer = (() => {
    const d = new DrawTurret();
    for (let i = 0; i < 3; i++) {
        d.parts.add(
            Object.assign(new RegionPart("-aim"), {
                heatProgress: pro.warmup.sin((4 - i) * 10, 20, 1),
                heatLight: true,
                drawRegion: false,
                y: (i + 1) * 20 / (1 - i / 20) + 9,
                xScl: 1 - i * 0.2,
                yScl: 1 - i * 0.2,
                heatColor: lib.Color("D1EFFF")
            })
        )
    }
    for (let i = 0; i < 4; i++) {
        lib.DoubleHalo(d, {
            x: -20,
            y: 32,
            moveX: i * 4,
            moveY: i * 10,
            shapeMR: i * -15,
            shapes: 1,
            radius: 0,
            radiusTo: 2 + 3 - i,
            triL: 0,
            triLT: 28 + i * 4,
            haloRot: 45,
            color: lib.Color("8CA9E8"),
            colorTo: lib.Color("D1EFFF")
        })
    }
    d.parts.add(
        Object.assign(new RegionPart("-glow"), {
            heatProgress: pro.warmup,
            heatColor: lib.Color("F03B0E"),
            drawRegion: false
        }),
        Object.assign(new RegionPart("-sidef"), {
            progress: pro.warmup.add(-1).absin(p * 0.5, 1),
            heatProgress: pro.warmup,
            under: true,
            mirror: true,
            moveX: 2,
            moveY: -2,
            heatColor: lib.Color("F03B0E"),
            turretHeatLayer: 50 - 0.0000001
        }),
        Object.assign(new RegionPart("-sideb"), {
            progress: pro.warmup.add(-1).absin(p * -0.5, 1),
            heatProgress: pro.warmup,
            under: true,
            mirror: true,
            moveX: 3.25,
            moveY: -3.25,
            heatColor: lib.Color("F03B0E"),
            turretHeatLayer: 50 - 0.0000001
        })
    )
    return d;
})();