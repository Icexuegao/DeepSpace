const lib = require("base/coflib");
Events.on(ClientLoadEvent, e => {
    let dialog = new BaseDialog("[#D75B6E]血肉诅咒");
    dialog.cont.image(lib.region("logo")).row();
    dialog.cont.pane((() => {
        let table = new Table();
        let fl = new FLabel("{shake}" + lib.bundle("text-welcome")),
            fl2 = new FLabel("{fade}" + lib.bundle("text-version", lib.mod.meta.version));
        table.add(fl).center().width(600).maxWidth(600).padLeft(-440).labelAlign(Align.center).row();
        table.add(fl2).center().width(600).maxWidth(600).padLeft(-315).labelAlign(Align.center).row();
        table.button("加入Q群", () => {
            Core.app.openURI("https://jq.qq.com/?_wv=1027&k=8xNAXFLK");
        }).bottom().size(120, 64);
        return table;
    })()).grow().center().maxWidth(600);
    dialog.buttons.button("[red]更新日志", Icon.infoCircle, () => {
        let dialog = new BaseDialog("[red]更新日志");
        dialog.cont.pane((() => {
            let table = new Table();//, fl = new FLabel("{fade}" + lib.bundle("text-updata"));
            table.add(lib.bundle("text-updata")).left().wrap().width(540).maxWidth(540).labelAlign(Align.left);
            table.row();
            return table;
        })()).grow().center().maxWidth(600);
        dialog.buttons.button("[red]历史更新", Icon.infoCircle, () => {
            let dialog = new BaseDialog("[red]历史更新");
            dialog.cont.pane((() => {
                let table = new Table();
                table.add(lib.bundle("text-history")).left().wrap().width(540).maxWidth(540).labelAlign(Align.left);
                table.row();
                return table;
            })()).grow().center().maxWidth(600);
            dialog.addCloseButton();
            dialog.show();
        }).size(210, 64);
        dialog.addCloseButton();
        dialog.show();
    }).size(210, 64);
    dialog.addCloseButton();
    dialog.show();

    /*let time = 5 * 60;
    dialog.buttons.button("", () => {
        Vars.player.unit().heal();
    }).size(45).disabled(b => time > 0).update(b => {
        if (time > 0) {
            time -= Time.delta
            b.setText("(" + parseInt(time / 60) + ")");
        } else {
            b.setText("OK");
            b.update(null);
        }
    }).row(); //打开后5秒允许关闭，显示倒计时*/

    /*Events.on(EventType.ResizeEvent, () => {
        const container = Reflect.get(Vars.ui.menufrag, "container");
        container.row();
        container.add(new MobileButton(Icon.download, Core.bundle.format('report'), () => dialog.show()));
    });*///主界面增加按钮，点击显示简介

    /*Events.on(EventType.ClientLoadEvent, e => {
        let i = 0;
        Time.run(30, () => {
            Core.scene.root.getChildren().each(e => e.fillParent && !([Vars.ui.menuGroup, Vars.ui.hudGroup, Vars.ui.chatfrag, Vars.ui.consolefrag, Vars.ui.load].includes(e)) && e.visible && e instanceof BaseDialog, e => {
                e.hide();
                i++;
            });
            if (i > 0) Vars.ui.showOkText("BaseDialogKiller", "总共杀死了" + i + "个垃圾弹窗", () => {});
        })
    })*/

    Vars.content.blocks().each(block => {
        let armor = Math.ceil(Math.pow(block.size, 2) / 2)
        if (block.armor < armor) block.armor = armor;
    }); //给所有方块按尺寸添加护甲！！！

    UnitSorts.closest = (u, x, y) => -u.targetPriority + Mathf.dst2(u.x, u.y, x, y) / 6400;
    UnitSorts.farthest = (u, x, y) => -u.targetPriority - Mathf.dst2(u.x, u.y, x, y) / 6400;

    /*	const loadren = extend(MenuRenderer, { //主界面图片
        render() {
            if (Core.settings.getBool("landscape")) {
                Draw.rect(lib.region("bgi"), Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, 2400, 1440);
            } else {
                Draw.rect(lib.region("bgi"), Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, 1440, 2400);
            }
        }
    })
    function Class(id) {
        return Seq([id]).get(0)
    }
    var fi = Class(MenuFragment).getDeclaredField("renderer");
    fi.setAccessible(true);
    fi.set(Vars.ui.menufrag, loadren);*/
});
//pad(4)可活动量
//wrap()文本超出时可活动/自动换行