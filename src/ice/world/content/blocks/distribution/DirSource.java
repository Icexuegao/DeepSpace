package ice.world.content.blocks.distribution;

import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import org.jetbrains.annotations.NotNull;
import singularity.world.blocks.SglBlock;
import singularity.world.blocks.nuclear.EnergySource;
import universecore.world.consumers.ConsumeType;

import java.util.Objects;

import static mindustry.Vars.content;
import static mindustry.type.ItemStack.with;

public class DirSource extends EnergySource {
  public float powerProduction = 1000000f/60f;
  public float heatOutput = 1000f;

  public DirSource(@NotNull String name){
    super(name);
    hasPower = true;
    outputsPower = true;
    consumesPower = false;
    conductivePower = true;
    rotateDraw = false;
    update = true;
    solid = true;
    canOverdrive = false;
    group = BlockGroup.transportation;
    schematicPriority = -9;
    selectionRows = 5;
    selectionColumns = 6;
    noUpdateDisabled = true;
    envEnabled = Env.any;
    placeableLiquid = true;
    alwaysUnlocked = true;
    rotate = true;
    regionRotated1 = 1;
    configurable = true;
    saveConfig = true;
    clearOnDoubleTap = true;

    requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
    setDrawers(new DrawMulti(
            new DrawDefault(),
            new DrawHeatOutput()
    ));
    config(int[].class, (DirSourceBuild tile, int[] v) -> {
      switch(v[0]){
        case -2:
          tile.ammo = v[1];
          tile.cool = v[2];
          break;
        case 0:
          tile.ammo = v[1];
          break;
        case 1:
          tile.cool = v[1];
          break;
      }
    });
  }



  public class DirSourceBuild extends EnergySourceBuild implements HeatBlock {


    public @Nullable Building turretBuild = null;
    public int ammo = -1;
    public int cool = -1;
    public Seq<UnlockableContent> consumes = new Seq<>();
    public Seq<UnlockableContent> ammoTypes = new Seq<>();
    public Seq<Liquid> coolant = new Seq<>();
    public Seq<Item> sglItems = new Seq<>();
    public Seq<Liquid> sglLiquids = new Seq<>();
    public Seq<UnlockableContent> sglConsumes = new Seq<>();

    public boolean checkBuild(Building other){
      return other != null && other.team == team;
    }

    public UnlockableContent getAmmo(){
      return (ammoTypes.any() && ammo > -1 && ammo < ammoTypes.size)
              ? ammoTypes.get(ammo)
              : null;
    }

    public Liquid getCool(){
      return (coolant.any() && cool > -1 && cool < coolant.size)
              ? coolant.get(cool)
              : null;
    }

    public String boosters(ReloadTurret turret, boolean baseReload, Liquid liquid){
      float reload = turret.reload;
      float maxUsed = turret.coolant.amount;
      float multiplier = turret.coolantMultiplier;
      float reloadRate = (baseReload? 1f : 0f) + maxUsed*multiplier*liquid.heatCapacity;
      float standardReload = baseReload? reload : reload/(maxUsed*multiplier*0.4f);
      float result = standardReload/(reload/reloadRate);
      return "[stat]" + Strings.autoFixed(result*100, 2) + "%";
    }

    @Override
    public void drawSelect(){
      if(turretBuild != null)
        drawItemSelection(getAmmo() != null? getAmmo() : turretBuild.block);
    }

