package singularity.world.unit;

import arc.Events;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import arc.util.serialization.Jval;
import ice.content.IStatus;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.mod.Mods;
import mindustry.type.UnitType;
import mindustry.world.meta.StatUnit;
import singularity.Sgl;
import singularity.core.ModsInteropAPI;
import singularity.world.meta.SglStat;

import java.io.DataInput;
import java.io.DataOutput;

public class EMPHealthManager {
  private final ObjectMap<Unit, EMPModel.EMPHealth> healthMap = new ObjectMap<>();
  private final ObjectMap<UnitType, EMPModel> unitDefaultHealthMap = new ObjectMap<>();

  private Unit lastGetter;
  private EMPModel.EMPHealth lastGetted;

  private static final EMPModel.EMPHealth ZERO = new EMPModel.EMPHealth();

  public void init() {
    /*添加mod交互API模型，用于其他mod定义单位的EMP生命模型
     * 通常条目格式:
     * ...
     * "empHealthModels": {
     *   “$unitTypeName”:{  //选中单位的内部名称，mod名称前缀可选，默认选择本mod中的content，一般不建议跨mod配置单位数据
     *     "maxEmpHealth": #, //最大EMP生命值
     *     "empArmor": #,  //EMP伤害减免百分比
     *     "empRepair": #,  //EMP损伤自动恢复速度（/tick）
     *     "empContinuousDamage": #  //电磁损毁状态下的持续生命扣除速度（/tick）
     *   },
     *   “$unitTypeName”:{
     *     "maxEmpHealth": #,
     *     "empArmor": #,
     *     "empRepair": #,
     *     "empContinuousDamage": #
     *   },
     *   ...
     * }
     * ...
     *
     * 若需要禁用某一单位的EMP损伤机制则作如下声明:
     * ...
     * "empHealthModels": {
     *   "$unitTypeName": {
     *     "disabled": true
     *   },
     *   ...
     * }
     * ...
     * */
    if (Sgl.config.interopAssignEmpModels) {
      Sgl.interopAPI.addModel(new ModsInteropAPI.ConfigModel("empHealthModels") {
        @Override
        public void parse(Mods.LoadedMod mod, Jval declaring) {
          Jval.JsonMap declares = declaring.asObject();

          for (ObjectMap.Entry<String, Jval> entry : declares) {
            UnitType unit = ModsInteropAPI.selectContent(ContentType.unit, entry.key, mod, true);

            EMPModel model = new EMPModel();
            model.disabled = entry.value.getBool("disabled", false);

            if (!model.disabled) {
              model.maxEmpHealth = entry.value.getFloat("maxEmpHealth", unit.health / Mathf.pow(unit.hitSize - unit.armor, 2) * 200);
              model.empArmor = entry.value.getFloat("empArmor", Mathf.clamp(unit.armor / 100));
              model.empRepair = entry.value.getFloat("empRepair", unit.hitSize / 60);
              model.empContinuousDamage = entry.value.getFloat("empContinuousDamage", unit.hitSize / 30);

              unit.stats.add(SglStat.empHealth, model.maxEmpHealth);
              unit.stats.add(SglStat.empArmor, model.empArmor * 100, StatUnit.percent);
              unit.stats.add(SglStat.empRepair, model.empRepair * 60, StatUnit.perSecond);
            }

            unitDefaultHealthMap.put(unit, model);
          }
        }

        @Override
        public void disable(Mods.LoadedMod mod) {
          for (UnitType unit : Vars.content.units()) {
            if (unit.minfo.mod == mod) {
              EMPModel model = new EMPModel();
              model.disabled = true;

              unitDefaultHealthMap.put(unit, model);
            }
          }
        }
      }, false);
    }
    var i = new Interval();
    Events.run(EventType.Trigger.update, () -> {

      Groups.unit.each(u -> {
        if (Vars.state.isGame()) {
          update(u);
        }
      });
      if (!i.get(60)) return;
      Groups.unit.each(u -> {
        if (Vars.state.isGame()) {
          if (u == null) return;
          if (!healthMap.containsKey(u)) healthMap.put(u, getInst(u));
          healthMap.each((unit, health) -> {
            if (unit != null && !unit.isAdded()) {
              EMPModel.EMPHealth h = healthMap.remove(unit);
              if (h != null) Pools.free(h);
            }
          });

        }
      });
    });

    SaveVersion.addCustomChunk("empHealth", new SaveFileReader.CustomChunk() {
      @Override
      public boolean shouldWrite() {
        return true;
      }

      @Override
      public void read(DataInput stream, int length) {
        read(stream);
      }

      @Override
      public void write(DataOutput stream) {
        Writes write = new Writes(stream);
        write.i(healthMap.size);
        for (ObjectMap.Entry<Unit, EMPModel.EMPHealth> entry : healthMap) {
          write.f(entry.key.x);
          write.f(entry.key.y);
          write.i(entry.key.type.id);
          write.f(entry.value.empHealth);
        }

      }

      @Override
      public void read(DataInput stream) {

        try (Reads read = new Reads(stream)) {
          int len = read.i();
          for (int i = 0; i < len; i++) {
            float x = read.f();
            float y = read.f();
            float id = read.i();

            float health = read.f();

            Unit unit = null;
            for (Unit u : Groups.unit) {
              if (!Mathf.equal(u.x, x) || !Mathf.equal(u.y, y)) continue;
              if (u.type.id != id) continue;

              unit = u;
              break;
            }

            if (unit == null) {
              Log.err("emp index unit not found in (" + x + ", " + y + ")");
              continue;
            }

            EMPModel.EMPHealth heal = getInst(unit);
            heal.empHealth = health;
            healthMap.put(heal.unit, heal);

            // unit.update();
          }
        }
      }
    });

    for (UnitType unit : Vars.content.units()) {
      getModel(unit);
    }
  }

