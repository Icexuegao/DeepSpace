//蓝钢模板
Events.on(EventType.ClientLoadEvent, cons(e => {
    var dialog = new BaseDialog("");
    dialog.cont.image(Core.atlas.find("")).row();

    dialog.buttons.defaults().size(210, 64);
    dialog.buttons.button("@close", run(() => {
        dialog.hide();
    })).size(210, 64);

    dialog.cont.pane((() => {
        var table = new Table();

        table.add("\n[                           \n[]                                                                      \n[red]当前您游玩的模组版本为:1.0                                      \n建议横屏浏览                                                  \n有bug请联系我\n贴图征集!                                            \n[]————联系方式—————                            \n[white]QQ:2894761300(冰洁雪糕)\n\n\n\n").left().growX().wrap().width(600).maxWidth(1000).pad(4).labelAlign(Align.left);
        table.row();

        table.button("[blue]QQ群", run(() => {
            var dialog2 = new BaseDialog("源计划模组QQ群二维码");
            var table = new Table();
            dialog2.cont.image(Core.atlas.find("源计划-二维码")).row();

            dialog2.buttons.defaults().size(210, 64);
            dialog2.buttons.button("@close", run(() => {
                dialog2.hide();
            })).size(210, 64);
            dialog2.show();
        })).size(210, 64).row();

        table.button("[#cc00ff]更新日志", run(() => {
            var dialog2 = new BaseDialog("[red]-----更新日志-----");
            var table = new Table();
            var t = new Table();
            t.add("\n\n\n\n\n\n\n无");
            dialog2.cont.add(new ScrollPane(t)).size(500, 600).row();
            dialog2.buttons.defaults().size(620, 64);
            dialog2.buttons.button("@close", run(() => {
                dialog2.hide();
            })).size(210, 64);
            dialog2.show();
        })).size(210, 64).row();/*.row()承接*/

        table.button("作者的话", run(() => {
            var dialog3 = new BaseDialog("作者的话");
            var table = new Table();
            var t = new Table();
            t.add("\n本mod明面上是我自己做的，\n但少不了感谢名单上所有人的帮助，以及路人和群友的贴图分享，所以本模组代码以及贴图公开，\n[red]但请表明出处,谢谢\n[]当然也可以私信我，加入你的创意!\n\n\n");

            dialog3.cont.add(new ScrollPane(t)).size(700, 800).row();/*界面大小*/
            dialog3.buttons.defaults().size(620, 64);
            dialog3.buttons.button("@close", run(() => {
                dialog3.hide();
            })).size(210, 64);/*关闭大小*/
            dialog3.show();
        })).size(210, 64).row();


        table.button("[red]感谢名单", run(() => {
            var dialog3 = new BaseDialog("[red]感谢名单");
            var table = new Table();
            var t = new Table();
            t.add("\n[blue]–––––特别鸣谢–––––                                 \n[blue]miner,白嫖怪帕奇维克,硫缺铅,小初/*生*/                     \n年年有余,激进派 沉陨-褪色,炽热                                       \n                                                                   \n[#ff0000]–––––贴图支持–––––                                      \n,啥也不会，白吃等死,卜萝,白嫖怪帕奇维克\n沅继                            \n                                                                        \n[white]–––––感谢名单–––––                                      \n3366,疯狂小翟,死神,星野宫子,真理,喵喵怪                           \nlet mkrd = new 斗蛐蛐之王('mkrd');,地雷教主(lldd)                   \n维生素 准备在你身体里出问题,绿帽子协会会长                        \n'name': '龙哥',我是黑子   \n\n\n");

            dialog3.cont.add(new ScrollPane(t)).size(700, 800).row();/*界面大小*/
            dialog3.buttons.defaults().size(620, 64);
            dialog3.buttons.button("@close", run(() => {
                dialog3.hide();
            })).size(210, 64);/*关闭大小*/
            dialog3.show();
        })).size(210, 64);
        return table;
    })()).grow().center().maxWidth(620);
    dialog.show();

}));
