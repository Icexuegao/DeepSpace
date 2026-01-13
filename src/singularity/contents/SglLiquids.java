package singularity.contents;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.gen.Puddle;
import mindustry.graphics.Pal;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import singularity.graphic.SglDraw;
import singularity.graphic.SglShaders;
import singularity.type.ReactLiquid;
import singularity.world.SglFx;

@SuppressWarnings("SpellCheckingInspection")
public class SglLiquids implements ContentList{
  /**纯净水*/
  public static Liquid
  purified_water,
  /**FEX流体*/
  FEX_liquid,
  /**相位态FEX流体*/
  phase_FEX_liquid,
  /**藻泥*/
  algae_mud,
  /**酸液*/
  acid,
  /**碱液*/
  lye,
  /**氯化硅溶胶*/
  silicon_chloride_sol,
  /**复合矿物溶液*/
  mixed_ore_solution,
  /**铀盐溶液*/
  uranium_salt_solution,

  //气体
  /**氯气*/
  chlorine,
  /**氦气*/
  helium,
  /**二氧化碳*/
  carbon_dioxide,
  /**二氧化硫*/
  sulfur_dioxide,
  /**孢子云*/
  spore_cloud;

  public void load(){
    purified_water = new Liquid("purified_water", Color.valueOf("#C3DFFF").a(0.8f)){
      {
        heatCapacity = 0.45f;
        temperature = 0.4f;
        flammability = 0;
        explosiveness = 0;
        viscosity = 0.5f;

        boilPoint = 0.5f;
      }

      @Override
      public float react(Liquid other, float amount, Tile tile, float x, float y) {
        if (other == Liquids.water){
          return amount;
        }
        return 0;
      }

      @Override
      public void update(Puddle puddle) {
        super.update(puddle);
        puddle.liquid = Liquids.water;
      }
    };

    FEX_liquid = new Liquid("FEX_liquid", Color.valueOf("#E34248")){
      {
        heatCapacity = 1f;
        explosiveness = 0f;
        flammability = 0f;
        temperature = 0.35f;
        viscosity = 0f;

        effect = OtherContents.crystallize;
      }

      public static final int taskID = SglDraw.nextTaskID();

      @Override
      public void drawPuddle(Puddle puddle) {
        SglDraw.drawTask(taskID, puddle, SglShaders.wave, s -> {
          s.waveMix = Pal.lightishGray;
          s.mixAlpha = 0.2f + Mathf.absin(5, 0.2f);
          s.waveScl = 0.2f;
          s.maxThreshold = 1f;
          s.minThreshold = 0.4f;
        }, super::drawPuddle);
      }
    };
    
    phase_FEX_liquid = new Liquid("phase_FEX_liquid", Color.valueOf("#E34248")){
      {
        heatCapacity = 1.25f;
        explosiveness = 0f;
        flammability = 0f;
        temperature = 0f;
        viscosity = 0f;

        particleEffect = SglFx.crystalFragFex;
        particleSpacing = 48;
      }

      public static final int taskID = SglDraw.nextTaskID();

      @Override
      public void drawPuddle(Puddle puddle) {
        SglDraw.drawTask(taskID, puddle, SglShaders.wave, s -> {
          s.waveMix = Color.white;
          s.mixAlpha = 0.2f + Mathf.absin(3, 0.4f);
          s.waveScl = 0.3f;
          s.maxThreshold = 0.9f;
          s.minThreshold = 0.5f;
        }, super::drawPuddle);
      }
    };
  
    algae_mud = new CellLiquid("algae_mud", Color.valueOf("#6EA145")){{
      heatCapacity = 0.4f;
      explosiveness = 0f;
      flammability = 0f;
      temperature = 0.5f;
      viscosity = 0.5f;

      coolant = false;

      boilPoint = 0.5f;

      spreadDamage = 0;
      spreadTarget = Liquids.water;
      spreadConversion = 1.1f;
      maxSpread = 0.4f;

      colorFrom = color.cpy().lerp(Color.white, 0.25f);
      colorTo = color.cpy().lerp(Color.white, 0.5f);

      canStayOn.addAll(Liquids.oil, Liquids.water);
    }};

    acid = new ReactLiquid("acid", Color.valueOf("#EDF3A9").a(0.75f)){{
      heatCapacity = 0.5f;
      temperature = 0.45f;
      flammability = 0;
      explosiveness = 0;
      viscosity = 0.5f;

      coolant = false;

      boilPoint = 0.55f;

      init = () -> {
        reactWith(lye, effectWith(Fx.vapor, 0.1f, Color.white, -1f));
      };
    }};

    lye = new ReactLiquid("lye", Color.valueOf("#DBFAFF").a(0.75f)){{
      heatCapacity = 0.5f;
      temperature = 0.45f;
      flammability = 0;
      explosiveness = 0;
      viscosity = 0.5f;

      coolant = false;

      boilPoint = 0.55f;

      init = () -> {
        reactWith(acid, effectWith(Fx.vapor, 0.1f, Color.white, -1));
      };
    }};

    silicon_chloride_sol = new Liquid("silicon_chloride_sol", Color.valueOf("#C0B4B0").a(0.8f)){{
      heatCapacity = 0.65f;
      temperature = 0.6f;
      flammability = 0.3f;
      explosiveness = 0;
      viscosity = 0.85f;

      coolant = false;

      boilPoint = 1.5f;
    }};

    mixed_ore_solution = new Liquid("mixed_ore_solution", Color.valueOf("#CBE0E0")){{
      heatCapacity = 0.6f;
      temperature = 0.65f;
      flammability = 0f;
      explosiveness = 0;
      viscosity = 0.5f;

      coolant = false;

      boilPoint = 1f;
    }};

    uranium_salt_solution = new Liquid("uranium_salt_solution", Color.valueOf("#DAF2AA")){{
      heatCapacity = 0.6f;
      temperature = 0.65f;
      flammability = 0f;
      explosiveness = 0;
      viscosity = 0.5f;

      coolant = false;

      boilPoint = 1f;
    }};

    //气体
    chlorine = new Liquid("chlorine", Color.valueOf("#DAF2AA")){{
      gas = true;

      heatCapacity = 0.35f;
      explosiveness = 0.3f;
      flammability = 0.2f;
      temperature = 0.4f;
      viscosity = 0f;
    }};

    helium = new Liquid("helium", Color.valueOf("#D6FFFC")){{
      gas = true;

      heatCapacity = 0.4f;
      explosiveness = 0f;
      flammability = 0f;
      temperature = 0.4f;
      viscosity = 0f;
    }};

    carbon_dioxide = new Liquid("carbon_dioxide", Color.white){{
      gas = true;

      heatCapacity = 1.2f;
      explosiveness = 0;
      flammability = 0;
      temperature = 0.4f;
      viscosity = 0;
    }};

    sulfur_dioxide = new Liquid("sulfur_dioxide", Color.valueOf("#FFCF76")){{
      gas = true;

      heatCapacity = 0.65f;
      explosiveness = 0;
      flammability = 0;
      temperature = 0.4f;
      viscosity = 0;
    }};

    spore_cloud = new Liquid("spore_cloud", Pal.spore){{
      gas = true;

      heatCapacity = 0.5f;
      explosiveness = 0.8f;
      flammability = 0.75f;
      temperature = 0.4f;
      viscosity = 0;
    }};
  }
}
