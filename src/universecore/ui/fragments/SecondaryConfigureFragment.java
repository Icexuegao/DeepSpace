package universecore.ui.fragments;

import arc.Core;
import arc.Events;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.ui.fragments.BlockConfigFragment;
import universecore.components.blockcomp.SecondableConfigBuildComp;

import static mindustry.Vars.state;

/**
 * 方块的二级配置面板，通常在{@link universecore.UncCore#secConfig}保存了一个实例，默认使用这个实例而不是创建一个新的
 * @author EBwilson
 * @since 1.5
 */
public class SecondaryConfigureFragment {
  protected BlockConfigFragment config = Vars.control.input.config;
  protected Table table = new Table();

  protected Building configCurrent;
  protected SecondableConfigBuildComp configuring;

  public void build(Group parent) {
    parent.addChild(table);

    Core.scene.add(new Element() {
      @Override
      public void act(float delta) {
        super.act(delta);
        if (state.isMenu()) {
          table.visible = false;
          configCurrent = null;
        } else {
          table.visible = config.isShown() && configCurrent != null;
          if (!table.visible) table.clearChildren();
          Building b = config.getSelected();
          configuring = b instanceof SecondableConfigBuildComp ? (SecondableConfigBuildComp) b : null;
        }
      }
    });

    Events.on(EventType.ResetEvent.class, e -> {
      table.visible = false;
      configCurrent = null;
    });
  }

  /**
   * 打对当前配置的方块打开对目标方块的二级配置菜单
   * @param target 二级配置执行的目标方块
   */
  public void showOn(Building target) {
    configCurrent = target;

    table.visible = true;
    table.clear();
    configuring.buildSecondaryConfig(table, target);
    table.pack();
    table.setTransform(true);
    table.actions(Actions.scaleTo(0f, 1f), Actions.visible(true), Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));

    table.update(() -> {
      table.setOrigin(Align.center);
      if (configuring == null || configCurrent == null || configCurrent.block == Blocks.air || !configCurrent.isValid()) {
        hideConfig();
      } else {
        configCurrent.updateTableAlign(table);
      }
    });
  }

  public Building getConfiguring() {
    return configCurrent;
  }

  public void hideConfig() {
    configCurrent = null;
    table.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
  }
}
