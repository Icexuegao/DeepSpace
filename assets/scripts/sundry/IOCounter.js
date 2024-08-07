//此js来自miner模组显示蓝图效率
//miner的mod群:757679470";
//侵权必删

importPackage(java.lang);
const javaObject = java.lang.Object;
const IntClass = Integer.TYPE;

// enum
const COUNTER_BREAK = 0;

// let input = Vars.control.input;
// let {selectPlans, linePlans} = input;

// 消耗/生产 一次的量
let inputCounter = new Counter(plan => plan.block);
let outputCounter = new Counter(plan => plan.block);

// 基础消耗/生产时间 (消耗/生产 一次所需时间[s])
let speedCounter = new Counter(plan => plan.block);
let consumerCounter = new Counter();

let returnCountField = getField(Drill.__javaObject__, "returnCount");
let returnItemField = getField(Drill.__javaObject__, "returnItem");
let countOreMethod = getMethod(Drill.__javaObject__, "countOre", [Tile.__javaObject__]);

// anuke¿
let Pump$canPumpMethod = getMethod(Pump.__javaObject__, "canPump", [Tile.__javaObject__]);
let SolidPump$canPumpMehod = getMethod(SolidPump.__javaObject__, "canPump", [Tile.__javaObject__]);

let getEfficiencyMethod = getMethod(WallCrafter.__javaObject__, "getEfficiency", [IntClass, IntClass, IntClass, Cons, Intc2]);

const tmpSeq = new Seq();

Events.on(ClientLoadEvent, e => {
    Vars.ui.hudGroup.fill(null, table => {
        table.top();
        
        // 关闭触碰
        table.touchable = Touchable.disabled;
        
        let entry = new IOEntry();
        table.table(Styles.black3, t => {
        }).padTop(120).update(t => {
            t.clearChildren();
            entry.clear();
                        
            Vars.control.input.allPlans().each(plan => {
                if(plan.breaking){
                    return;
                }
            
                count(plan, entry);
            });
                        
            let i = 0;
            entry.map.each(e => {
                let item = e.key, amount = e.value;
                t.table(null, itemTable => {
                    itemTable.left();
                    
                    itemTable.image(item.uiIcon).size(32);
                    itemTable.add(Strings.autoFixed(amount, 2) + "[white]/s").padLeft(4).color(amount > 0 ? Pal.heal : Pal.remove).growX().right();
                }).minWidth(48).padLeft(8).growX();
                                
                if(++i % 5 == 0){
                    t.row();
                }
            });
            
            // 电力下次一定!
            // let power = entry.power;
            // if(power > 0){
                // t.table(null, powerTable => {                
                    // powerTable.left();
                    
                    // powerTable.image(Icon.power).size(32);
                    // powerTable.add(Strings.autoFixed(power, 1) + "[white]/s").padLeft(4).color(power > 0 ? Pal.heal : Pal.remove).growX().right();
                // }).minWidth(48).padLeft(8).growX();
                
                // if(++i % 5 == 0){
                    // t.row();
                // }
            // }
        });
    });
});

function count(plan, entry){
    let ie = inputCounter.count(plan, new IOEntry());
    let oe = outputCounter.count(plan, new IOEntry());
    
    entry.addInputEntry(ie);
    entry.addOutputEntry(oe);
        
    //let builder = new StringBuilder();
    // builder.append(block.emoji()).append(":");
    
    // builder.append("\n").append("Input:");
    // mapEach(ie.map, (item, amount) => {
        // builder.append(amount).append(item.emoji());
        // builder.append(";");
    // });
    
    // builder.append("\n").append("Output:");
    // mapEach(oe.map, (item, amount) => {
        // builder.append(amount).append(item.emoji());
        // builder.append(";");
    // });
}

Events.on(ContentInitEvent, e => {
    registerSpeed();
    registerConsumer();
    registerInput();
    registerOutput();
});

// 或许能让mod来注册¿