    @Override
    public void updateTile(){
      if(turretBuild instanceof BaseTurret.BaseTurretBuild){
        for(var v : consumes)
          if(v instanceof Item item && turretBuild.acceptItem(this, item))
            turretBuild.handleItem(this, item);
          else if(v instanceof Liquid liquid)
            turretBuild.liquids.set(liquid, turretBuild.block.liquidCapacity);
        if(turretBuild instanceof ItemTurret.ItemTurretBuild build){
          if(getAmmo() instanceof Item item){
            if(build.ammo.size == 1 && ((ItemTurret.ItemEntry) build.ammo.first()).item == item){
              build.ammo.first().amount = build.totalAmmo = ((ItemTurret) build.block).maxAmmo;
            } else {
              build.ammo.clear();
              build.handleItem(this, item);
            }
          } else {
            build.ammo.clear();
            build.totalAmmo = 0;
            build.reloadCounter = 0f;
          }
        } else {
          if(getAmmo() instanceof Liquid liquid)
            turretBuild.liquids.set(liquid, turretBuild.block.liquidCapacity);
          else
            for(var v : ammoTypes)
              if(v instanceof Liquid liquid)
                turretBuild.liquids.set(liquid, 0f);
        }
        if(getCool() != null)
          turretBuild.liquids.set(getCool(), turretBuild.block.liquidCapacity);
        else
          for(var liquid : coolant)
            turretBuild.liquids.set(liquid, 0f);
      } else if(turretBuild instanceof SglBuilding sglBuilding){
        for(var v : sglConsumes)
          if(v instanceof Item item && sglBuilding.acceptItem(this, item))
            sglBuilding.handleItem(this, item);
          else if(v instanceof Liquid liquid && sglBuilding.acceptLiquid(this, liquid))
            sglBuilding.handleLiquid(this, liquid, sglBuilding.block.liquidCapacity = sglBuilding.liquids.get(liquid));
      } else if(turretBuild != null){
        for(var v : consumes)
          if(v instanceof Item item && turretBuild.acceptItem(this, item))
            turretBuild.handleItem(this, item);
          else if(v instanceof Liquid liquid && turretBuild.acceptLiquid(this, liquid))
            turretBuild.handleLiquid(this, liquid, turretBuild.block.liquidCapacity - turretBuild.liquids.get(liquid));
      }
    }

    @Override
    public void onProximityUpdate(){
      consumes.clear();
      ammoTypes.clear();
      coolant.clear();
      sglItems.clear();
      sglLiquids.clear();
      Building b = front();
      if(checkBuild(b)){
        if(b instanceof BaseTurret.BaseTurretBuild baseTurretBuild && (turretBuild == baseTurretBuild || turretBuild == null)){
          turretBuild = baseTurretBuild;
          if(getAmmo() instanceof Item item && !turretBuild.block.consumesItem(item)) ammo = -1;
          if(getAmmo() instanceof Liquid liquid && !turretBuild.block.consumesLiquid(liquid)) ammo = -1;
          if(getCool() != null && !turretBuild.block.consumesLiquid(getCool())) cool = -1;

          if(turretBuild.block.hasItems)
            for(var item : content.items())
              if(turretBuild.block.consumesItem(item))
                consumes.add(item);
          if(turretBuild.block.hasLiquids)
            for(var liquid : content.liquids())
              if(turretBuild.block.consumesLiquid(liquid))
                consumes.add(liquid);
          if(turretBuild instanceof ItemTurret.ItemTurretBuild){
            for(var v : consumes)
              if(v instanceof Item)
                ammoTypes.add(v);
          } else if(turretBuild instanceof LiquidTurret.LiquidTurretBuild){
            for(var v : consumes)
              if(v instanceof Liquid)
                ammoTypes.add(v);
          } else if(turretBuild instanceof ContinuousLiquidTurret.ContinuousLiquidTurretBuild){
            for(var v : consumes)
              if(v instanceof Liquid)
                ammoTypes.add(v);
          }
          for(var v : ammoTypes)
            consumes.remove(v);
          if(((BaseTurret) turretBuild.block).coolant != null)
            for(var liquid : content.liquids())
              if(((BaseTurret) turretBuild.block).coolant.consumes(liquid)){
                coolant.add(liquid);
                consumes.remove(liquid);
              }
        } else if(turretBuild instanceof SglBuilding sglBuilding){
          for(var consumers : ((SglBlock) sglBuilding.block).getConsumers()){
            var consumeItemBase = consumers.get(ConsumeType.Companion.getItem());
            if(consumeItemBase != null)
              for(var v : Objects.requireNonNull(consumeItemBase.getConsItems()))
                sglItems.add(v.item);
            var consumeLiquidBase = consumers.get(ConsumeType.Companion.getLiquid());
            if(consumeLiquidBase != null)
              for(var v : Objects.requireNonNull(consumeLiquidBase.getConsLiquids()))
                sglLiquids.add(v.liquid);
          }
        } else if(b != null){
          turretBuild = b;
          for(var item : content.items())
            if(turretBuild.block.consumesItem(item))
              consumes.add(item);
          for(var liquid : content.liquids())
            if(turretBuild.block.consumesLiquid(liquid))
              consumes.add(liquid);
        } else {
          turretBuild = null;
          ammo = -1;
          cool = -1;
          sglConsumes.clear();
        }
      }
    }

