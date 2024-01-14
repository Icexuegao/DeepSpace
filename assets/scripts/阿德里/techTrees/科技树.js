// 创建星球专属科技树 (这里偷懒塞一起了)
const nodeRoot = TechTree.nodeRoot;
const node = TechTree.node;

const 量子核心 = extend(CoreBlock, "量子核心", {});
exports.量子核心 = 量子核心;


const 阿德里 = require("阿德里/planets/阿德里");
const {伊始} = require("阿德里/presets/阿德里");

// public static TechNode nodeRoot(String name, Unlockable德尔塔 德尔塔, boolean requireUnlock, Runnable children)
阿德里.techTree = nodeRoot("阿德里", 量子核心,true, () => {
    node(伊始, Seq.with(
        new Objectives.SectorComplete(SectorPresets.planetaryTerminal),
    ), () => {});
   
    // 更多的自己设置罢
});