function registerConsumer(){
    consumerCounter.register(ConsumeItems.__javaObject__, (consumer, entry) => {
        let {items} = consumer;
        
        arrayEach(items, stack => {
            entry.addItem(stack.item, stack.amount);
        });
    });
    
    consumerCounter.register(ConsumeLiquid.__javaObject__, (consumer, entry) => {
        let {liquid, amount} = consumer;
        entry.addItem(liquid, amount*60); // amount是每帧耗量
    });
    
    consumerCounter.register(ConsumeLiquids.__javaObject__, (consumer, entry) => {
        let {liquids} = consumer;
        
        arrayEach(liquids, stack => {
            entry.addItem(stack.liquid, stack.amount*60); // amount是每帧耗量
        });
    });
}

function registerInput(){
    inputCounter.register(Block.__javaObject__, (plan, entry) => {
        let {block} = plan;
        let {consumers, consPower} = block;
        
        let speedEntry = speedCounter.count(plan, new SpeedEntry());
        let consumeSpeed = speedEntry.consume;
        
        // 可选不计
        arrayEach(consumers, consumer => {
            consumerCounter.count(consumer, entry);
        });
        
        if(consumeSpeed != 0){
            entry.mulSpeed(consumeSpeed);
        }
        
        // 电力下次一定!
        // if(consPower != null){
            // entry.addPower(consPower.usage * 60);
        // }
    });
    
    inputCounter.register(UnitFactory.__javaObject__, (plan, entry) => {
        let {block, config} = plan;
        let {plans} = block;
        
        if(plans.size == 1){
            config = 0; // 只有一个选项 自动选上
        }
        
        if(config != null){
            let unitPlan = plans.get(Math.min(config, plans.size - 1));
            
            arrayEach(unitPlan.requirements, stack => {
                entry.addItem(stack.item, stack.amount);
            });
        }
    });
}

