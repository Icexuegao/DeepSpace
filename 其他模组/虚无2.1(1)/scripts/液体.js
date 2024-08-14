function newLiquid(name) {
    exports[name] = (() => {
        let myLiquid = extend(Liquid, name, {});
        return myLiquid;
    })();
}

newLiquid("化钢液")
newLiquid("高温溶液")
newLiquid("虚空液")