    public ImageButton imageButton(UnlockableContent item){
      // 已经累了喵不想努力了喵兼容多人的事以后再说喵我们需要更好的数据包喵...
      // 然后就是这个多按钮之前没写过本地测试也不方便也没有足够的时间所以暂时先这样...
      // BUG当然是有的崩溃是可能发生的猫猫是在睡觉觉的...欸这懒猫...
      // Tj开头的类是我从自己项目里搬过来的, 写的比较乱而且没注释如果有更好的方法可以直接换...
      // 其实这一整个类里面80%都是搬过来的...以Neil的效率一天是不可能写完的...毕竟当时自己的都打磨了几周...
      // 原版类基本完美兼容喵, Sgl类另说咕咕咕...
      var button = new ImageButton(item.uiIcon, Styles.clearNoneTogglei);
      button.clicked(() -> {
        if(sglConsumes.contains(item))
          sglConsumes.remove(item);
        else
          sglConsumes.add(item);
      });
      return button;
    }

    @Override
    public void buildConfiguration(Table table){
      table.clear();
      table.background(Tex.pane).top();
      if(turretBuild instanceof BaseTurret.BaseTurretBuild){
        TjConfigTable.rowTable(this, table, new Image(turretBuild.block.uiIcon), turretBuild.block.localizedName, ammoTypes, -1, () -> ammo, false, 0);
        if(coolant.any())
          if(turretBuild.block instanceof ReloadTurret turret)
            TjConfigTable.rowTable(this, table, new Image(Icon.star), "强化", coolant.map(item -> item.uiIcon), coolant.map(liquid -> liquid.localizedName + "\n" + boosters(turret, true, liquid)), coolant.indexOf(coolant.max(liquid -> liquid.heatCapacity)), () -> cool, false, 1);
          else
            TjConfigTable.rowTable(this, table, new Image(Icon.star), "强化", coolant, coolant.indexOf(coolant.max(liquid -> liquid.heatCapacity)), () -> cool, false, 1);
        if(consumes.any()) TjConfigTable.rowImageTable(table, new Image(Icon.download), "消耗", consumes);
      } else if(turretBuild instanceof SglBuilding sglBuilding){
        for(var v : sglItems){
          table.add(imageButton(v)).size(40).tooltip(v.localizedName, true).left();
        }
        table.row();
        for(var v : sglLiquids){
          table.add(imageButton(v)).size(40).tooltip(v.localizedName, true).left();
        }
      } else if(turretBuild != null){
        TjConfigTable.rowImageTable(table, new Image(turretBuild.block.uiIcon), turretBuild.block.localizedName, consumes);
      }
    }

    @NotNull
    @Override
    public int[] config(){
      return new int[]{-2, ammo, cool};
    }

    @Override
    public boolean acceptItem(@NotNull Building source, @NotNull Item item){
      return false;
    }

    @Override
    public void handleItem(Building source, Item item){
    }

    @Override
    public boolean acceptLiquid(@NotNull Building source, @NotNull Liquid liquid){
      return false;
    }

