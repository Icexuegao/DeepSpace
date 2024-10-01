package ice.Alon.library.tool;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
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
    }

    /**
     * 缩放
     */
    public static void updateZoom() {
        float minZoomLim = 0.5f;
        float maxZoomLim = 40f;
        Vars.renderer.minZoom = minZoomLim;
        Vars.renderer.maxZoom = maxZoomLim;
    }

    /**
     * 星球ID
     */
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
    }
}