  public void setEmpModel(UnitType type, float maxHealth, float armor, float repair, float empContDam) {
    type.immunities.remove(IStatus.INSTANCE.get电磁损毁());
    unitDefaultHealthMap.put(type, new EMPModel() {{
      this.maxEmpHealth = maxHealth;
      this.empArmor = armor;
      this.empRepair = repair;
      this.empContinuousDamage = empContDam;
    }});
  }

  public void setEmpDisabled(UnitType type) {
    unitDefaultHealthMap.put(type, new EMPModel() {{
      disabled = true;
    }});
  }

  public EMPModel getModel(UnitType type) {
    return unitDefaultHealthMap.get(type, () -> {
      type.immunities.remove(IStatus.INSTANCE.get电磁损毁());

      EMPModel res = new EMPModel();
      res.maxEmpHealth = type.health / Mathf.pow(type.hitSize - type.armor, 2) * 200;
      res.empArmor = Mathf.clamp(type.armor / 100, 0, 0.9f);
      res.empRepair = type.hitSize / 60;
      res.empContinuousDamage = res.empRepair * 2;

      type.stats.add(SglStat.empHealth, res.maxEmpHealth);
      type.stats.add(SglStat.empArmor, res.empArmor * 100, StatUnit.percent);
      type.stats.add(SglStat.empRepair, res.empRepair * 60, StatUnit.perSecond);
      return res;
    });
  }

  public EMPModel.EMPHealth getInst(Unit unit) {
    return getModel(unit.type).generate(unit);
  }

  public EMPModel.EMPHealth zeroInst(Unit unit) {
    ZERO.model = getModel(unit.type);
    ZERO.unit = unit;
    ZERO.empHealth = 0;
    return ZERO;
  }

  public void update(Unit unit) {
    if (Vars.state.isPaused()) return;
    EMPModel.EMPHealth h = get(unit);
    if (h.model.disabled) return;
    if (!unit.hasEffect(IStatus.INSTANCE.get电磁损毁())) {
      if (h.empHealth <= 0) {
        unit.shield = 0;
        Fx.unitShieldBreak.at(unit.x, unit.y, 0.0F, unit.team.color, unit);
        unit.apply(IStatus.INSTANCE.get电磁损毁(), 660);
      } else if (h.empHealth < h.model.maxEmpHealth) h.empHealth += h.model.empRepair * Time.delta;
    }
  }

  public EMPModel.EMPHealth get(Unit unit) {
    if (!unit.isAdded()) return zeroInst(unit);
    if (lastGetted != null && unit == lastGetter && lastGetted.bind) return lastGetted;

    return lastGetted = healthMap.get(lastGetter = unit, () -> getInst(unit));
  }

  public boolean empDamaged(Unit unit) {
    EMPModel.EMPHealth h = get(unit);

    if (h.model.disabled) return false;

    return h.empHealth < h.model.maxEmpHealth;
  }

  public float getHealth(Unit unit) {
    if (get(unit).model.disabled) return 1;
    return get(unit).empHealth;
  }

  public float healthPresent(Unit unit) {
    EMPModel.EMPHealth h = get(unit);

    if (h.model.disabled) return 1;

    return Mathf.clamp(h.empHealth / h.model.maxEmpHealth);
  }

  public float empDamage(Unit unit, float damage, boolean realDam) {
    EMPModel.EMPHealth h = get(unit);

    if (h.model.disabled) return 0;

    float real = Mathf.maxZero(realDam ? damage : damage - damage * h.model.empArmor);
    float orig = h.empHealth;
    h.empHealth = Mathf.maxZero(h.empHealth - real);

    return orig - h.empHealth;
  }

  public void heal(Unit unit, float heal) {
    EMPModel.EMPHealth h = get(unit);

    if (h.model.disabled) return;

    h.empHealth = Math.min(h.empHealth + heal, h.model.maxEmpHealth);
  }
}