    @Override
    public void handleLiquid(Building source, Liquid liquid, float amount){
    }

    @Override
    public float getPowerProduction(){
      return powerProduction;
    }

    @Override
    public float heat(){
      return heatOutput;
    }

    @Override
    public float heatFrac(){
      return 1f;
    }

    @Override
    public void write(@NotNull Writes write){
      super.write(write);
      write.i(ammo);
      write.i(cool);
    }

    @Override
    public void read(@NotNull Reads read, byte revision){
      super.read(read, revision);
      ammo = read.i();
      cool = read.i();
    }

  }

  public static class TjConfigTable {

    private static final float uiSize = 40f;

    public static <Type extends UnlockableContent> void rowTable(Building building, Table table, Image icon, String tip, Seq<Type> items, int favorite, Prov<Integer> holder, boolean closeSelect, int groupIndex){
      rowTable(building, table, icon, tip, items.map(item -> item.uiIcon), items.map(item -> item.localizedName), favorite, holder, closeSelect, groupIndex);
    }

    public static void rowTable(Building building, Table table, Image icon, String tip, Seq<TextureRegion> regions, Seq<String> tips, int favorite, Prov<Integer> holder, boolean closeSelect, int groupIndex){
      table.add(icon).size(uiSize).tooltip(tip, true).center();
      if(regions.any()){
        table.table(configTable(building, regions, tips, holder, closeSelect, groupIndex)).left();
        table.add(imageButton(Icon.undo, () -> building.configure(new int[]{groupIndex, -1}))).size(uiSize).tooltip("重置", true).center();
        if(favorite > -1 && favorite < regions.size)
          table.add(imageButton(Icon.star, () -> building.configure(new int[]{groupIndex, favorite}))).size(uiSize).tooltip("预设", true).center();
        table.row();
      } else table.image(Icon.cancel).size(uiSize).center().row();
    }

    public static Cons<Table> configTable(Building building, Seq<TextureRegion> regions, Seq<String> tips, Prov<Integer> holder, boolean closeSelect, int groupIndex){
      return table -> {
        table.clear();
        table.background(Styles.black6).left().defaults().size(uiSize);
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);

        for(int i = 0; i < regions.size; i += 1){
          configButton(table, group, holder, building::configure, closeSelect, regions.get(i), tips.get(i), groupIndex, i);
        }
      };
    }

    private static ImageButton imageButton(Drawable icon, Runnable r){
      return imageButton(icon, Styles.clearNonei, r);
    }

    private static ImageButton imageButton(Drawable icon, ImageButton.ImageButtonStyle style, Runnable r){
      var button = new ImageButton(icon, style);
      button.clicked(r);
      return button;
    }

    private static void configButton(
            Table table, ButtonGroup<ImageButton> group, Prov<Integer> holder,
            Cons<int[]> consumer, boolean closeSelect,
            TextureRegion region, String tip,
            int groupIndex, final int buttonIndex
    ){
      ImageButton button = table.button(Tex.whiteui, Styles.clearNoneTogglei, 24f, () -> {
        if(closeSelect) Vars.control.input.config.hideConfig();
      }).tooltip(tip, true).group(group).get();

      if(groupIndex != -1)
        button.changed(() -> consumer.get(new int[]{groupIndex, button.isChecked()? buttonIndex : -1}));
      button.getStyle().imageUp = new TextureRegionDrawable(region);
      button.update(() -> button.setChecked(holder.get() == buttonIndex));
    }

    public static <Type extends UnlockableContent> void rowImageTable(Table table, Image icon, String tip, Seq<Type> items){
      table.add(icon).size(uiSize).tooltip(tip, true).center();
      if(items.any())
        table.table(rowImage -> {
          for(var item : items)
            rowImage.table(frame -> frame.image(item.uiIcon).center()).tooltip(item.localizedName, true).size(uiSize);
        }).left().row();
      else table.image(Icon.cancel).size(uiSize).center().row();
    }
  }
}
