const 可摆放核心 = extend(CoreBlock, "可摆放核心", {
    canBreak() {
        return true;
    },
    canPlaceOn(tile, team) {
        return true;
    },
//replaceable(){return false;},
});