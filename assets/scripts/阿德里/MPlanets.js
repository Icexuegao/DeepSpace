// 负责加载所有星球相关模块 并导出

// 加载星球
const 阿德里 = require("阿德里/planets/阿德里");
require("阿德里/presets/阿德里");
require("阿德里/techTrees/科技树");

exportObj("阿德里", 阿德里);

function exportObj(name, obj){
    module.exports[name] = obj;
}