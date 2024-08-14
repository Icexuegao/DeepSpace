const {Char} = require("planet/查尔");
const p = extend(Planet, "查尔星环", Char, 0.1, {
    scale: 1,
    base: Blocks.snow,
    tint: Blocks.ice,
    tintThresh: 0.5,
    pieces: 300 //陨石数量
});
p.localizedName = "查尔星环";
p.orbitRadius = 0;
p.hasAtmosphere = false;
p.camRadius = p.scale;
p.minZoom = 0.01;
p.accessible = false;
p.sectors.add(new Sector(p, new PlanetGrid.Ptile(0, 0)));
p.generator = new AsteroidGenerator();
p.meshLoader = () => {
    let meshes = new Seq();
    let tinted = p.tint.mapColor;
    let color = p.base.mapColor;
    let rand = new Rand(p.id + 2);
    for (let j = 0; j < p.pieces; j++) {
        let v2 = new Vec2();
        v2.setToRandomDirection().setLength(rand.random(0.7, 1.3)); //宽度
        let v22 = new Vec2(v2.y, rand.random(-0.1, 0.1)); //厚度
        v22.rotate(60); //倾斜角度
        meshes.add(new MatMesh(
            new NoiseMesh(p, j + 1, 1, 0.022 + rand.random(0.039) * p.scale, 2, 0.6, 0.38, 20, color, tinted, 3, 0.6, 0.38, p.tintThresh),
            new Mat3D().setToTranslation(new Vec3(v2.x, v22.x, v22.y).scl(5)) //整体大小
        ));
    }
    ;
    return new JavaAdapter(GenericMesh, {
        meshes: meshes.toArray(),
        render(params, projection, transform) {
            for (let v of this.meshes) {
                v.render(params, projection, transform);
            }
        }
    });
};