//xvx神魂
//本代码可以塞在任意方块里
//比如墙又或者传送带，又或者核心
//只要这个方块可以刷新，就可以塞并且有效果

const MultipleTurrets = extend(Wall, "单方块多炮塔", {});

MultipleTurrets.buildVisibility = BuildVisibility.shown;
MultipleTurrets.category = Category.logic;

//这是必需的
MultipleTurrets.update = true;
//设置方块可使用物品
MultipleTurrets.hasItems = true;

//设置可使用的弹药
const TItems = [Items.copper, Items.graphite, Items.pyratite, Items.silicon, Items.thorium]

MultipleTurrets.buildType = prov(() => {
	//创建多炮塔
	//(方块，队伍(不需要设置))
	const payloads = [
		new BuildPayload(Blocks.salvo, Team.derelict),
		new BuildPayload(Blocks.salvo, Team.derelict),
		new BuildPayload(Blocks.salvo, Team.derelict),
		new BuildPayload(Blocks.salvo, Team.derelict)
	];
	const build = extend(Wall.WallBuild, MultipleTurrets, {
		//设置方块进入物品规则
		//你们可以自己设置规则
		acceptItem(source, item) {
			for(var i = 0; i < TItems.length; i++){
				if(TItems[i] == item){
					if(this.items.get(TItems[i]) < this.block.itemCapacity){
						return true;
					}
				}
			}

            return false;
        },
		updateTile() {
			this.super$updateTile();

			//可以让炮塔转起来的代码
			for (var i = 0; i < payloads.length; i++) {
                var t = payloads[i];
                var rotation = (360.0 / payloads.length) * i + Time.time;

				//这里的24为距离本体方块中心的多少距离旋转(8为1格)
                t.set(x + Angles.trnsx(rotation, 24), y + Angles.trnsy(rotation, 24), t.build.payloadRotation);
            }

			//设置模块
			for(var id = 0; id < payloads.length; id++){
				//设置队伍，如果在上面的创建位置设置，无用
                if(payloads[id].build.team != this.team){
                    payloads[id].build.team = this.team;
                }
				
				//执行炮塔更新
                payloads[id].update(null, this);

				//为物品炮塔添加弹药
				//你们需要可自己定义
				for(var i = 0; i < TItems.length; i++){
					if(payloads[id].build.acceptItem(payloads[id].build, TItems[i]) && this.items.get(TItems[i]) >= 1) {
						payloads[id].build.handleItem(payloads[id].build, TItems[i]);
						this.items.remove(TItems[i], 1);
					}
				}
            }

			//设置炮塔的位置
			//有需求你们可以自己定义
			//（x, y, r）
			payloads[0].set(this.x + 24, this.y + 24, payloads[0].build.payloadRotation);
			payloads[1].set(this.x + 24, this.y - 24, payloads[1].build.payloadRotation);
			payloads[2].set(this.x - 24, this.y - 24, payloads[2].build.payloadRotation);
			payloads[3].set(this.x - 24, this.y + 24, payloads[3].build.payloadRotation);
		},
		draw(){
			this.super$draw();

			//执行多炮塔的动画
			for(var i = 0; i < payloads.length; i++){
                payloads[i].draw();
            }
		},
		write(write) {
            this.super$write(write);
			
			//往地图里写入多炮塔的数据
			//用于保存地图
            for(var i = 0; i < payloads.length; i++){
                Payload.write(payloads[i], write);
            }
        },
		read(read, revision) {
            this.super$read(read, revision);

			//在地图里读取数据
			//用于加载地图
            for(var i = 0; i < payloads.length; i++){
                payloads[i] = Payload.read(read);
            }
        }
	});

	return build;
});