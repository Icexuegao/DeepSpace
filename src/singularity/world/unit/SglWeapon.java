package singularity.world.unit;

import arc.func.Cons2;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.entities.bullet.BulletType;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import singularity.ui.UIUtils;

public class SglWeapon extends Weapon {
  public Cons2<BulletType, Table> customDisplay;
  public boolean override;

  public SglWeapon(){
  }

  public SglWeapon(String name){
    super(name);
  }

  @Override
  public void addStats(UnitType u, Table t) {
    if(inaccuracy > 0){
      t.row();
      t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int)inaccuracy + " " + StatUnit.degrees.localized());
    }
    if(!alwaysContinuous && reload > 0){
      t.row();
      t.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / reload * shoot.shots, 2) + " " + StatUnit.perSecond.localized());
    }

    if (!override) {
      UIUtils.buildAmmo(t, bullet);
    }
    if (customDisplay != null){
      customDisplay.get(bullet, t);
    }
  }
}
