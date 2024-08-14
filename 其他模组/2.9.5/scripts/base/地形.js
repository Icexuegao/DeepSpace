exports.boneFloor = new Floor("骸骨地");
exports.boneWall = new StaticWall("骸骨墙");
exports.bloodScab = new Floor("血痂岩");
exports.bloodSand = new Floor("血沙");
exports.bloodSandWall = new StaticWall("血沙墙");
exports.bloodFloor = new Floor("血蚀岩");
exports.bloodWall = new StaticWall("血蚀墙");
exports.curseFloor = new Floor("诅咒之地", 4);
exports.curseWall = new StaticWall("诅咒之墙");

function Shader(name) {
    let shaders = Vars.mods.locateMod("curse-of-flesh").root.child("shaders");
    let s = new Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), shaders.child(name + ".frag").readString());
    let m = new CacheLayer.ShaderLayer(s);
    CacheLayer.add(m);
    return m
}

function liquidf(name, shader) {
    let l = new Floor(name, 0);
    l.cacheLayer = Shader(shader);
    return exports[name] = l;
}

liquidf("血池", "blood");
liquidf("血池浅", "bloodShallow");

Attribute.add("ice");

function setAttribute(block, attribute, amount) {
    return block.attributes.set(Attribute.get(attribute), amount);
}

setAttribute(Blocks.snow, "ice", 0.6);
setAttribute(Blocks.iceSnow, "ice", 0.9);
setAttribute(Blocks.ice, "ice", 1.2);
setAttribute(Blocks.redIce, "ice", 1.2);
setAttribute(Blocks.sand, Attribute.sand, 0.7);
setAttribute(Blocks.darksand, Attribute.sand, 1.5);

let iceMaker = new AttributeCrafter("低温化合器");
iceMaker.attribute = Attribute.get("ice");