function registerOutput(){
    outputCounter.register(GenericCrafter.__javaObject__, (plan, entry) => {
        let {block} = plan;
        let {craftTime, outputItems, outputLiquids} = block;
        
        let speedEntry = speedCounter.count(plan, new SpeedEntry());
        let outputSpeed = speedEntry.output;
        
        if(outputItems != null){
            arrayEach(outputItems, stack => {
                entry.addItem(stack.item, stack.amount);
            });
        }
        
        if(outputLiquids != null){
            arrayEach(outputLiquids, stack => {
                entry.addItem(stack.liquid, stack.amount*60);
            });
        }
        
        entry.mulSpeed(outputSpeed);
    });
    
    outputCounter.register(Drill.__javaObject__, (plan, entry) => {
        let tile = plan.tile();
        let {block} = plan;
        
        countOreMethod.invoke(block, [tile]);
        
        let item = returnItemField.get(block);
        
        // 防止连放钻头导致崩溃
        if(item == null){
            return;
        }
        
        let count = returnCountField.get(block);
        
        let amount = 60 / block.getDrillTime(item) * count;
        entry.addItem(item, amount);
    });
    
    outputCounter.register(BeamDrill.__javaObject__, (plan, entry) => {
        let {block, x, y, rotation} = plan;
        let {size, range, tier, drillTime} = block;
        
        let dx = Geometry.d4x[rotation];
        let dy = Geometry.d4y[rotation];
    
        let item = null;
        let count = 0;

        outer:
        for(let i = 0; i < size; i++){
            block.nearbySide(x, y, rotation, i, Tmp.p1);
            let px = Tmp.p1.x, py = Tmp.p1.y;
            
            inner:
            for(let j = 0; j < range; j++){
                let rx = px + dx * j, ry = py + dy * j;
                let other = Vars.world.tile(rx, ry);
                
                if(other != null && other.solid()){
                    let drop = other.wallDrop();
                    if(drop != null){
                        if(drop.hardness <= tier){
                            // 多种能挖的矿物钻头不工作
                            if(item != null && drop != item){
                                item = null;
                                break outer;
                            }
                            
                            item = drop;
                            count++;
                        }
                    }
                    
                    // 碰到墙就结束循环 换另一线
                    break inner;
                }
            }
        }

        if(item != null){
            entry.addItem(item, 60 / drillTime * count);
        }
    });
    
    outputCounter.register(WallCrafter.__javaObject__, (plan, entry) => {
        let {block, x, y, rotation} = plan;
        let {drillTime, output} = block;
        
        let eff = getEfficiencyMethod.invoke(block, [
            toInt(x), toInt(y), toInt(rotation), 
            null, null
        ]);
        
        entry.addItem(output, 60 / drillTime * eff);
    });
    
    outputCounter.register(Pump.__javaObject__, (plan, entry) => {
        let tile = plan.tile();
        
        let {block} = plan;
        
        let amount = 0;
        let liquidDrop = null;

        tile.getLinkedTilesAs(block, tmpSeq).each(other => {
            if(Pump$canPumpMethod.invoke(block, other)){
                let floor = other.floor();
                if(liquidDrop != null && floor.liquidDrop != liquidDrop){
                    liquidDrop = null;
                    return;
                }
                liquidDrop = floor.liquidDrop;
                amount += floor.liquidMultiplier;
            }
        });
        
        if(liquidDrop == null) return COUNTER_BREAK;
        
        entry.addItem(liquidDrop, amount * block.pumpAmount * 60);
        
        return COUNTER_BREAK;
    });
    
    outputCounter.register(SolidPump.__javaObject__, (plan, entry) => {
        let tile = plan.tile();
        let {block, x, y} = plan;
        let {result, size, attribute, baseEfficiency, pumpAmount} = block;
        
        let size2 = (size * size);
        
        let efficiency = block.sumAttribute(attribute, x, y) / size2;
        tile.getLinkedTiles(tmpSeq).each(other => {
            if(SolidPump$canPumpMehod.invoke(block, other)){
                efficiency += baseEfficiency / size2;
            }
        });
        efficiency += attribute == null ? 0 : attribute.env();
        
        entry.addItem(result, pumpAmount * 60 * efficiency);
        
        return COUNTER_BREAK;
    });
    
    outputCounter.register(Separator.__javaObject__, (plan, entry) => {
        let {block} = plan;
        let {results} = block;
        
        let speedEntry = speedCounter.count(plan, new SpeedEntry());
        let outputSpeed = speedEntry.output;
        
        // 分离机的效率估算
        let sum = 0;
        arrayEach(results, stack => sum += stack.amount);
        arrayEach(results, stack => {
            let {item, amount} = stack;
            entry.addItem(item, amount / sum * outputSpeed);
        });
    });
    
    outputCounter.register(ConsumeGenerator.__javaObject__, (plan, entry) => {
        let {block} = plan;
        let {outputLiquid} = block;
        
        if(outputLiquid == null){
            return;
        }
        
        entry.addItem(outputLiquid.liquid, outputLiquid.amount*60);
    });
    
    outputCounter.register(ThermalGenerator.__javaObject__, (plan, entry) => {
        let {block, x, y} = plan;
        let {attribute, outputLiquid} = block
                
        let efficiency = block.sumAttribute(attribute, x, y);
        efficiency += attribute.env();
        
        if(outputLiquid != null){
            entry.addItem(outputLiquid.liquid, outputLiquid.amount*60 * efficiency);
        }
    });
    
    // 电力下次一定!
    // outputCounter.register(PowerGenerator.__javaObject__, (plan, entry) => {
        // entry.addPower(plan.block.powerProduction * 60);
    // });
}

