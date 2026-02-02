package singularity.world.unit;

import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;


/**具有独立变量空间的武器，该种武器会自动将单位的相应{@link WeaponMount}替换为实现了{@link }接口的{@link DataWeaponMount}，并提供了一系列用于访问独立变量区的行为
 * <p>通常这对于自定义程度高的武器会非常有用*/
public class DataWeapon extends SglWeapon{
  public DataWeapon(){
  }

  public DataWeapon(String name){
    super(name);
  }

  @Override
  public void update(Unit unit, WeaponMount mount){
    if(!(mount instanceof DataWeaponMount)){
      for(int i = 0; i < unit.mounts.length; i++){
        if(unit.mounts[i] == mount){
          DataWeaponMount m;
          unit.mounts[i] = m = new DataWeaponMount();
          init(unit, m);
          break;
        }
      }
    }

    super.update(unit, mount);

    if(mount instanceof DataWeaponMount m){
      update(unit, m);
    }
  }

  /**对武器的初始化，此方法通常用于分配武器的初始变量*/
  public void init(Unit unit, DataWeaponMount mount){

  }

  /**经过类型检查与转换的update方法，访问变量的行为应在此方法的覆盖中描述*/
  public void update(Unit unit, DataWeaponMount mount){

  }

  @Override
  public void draw(Unit unit, WeaponMount mount){
    super.draw(unit, mount);
    if(mount instanceof DataWeaponMount m){
      draw(unit, m);
    }
  }

  /**经过类型检查与转换的draw方法，访问变量的绘制行为应在此方法的覆盖中描述*/
  public void draw(Unit unit, DataWeaponMount mount){

  }

  @Override
  protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
    super.shoot(unit, mount, shootX, shootY, rotation);
    if(mount instanceof DataWeaponMount m){
      shoot(unit, m, shootX, shootY, rotation);
    }
  }

  protected void shoot(Unit unit, DataWeaponMount mount, float shootX, float shootY, float rotation){

  }


  public class DataWeaponMount extends WeaponMount{
    public DataWeaponMount(){
      super(DataWeapon.this);
    }
  }
}
