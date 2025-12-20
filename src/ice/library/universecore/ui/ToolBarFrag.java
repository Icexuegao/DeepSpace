package ice.library.universecore.ui;

import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.func.Prov;
import arc.scene.style.Drawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import ice.graphics.SglDrawConst;
import ice.library.universecore.util.handler.FieldHandler;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public class ToolBarFrag {
    private final OrderedMap<String, ToolEntry> tools = new OrderedMap<>();

    public Table toolsTable;

    public void init() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            Core.app.post(() -> {
                Table blockCatTable = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "blockCatTable");
                blockCatTable.table(SglDrawConst.grayUIAlpha, tools -> {
                    tools.top().pane(Styles.noBarPane, this::buildTools).growY().width(50).left().get().setScrollingDisabledX(true);
                }).width(50).growY();
            });
        });
    }

    public void buildTools(Table tools) {
        toolsTable = tools;
        tools.clearChildren();
        tools.top().defaults().top().size(50).pad(0);

        for (ToolEntry entry : this.tools.values()) {
            if (!entry.shown) continue;

            ImageButton button = tools.button(Tex.clear, Styles.clearNoneTogglei, entry.listener).update(b -> {
                b.getStyle().imageUp = entry.icon.get();
                b.resizeImage(36);
                b.setChecked(entry.checked.get());
            }).get();
            if (entry.hoverTip != null) {
                button.addListener(new Tooltip(t -> t.background(Tex.button).add(entry.hoverTip.get()).update(l -> {
                    l.setText(entry.hoverTip.get());
                    l.pack();
                })) {{
                    allowMobile = true;
                }});
            }
            tools.row();
        }
    }

    public void addTool(String name, Prov<Drawable> icon, Runnable listener, Boolp checked) {
        tools.put(name, new ToolEntry(icon, listener, checked));
        if (toolsTable != null) buildTools(toolsTable);
    }

    public void addTool(String name, Prov<String> tip, Prov<Drawable> icon, Runnable listener, Boolp checked) {
        tools.put(name, new ToolEntry(icon, listener, checked, tip));
        if (toolsTable != null) buildTools(toolsTable);
    }

    public void removeTool(String name) {
        tools.remove(name);
        if (toolsTable != null) buildTools(toolsTable);
    }

    public void hideTool(String name) {
        setShown(name, false);
    }

    public void showTool(String name) {
        setShown(name, true);
    }

    public void setShown(String name, boolean shown) {
        ToolEntry entry = tools.get(name);
        if (entry != null) {
            entry.shown = shown;
        }

        if (toolsTable != null) buildTools(toolsTable);
    }

    public void clearTools() {
        tools.clear();
    }

    public static class ToolEntry {
        boolean shown = true;

        Prov<Drawable> icon;
        Runnable listener;
        Boolp checked;

        Prov<String> hoverTip;

        public ToolEntry(Prov<Drawable> icon, Runnable listener, Boolp checked) {
            this.icon = icon;
            this.listener = listener;
            this.checked = checked;
        }

        public ToolEntry(Prov<Drawable> icon, Runnable listener, Boolp checked, Prov<String> hoverTip) {
            this.icon = icon;
            this.listener = listener;
            this.checked = checked;
            this.hoverTip = hoverTip;
        }
    }
}