function registerSpeed(){
    speedCounter.register(GenericCrafter.__javaObject__, (plan, entry) => {
        entry.setAll(60 / plan.block.craftTime);
    });
    
    speedCounter.register(AttributeCrafter.__javaObject__, (plan, entry) => {
        let {block, x, y} = plan;
        let {craftTime, baseEfficiency, maxBoost, boostScale, attribute} = block;
        
        let baseSpeed = 60 / craftTime;
        
        let attrsum = block.sumAttribute(attribute, x, y);
        let efficiency = baseEfficiency + Math.min(maxBoost, boostScale * attrsum) + attribute.env();
        
        entry.setAll(baseSpeed * efficiency);
        
        return COUNTER_BREAK;
    });
    
    speedCounter.register(Separator.__javaObject__, (plan, entry) => {
        entry.setAll(60 / plan.block.craftTime);
    });
    
    speedCounter.register(Reconstructor.__javaObject__, (plan, entry) => {
        entry.setAll(60 / plan.block.constructTime);
    });
    
    speedCounter.register(UnitFactory.__javaObject__, (plan, entry) => {
        // 蓝图可能会有config
        let config = plan.config;
        if(config != null){
            let plans = plan.block.plans;
            let unitPlan = plans.get(Math.min(config, plans.size - 1));
            entry.setAll(60 / unitPlan.time);
        }
    });
    
    speedCounter.register(ConsumeGenerator.__javaObject__, (plan, entry) => {
        entry.setConsume(60 / plan.block.itemDuration);
    });
    
    speedCounter.register(ImpactReactor.__javaObject__, (plan, entry) => {
        entry.setConsume(60 / plan.block.itemDuration);
    });
    
    speedCounter.register(NuclearReactor.__javaObject__, (plan, entry) => {
        entry.setConsume(60 / plan.block.itemDuration);
    });
    
    speedCounter.register(ForceProjector.__javaObject__, (plan, entry) => {
        let {block} = plan;
        
        if(block.itemConsumer == null){
            return;
        }
    
        entry.setConsume(60 / block.phaseUseTime);
    });
    
    speedCounter.register(MendProjector.__javaObject__, (plan, entry) => {
        entry.setConsume(60 / plan.block.useTime);
    });
    
    speedCounter.register(OverdriveProjector.__javaObject__, (plan, entry) => {
        entry.setConsume(60 / plan.block.useTime);
    });
}

function Counter(objCons){
    this.map = new ObjectMap(); // <Class, entryCons>
    this.objCons = (objCons || (obj => obj));
    
    this.register = function(clazz, entryCons){
        this.map.put(clazz, entryCons);
    }
    
    this.count = function(obj, entry){
        let {map} = this;
        let currentClass = this.objCons(obj).getClass();
        
        loop:
        while(currentClass != javaObject){
            let cons = map.get(currentClass);
            
            if(cons != null){
                let flag = cons(obj, entry);
                
                if(flag == COUNTER_BREAK){
                    break loop;
                }
            }
        
            currentClass = currentClass.getSuperclass(); 
        }
        
        return entry;
    }
}

function IOEntry(){
    this.map = new ObjectFloatMap(); // <项目, 数量>
    
    // 一些特殊的项目
    this.power = 0;
    this.heat = 0;
    
    this.clear = function(){
        this.map.clear();
        this.power = 0;
        this.heat = 0;
    }
    
    this.addItem = function(item, amount){
        this.map.increment(item, 0, amount);
    }
    
    this.addPower = function(amount){
        this.power += amount;
    }
    
    this.addHeat = function(amount){
        this.heat += amount;
    }
    
    this.mulSpeed = function(speed){
        let {map} = this;
        
        map.each(entry => {
            let {key, value} = entry;
            
            // 流体与生产/消耗速度无关
            if(key instanceof Liquid){
                return;
            }
            
            map.put(key, value * speed)
        });
    }
    
    this.addOutputEntry = function(entry){
        let {map} = this;
        entry.map.each(e => {
            let item = e.key, amount = e.value;
            map.increment(item, 0, amount);
        });
        
        // 电力下次一定!
        // this.power += entry.power;
        // this.heat += entry.heat;
    }
    
    this.addInputEntry = function(entry){
        let {map} = this;
        entry.map.each(e => {
            let item = e.key, amount = e.value;
            map.increment(item, 0, -amount);
        });
        
        // 电力下次一定!
        // this.power -= entry.power;
        // this.heat -= entry.heat;
    }
}

function SpeedEntry(){
    this.consume = 0;
    this.output = 0;
    
    this.setAll = function(speed){
        this.output = this.consume = speed;
        return this;
    }
    
    this.setConsume= function(speed){
        this.consume = speed;
        return this;
    }
    
    this.setOutput = function(speed){
        this.output = speed;
        return this;
    }
}

function arrayEach(array, cons){
    for(let i = 0, length = array.length; i < length; i++){
        cons(array[i]);
    }
}

function getField(clazz, name){
    let field = clazz.getDeclaredField(name);
    field.setAccessible(true);
    return field;
}

function getMethod(clazz, name, parameterTypes){
    let method = clazz.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method;
}

function toInt(value){
    return new Integer(value);
}