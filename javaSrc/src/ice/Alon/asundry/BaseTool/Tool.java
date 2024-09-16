package ice.Alon.asundry.BaseTool;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import ice.Alon.asundry.world.planet.ADriBaseGenerator;
import ice.Alon.asundry.world.planet.ADriBaseRegistry;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;

public class Tool {
    public static boolean shown = true;


    public static void load() {
        updateZoom();
        SectorID();
        blueprint();
        ToolUi.toolUi();
    }


    public static void blueprint() {
        ADriBaseRegistry base = ADriBaseGenerator.bases;
        base.load();
        Log.info("蓝图加载完毕");
        Log.info("剩余parts蓝图数量为:" + base.parts.size);
        Log.info("剩余cores蓝图数量为:" + base.cores.size);

    }

    public static void updateZoom() {
        float minZoomLim = 0.5f;
        float maxZoomLim = 40f;
        Vars.renderer.minZoom = minZoomLim;
        Vars.renderer.maxZoom = maxZoomLim;
        Log.info("缩放加载完毕");
        Log.info("现在最小缩放:" + Vars.renderer.minZoom);
        Log.info("现在最大缩放:" + Vars.renderer.maxZoom);
    }


    public static void SectorID() {
        shown = Core.settings.getBool("planet-sector-id", true);
        Events.on(ClientLoadEvent.class, (e)->{
            final Font font = Fonts.outline;
            final float fontScl = 0.6F / Scl.scl();
            Vars.ui.planet = new PlanetDialog() {
                {
                    shown(this::rebuildButton);
                }

                @Override
                public void renderProjections(Planet planet) {
                    super.renderProjections(planet);
                    if (shown) {
                        float alpha = this.state.uiAlpha;
                        if (!(alpha < 1.0E-4F)) {

                            for (Sector sec : planet.sectors) {
                                this.planets.drawPlane(sec, ()->font.draw(String.valueOf(sec.id), 0.0F, 0.0F, Color.white, fontScl, true, 1));
                            }
                        }
                    }
                }

                void rebuildButton() {
                    Stack stack = (Stack) this.getChildren().get(0);
                    Table table = (Table) stack.getChildren().get(3);
                    table.row();
                    table.table(Styles.black6, (t)->t.button("显示星球区块id", Styles.flatTogglet, ()->{
                        shown = !shown;
                        Core.settings.put("planet-sector-id", shown);
                    }).height(43.333332F).growX().checked((b)->shown)).fillX();
                }
            };
        });
        Log.info("星球区块ID加载完毕");
    }
}
