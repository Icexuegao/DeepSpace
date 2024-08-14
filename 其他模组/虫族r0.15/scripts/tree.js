const nodeRoot = TechTree.nodeRoot;
const nodeProduce = TechTree.nodeProduce
const node = TechTree.node;
const SectorComplete = Objectives.SectorComplete;
const Research = Objectives.Research;

const core = require("zerg/block/core");
const distribution = require("zerg/block/distribution");
const factory = require("zerg/block/factory");
const liquidBlock = require("zerg/block/liquidBlock");
const logic = require('zerg/block/logic');
const other = require("zerg/block/other");
const power = require("zerg/block/power");
const production = require("zerg/block/production");
const turret = require("zerg/block/turret");
const unitFactory = require("zerg/block/unitFactory");
const wall = require("zerg/block/wall");

const air = require("zerg/unit/air");
const crystive = require('zerg/unit/crystive');
const insect = require("zerg/unit/insect");
const tank = require("zerg/unit/tank");

const item = require("zerg/item");
const liquid = require("zerg/liquid");
const planet = require("zerg/planet");

planet.greavar.techTree = nodeRoot("greavar", planet.greavar, () => {
    nodeProduce(item.nickel, () => {
        nodeProduce(Items.sand, () => {
            nodeProduce(Items.silicon, () => {
            })
        }),
            nodeProduce(Items.coal, () => {
                nodeProduce(Items.graphite, () => {
                })
            }),
            nodeProduce(item.manganese, () => {
                nodeProduce(item.crystal, () => {
                    nodeProduce(Liquids.water, () => {
                        nodeProduce(Liquids.neoplasm, () => {
                        }),
                            nodeProduce(item.biomass, () => {
                                nodeProduce(item.amino, () => {
                                    nodeProduce(liquid.dissolvant, () => {
                                    }),
                                        nodeProduce(item.sulfone, () => {
                                        })
                                }),
                                    nodeProduce(item.biomassSteel, () => {
                                    })
                            })
                    }),
                        nodeProduce(Liquids.arkycite, () => {
                            nodeProduce(Liquids.oil, () => {
                            })
                        }),
                        nodeProduce(liquid.acid, () => {
                            nodeProduce(item.salt, () => {
                            }),
                                nodeProduce(Liquids.hydrogen, () => {
                                    nodeProduce(liquid.acetylene, () => {
                                        nodeProduce(liquid.yperite, () => {
                                        })
                                    })
                                })
                        }),
                        nodeProduce(item.energic, () => {
                        })
                }),
                    nodeProduce(item.chromium, () => {
                        nodeProduce(Liquids.slag, () => {
                            nodeProduce(item.iridium, () => {
                            })
                        })
                    })
            })
    }),
        node(core.ash, () => {
            node(core.albus, Seq.with(
                new Research(other.frame)
            ), () => {
                node(core.annular, Seq.with(
                    new Research(other.matrix)
                ), () => {
                })
            })
        }),
        node(distribution.nickelConveyor, () => {
            node(distribution.manganeseConveyor, () => {
                node(other.box, () => {
                    node(other.unloader, () => {
                    }),
                        node(other.launchPad, Seq.with(
                            new SectorComplete(planet.crystalOutpost)
                        ), () => {
                        })
                }),
                    node(distribution.biomassConveyor, () => {
                    }),
                    node(distribution.armoredConveyor, () => {
                    })
            }),
                node(distribution.junction, () => {
                    node(distribution.router, () => {
                        node(distribution.distributor, () => {
                        }),
                            node(distribution.overflowGate, () => {
                                node(distribution.underflowGate, () => {
                                }),
                                    node(distribution.sorter, () => {
                                        node(distribution.invertedSorter, () => {
                                        })
                                    })
                            })
                    }),
                        node(distribution.nickelBridge, () => {
                        })
                }),
                node(distribution.heatPipe, () => {
                    node(distribution.heatRouter, () => {
                    })
                })
        }),
        node(factory.compressor, () => {
            node(factory.multiCompressor, () => {
            }),
                node(factory.smelter, () => {
                    node(factory.incubator, () => {
                        node(factory.biomassSmelter, () => {
                        }),
                            node(factory.biomassDissociator, () => {
                                node(factory.dissolvantMixer, () => {
                                })
                            }),
                            node(factory.biomassFermenter, () => {
                            })
                    }),
                        node(factory.charger, () => {
                        }),
                        node(factory.oilRefinery, () => {
                            node(factory.arkyciteExtractor, () => {
                            }),
                                node(factory.oilDistillation, () => {
                                })
                        })
                }),
                node(factory.displacer, () => {
                    node(factory.synthesizer, () => {
                    }),
                        node(factory.acetyleneSynthesizer, () => {
                            node(factory.additiver, () => {
                            })
                        })
                })
        }),
        node(production.nickelDrill, () => {
            node(production.manganeseDrill, () => {
                node(production.crystalCollector, () => {
                    node(production.crystalDrill, () => {
                        node(production.biomassDrill, () => {
                        })
                    })
                })
            }),
                node(liquidBlock.nickelPump, () => {
                    node(liquidBlock.screwPump, () => {
                    }),
                        node(liquidBlock.crystalConduit, () => {
                            node(liquidBlock.manganeseConduit, () => {
                                node(liquidBlock.armoredConduit, () => {
                                })
                            }),
                                node(liquidBlock.crystalLiquidJunction, () => {
                                    node(liquidBlock.crystalLiquidRouter, () => {
                                        node(liquidBlock.crystalLiquidContainer, () => {
                                            node(liquidBlock.crystalLiquidTank, () => {
                                            })
                                        }),
                                            node(liquidBlock.crystalConduitBridge, () => {
                                            })
                                    })
                                })
                        }),
                        node(liquidBlock.waterExtractor, () => {
                        })
                })
        }),
        node(power.deflagrationGenerator, () => {
            node(power.fullEffectGenerator, () => {
                node(power.pyrolysis, () => {
                    node(power.biomassReactor, () => {
                    }),
                        node(power.extremeGenerator, () => {
                        })
                }),
                    node(power.crystalPanel, () => {
                        node(power.crystalPanelMedium, () => {
                            node(power.crystalPanelLarge, () => {
                            })
                        })
                    })
            }),
                node(power.nickelPowerNode, () => {
                    node(power.nickelPowerNodeLarge, () => {
                    }),
                        node(power.nickelBattery, () => {
                            node(power.nickelBatteryMedium, () => {
                                node(power.nickelBatteryLarge, () => {
                                })
                            })
                        }),
                        node(other.lamp, () => {
                            node(logic.processor, () => {
                                node(logic.processorLarge, () => {
                                }),
                                    node(logic.switchBlock, () => {
                                        node(logic.message, () => {
                                            node(logic.logicDisplay, () => {
                                            }),
                                                node(logic.memoryCell, () => {
                                                })
                                        })
                                    })
                            })
                        }),
                        node(other.frame, () => {
                            node(other.matrix, () => {
                                node(other.clan, () => {
                                }),
                                    node(other.resurrection, () => {
                                    })
                            }),
                                node(other.catalyzer, () => {
                                    node(other.prophet, () => {
                                    })
                                })
                        })
                })
        }),
        node(turret.guard, () => {
            node(turret.obstruct, () => {
            }),
                node(turret.lumen, () => {
                    node(turret.sange, () => {
                    })
                }),
                node(turret.nexus, () => {
                    node(turret.bomb, () => {
                        node(turret.midnight, () => {
                        })
                    }),
                        node(turret.spiral, () => {
                            node(turret.lacerate, () => {
                                node(turret.blowtorth, () => {
                                })
                            }),
                                node(turret.soak, () => {
                                    node(turret.deluge, () => {
                                    })
                                })
                        })
                })
        }),
        node(unitFactory.tankFactory, () => {
            node(unitFactory.reconstructor, () => {
                node(unitFactory.deepReconstructor, () => {
                })
            }),
                node(tank.pioneer, () => {
                    node(tank.brigadier, () => {
                        node(tank.kibbler, () => {
                            node(tank.purge, () => {
                                node(tank.fearless, () => {
                                })
                            })
                        })
                    }),
                        node(tank.gale, () => {
                            node(tank.hurricane, () => {
                                node(tank.tornado, () => {
                                })
                            })
                        }),
                        node(tank.alter, () => {
                        })
                }),
                node(unitFactory.airFactory, () => {
                    node(air.mist, () => {
                        node(air.cirrus, () => {
                            node(air.cloud, () => {
                            })
                        }),
                            node(air.electron, () => {
                                node(air.inductance, () => {
                                    node(air.ampere, () => {
                                    })
                                })
                            }),
                            node(air.phantom, () => {
                                node(air.shadow, () => {
                                })
                            })
                    })
                }),
                node(unitFactory.unitIncubator, () => {
                    node(unitFactory.reincubator, () => {
                        node(unitFactory.hyperplasia, () => {
                            node(unitFactory.metamorphosiser, () => {
                            })
                        }),
                            node(unitFactory.laboratory, () => {
                                node(unitFactory.conflater, () => {
                                })
                            })
                    }),
                        node(insect.buffer, () => {
                            node(insect.spread, () => {
                            }),
                                node(insect.spider, () => {
                                    node(insect.tarantula, () => {
                                        node(insect.group, () => {
                                            node(insect.mantis, () => {
                                            })
                                        })
                                    }),
                                        node(crystive.anatase, () => {
                                            node(crystive.asbestos, () => {
                                            })
                                            node(crystive.quartz, () => {
                                            })
                                        })
                                }),
                                node(insect.mosquito, () => {
                                    node(insect.burst, () => {
                                        node(insect.dragonfly, () => {
                                        })
                                    })
                                })
                        })
                }),
                node(unitFactory.payloadConveyor, () => {
                    node(unitFactory.payloadRouter, () => {
                    })
                    node(unitFactory.fixPoint, () => {
                        node(unitFactory.fixTurret, () => {
                        }),
                            node(unitFactory.assemblerModule, () => {
                            }),
                            node(unitFactory.buildingConstructor, () => {
                            })
                    })
                })
        }),
        node(wall.nickelWall, () => {
            node(wall.nickelWallLarge, () => {
            }),
                node(wall.manganeseWall, () => {
                    node(wall.manganeseWallLarge, () => {
                    }),
                        node(wall.crystalWall, () => {
                            node(wall.crystalWallLarge, () => {
                            }),
                                node(wall.energicWall, () => {
                                    node(wall.energicWallLarge, () => {
                                    })
                                })
                        }),
                        node(wall.chromiumWall, () => {
                            node(wall.chromiumWallLarge, () => {
                            }),
                                node(wall.biomassWall, () => {
                                    node(wall.biomassWallLarge, () => {
                                    })
                                })
                        }),
                        node(wall.manganeseDoor, () => {
                            node(wall.manganeseDoorLarge, () => {
                            })
                        })
                })
        }),
        node(planet.iceField, () => {
            node(planet.valleyPlain, Seq.with(
                new SectorComplete(planet.iceField)
            ), () => {
                node(planet.coldJunction, Seq.with(
                    new SectorComplete(planet.valleyPlain)
                ), () => {
                    node(planet.twilightSea, Seq.with(
                        new SectorComplete(planet.coldJunction)
                    ), () => {
                    }),
                        node(planet.intertwinedGlacier, Seq.with(
                            new SectorComplete(planet.coldJunction)
                        ), () => {
                        })
                }),
                    node(planet.crystalOutpost, Seq.with(
                        new SectorComplete(planet.valleyPlain)
                    ), () => {
                    })
            })
        })
})