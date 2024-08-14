function newItem(name) {
    exports[name] = (() => {
        let myItem = extend(Item, name, {});
        return myItem;
    })();
}

newItem("铀")
newItem("化钢")
newItem("火石矿")
newItem("绯红之石")
newItem("黑核")
newItem("绯红合金")
newItem("一级研究协议")
newItem("核燃料棒")
newItem("二级研究协议")
newItem("裂位合金")
newItem("虚空石")
newItem("虚空精华")
newItem("虚铁")
newItem("虚空钢")
newItem("钛板")