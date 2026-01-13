package singularity.contents;

import ice.content.IItems;
import mindustry.type.Item;

@SuppressWarnings("SpellCheckingInspection")
public class SglItems implements ContentList{
  /**铝*/
  public static Item aluminium,
  /**FEX水晶*/
  crystal_FEX,
  /**充能FEX水晶*/
  crystal_FEX_power,
  /**矩阵合金*/
  matrix_alloy,
  /**强化合金*/
  strengthening_alloy,
  /**气凝胶*/
  aerogel,
  /**简并态中子聚合物*/
  degenerate_neutron_polymer,
  /**铀238*/
  uranium_238,
  /**铀235*/
  uranium_235,
  /**钚239*/
  plutonium_239,
  /**相位封装氢单元*/
  encapsulated_hydrogen_cell,
  /**相位封装氦单元*/
  encapsulated_helium_cell,
  /**浓缩铀235核燃料*/
  concentration_uranium_235,
  /**浓缩钚239核燃料*/
  concentration_plutonium_239,
  /**氢聚变燃料*/
  hydrogen_fusion_fuel,
  /**氦聚变燃料*/
  helium_fusion_fuel,
  /**反物质*/
  anti_metter,
  /**绿藻块*/
  chlorella_block,
  /**绿藻素*/
  chlorella,
  /**碱石*/
  alkali_stone,
  /**絮凝剂*/
  flocculant,
  /**焦炭*/
  coke,
  /**铱*/
  iridium,
  /**核废料*/
  nuclear_waste,
  /**黑晶石*/
  black_crystone,
  /**岩层沥青*/
  rock_bitumen,
  /**铀原矿*/
  uranium_rawore,
  /**铀原料*/
  uranium_rawmaterial,
  /**铱金混合物*/
  iridium_mixed_rawmaterial,
  /**氯铱酸盐*/
  iridium_chloride;

  public void load(){
    aluminium = IItems.INSTANCE.getAluminium();

    crystal_FEX = IItems.INSTANCE.getCrystal_FEX();
    
    crystal_FEX_power = IItems.INSTANCE.getCrystal_FEX_power();
    
    matrix_alloy =IItems.INSTANCE.getMatrix_alloy();
    strengthening_alloy =IItems.INSTANCE.getStrengthening_alloy();
    aerogel = IItems.INSTANCE.getAerogel();
  
    degenerate_neutron_polymer =IItems.INSTANCE.getDegenerate_neutron_polymer();
    
    uranium_238 =IItems.INSTANCE.getUranium_238();
    uranium_235 = IItems.INSTANCE.getUranium_235();
    
    plutonium_239 = IItems.INSTANCE.getPlutonium_239();

    encapsulated_hydrogen_cell =IItems.INSTANCE.getEncapsulated_hydrogen_cell();

    encapsulated_helium_cell = IItems.INSTANCE.getEncapsulated_helium_cell();
    
    concentration_uranium_235 = IItems.INSTANCE.getConcentration_uranium_235();
    
    concentration_plutonium_239 =IItems.INSTANCE.getConcentration_plutonium_239();

    hydrogen_fusion_fuel = IItems.INSTANCE.getHydrogen_fusion_fuel();

    helium_fusion_fuel =IItems.INSTANCE.getHelium_fusion_fuel();

    anti_metter = IItems.INSTANCE.getAnti_metter();
    chlorella_block =IItems.INSTANCE.getChlorella_block();

    chlorella = IItems.INSTANCE.getChlorella();

    alkali_stone = IItems.INSTANCE.getAlkali_stone();

    coke = IItems.INSTANCE.getCoke();

    flocculant = IItems.INSTANCE.getFlocculant();
    iridium = IItems.INSTANCE.getIridium();
    
    nuclear_waste = IItems.INSTANCE.getNuclear_waste();
    black_crystone =IItems.INSTANCE.getBlack_crystone();
    rock_bitumen = IItems.INSTANCE.getRock_bitumen();
    
    uranium_rawore = IItems.INSTANCE.getUranium_rawore();
    
    uranium_rawmaterial = IItems.INSTANCE.getUranium_rawmaterial();
    
    iridium_mixed_rawmaterial = IItems.INSTANCE.getIridium_mixed_rawmaterial();

    iridium_chloride = IItems.INSTANCE.getIridium_chloride();
  }
}
