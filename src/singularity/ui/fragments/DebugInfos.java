package singularity.ui.fragments;

import arc.Core;
import arc.func.Prov;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import singularity.Sgl;
import universecore.world.particles.Particle;

public class DebugInfos {
  public boolean hidden = true;
  public Seq<InfoEntry> displays = new Seq<>();

  Runnable rebuild;

  {
    addMonitor("显示", () -> displays.size);
    addMonitor("粒子数量", Particle::count);
    addMonitor("cloudCouts", () -> Pools.get(Particle.Cloud.class, Particle.Cloud::new, 65536).peak);
  }

  public void addMonitor(String name, Prov<?> value){
    displays.add(new ValueMonitor(name, value));

    if (rebuild != null) rebuild.run();
  }

  public void build(Group parent){
    parent.fill(t -> {
      rebuild = () -> {
        t.clearChildren();
        t.left().bottom().pane(Styles.noBarPane, dis -> {
          dis.left().defaults().growX().fillY().left();
          for (InfoEntry entry : displays) {
            dis.table(ta -> {
              ta.left().defaults().left().padLeft(4);
              entry.display(ta);
            });
            dis.row();
          }
        }).fill().maxHeight(Core.graphics.getHeight());
      };

      rebuild.run();

      t.touchable = Touchable.disabled;
      t.visibility = () -> !hidden && Vars.state.isGame();
    });
  }

  public interface InfoEntry{
    void display(Table table);
  }

  public static class ValueMonitor implements InfoEntry{
    public String name;
    public Prov<?> value;

    public ValueMonitor(String name, Prov<?> value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public void display(Table table) {
      table.add(name + ": ");
      table.add("").update(l -> l.setText(value == null? "null": value.get().toString())).color(Pal.accent);
    }
  }
}
