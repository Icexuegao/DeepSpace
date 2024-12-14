package ice.ui.dialogs;

import arc.Core;
import arc.flabel.FLabel;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Scaling;
import ice.world.meta.stat.IceStat;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Iconc;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;

public class ContentInfoDialog extends mindustry.ui.dialogs.ContentInfoDialog {
    public ContentInfoDialog() {
        title.name = "@info.title";
        //  addCloseButton();
        keyDown(key->{
            if (key == Core.keybinds.get(Binding.block_info).key) {
                Core.app.post(this::hide);
            }
        });
    }

    @Override
    public void show(UnlockableContent content) {
        cont.clear();
        Table table = new Table();
        table.margin(10);
        //initialize stats if they haven't been yet
        content.checkStats();

        table.table(title1->{
            title1.image(content.uiIcon).size(Vars.iconXLarge).scaling(Scaling.fit);
            title1.add("[accent]" + content.localizedName + (Core.settings.getBool("console") ? "\n[gray]" + content.name : "")).padLeft(5);
        });

        table.row();

        if (content.description != null) {
            boolean any = content.stats.toMap().size > 0;

            if (any) {
                table.add("@category.purpose").color(Pal.accent).fillX().padTop(10);
                table.row();
            }

            table.add("[lightgray]" + content.displayDescription()).wrap().fillX().padLeft(any ? 10 : 0).width(500f).padTop(any ? 0 : 10).left();
            table.row();

            if (!content.stats.useCategories && any) {
                table.add("@category.general").fillX().color(Pal.accent);
                table.row();
            }
        }

        Stats stats = content.stats;

        for (StatCat cat : stats.toMap().keys()) {
            OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

            if (map.size == 0) continue;

            if (stats.useCategories) {
                table.add("@category." + cat.name).color(Pal.accent).fillX();
                table.row();
            }

            for (Stat stat : map.keys()) {
                table.table(inset->{
                    inset.left();

                    if ((stat instanceof IceStat stat1)) {
                        if (stat1.Flable) {
                            FLabel label = new FLabel(stat1.color + stat1.localized() + ":[]");
                            if (!stat1.defaultToken.isEmpty()) label.setDefaultToken(stat1.defaultToken);
                            inset.add(label).left().top();
                            Seq<StatValue> arr = map.get(stat);
                            for (StatValue value : arr) {
                                value.display(inset);
                                inset.add().size(10f);
                            }
                        } else {
                            inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
                            Seq<StatValue> arr = map.get(stat);
                            for (StatValue value : arr) {
                                value.display(inset);
                                inset.add().size(10f);
                            }
                        }
                    } else {
                        inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
                        Seq<StatValue> arr = map.get(stat);
                        for (StatValue value : arr) {
                            value.display(inset);
                            inset.add().size(10f);
                        }
                    }


                }).fillX().padLeft(10);
                table.row();
            }
        }

        if (content.details != null) {
            table.add("[gray]" + (content.unlocked() || !content.hideDetails ? content.details : Iconc.lock + " " + Core.bundle.get("unlock.incampaign"))).pad(6).padTop(20).width(400f).wrap().fillX();
            table.row();
        }

        content.displayExtra(table);

        ScrollPane pane = new ScrollPane(table);
        cont.add(pane);

        show();
    }

}
