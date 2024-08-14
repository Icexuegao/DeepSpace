const lib = require("base/coflib");
const my = require("base/物品");
const {先遣核心} = require("block/核心");
const {Char, map1, map2, map3, map4, mapX, map5, map6, map7, map8} = require("planet/查尔");
let node = TechTree.node;
let Research = Objectives.Research;
let nodeProduce = TechTree.nodeProduce;
let SectorComplete = Objectives.SectorComplete;

const {核心卸载器, 核心装载器, 液体装卸器} = require("block/工厂/装卸器核心");
const {资源装载器, 资源卸载器} = require("block/工厂/装卸器资源");
const {Back, 高能脉冲地雷} = require("block/炮塔/地雷");
const {破冰器} = require("block/杂项/炸弹");
const {观测系统} = require("other/观测系统");

lib.addResearch(液体装卸器, {parent: "liquid-container"});
lib.addResearch(高能脉冲地雷, {parent: "shock-mine"});
lib.addResearch(Back, {parent: "高能脉冲地雷"});
lib.addResearch(破冰器, {parent: "impulse-pump"});

/*Char.techTree = */
TechTree.nodeRoot("查尔", 先遣核心, true, () => {
    node(核心卸载器, ItemStack.with(
        Items.graphite, 900,
        Items.titanium, 900,
        Items.silicon, 1400,
        my.肃正协议, 1
    ), () => {
        node(核心装载器, ItemStack.with(
            Items.graphite, 900,
            Items.titanium, 900,
            Items.silicon, 1400,
            my.肃正协议, 1
        ), () => {
            node(资源卸载器, ItemStack.with(
                my.铱板, 5200,
                my.导能回路, 2400,
                my.肃正协议, 4
            ), () => {
            });
            node(资源装载器, ItemStack.with(
                my.铱板, 5200,
                my.导能回路, 2400,
                my.肃正协议, 4
            ), () => {
            });
        });
    });
    node(观测系统, ItemStack.with(
        Items.lead, 2300,
        Items.graphite, 1400,
        Items.titanium, 1800,
        Items.silicon, 4600
    ), () => {
    });
    node(map1, Seq.with(new SectorComplete(SectorPresets.planetaryTerminal)), () => {
        node(map2, Seq.with(new SectorComplete(map1)), () => {
            node(map3, Seq.with(new SectorComplete(map2)), () => {
                node(map4, Seq.with(new SectorComplete(map3)), () => {
                    node(mapX, Seq.with(new SectorComplete(map5)), () => {
                    });
                });
            });
            node(map5, Seq.with(new SectorComplete(map4)), () => {
                node(map6, Seq.with(new SectorComplete(map3), new SectorComplete(map5)), () => {
                    node(map7, Seq.with(new SectorComplete(map6)), () => {
                        node(map8, Seq.with(new SectorComplete(map7)), () => {
                        });
                    });
                });
            });
        });
    });
});