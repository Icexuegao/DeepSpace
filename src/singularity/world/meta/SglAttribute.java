package singularity.world.meta;

import mindustry.world.meta.Attribute;

public class SglAttribute{
  //沥青属性，影响采集深岩沥青相关的机器效率
  public static Attribute bitumen;
  
  public static Attribute[] all;
  
  public static void load(){
    bitumen = Attribute.add("bitumen");
    
    all = new Attribute[]{bitumen};
  }
  
  public static Attribute[] values(){
    return all;
  }
}
