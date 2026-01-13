package universecore.util;

import arc.func.Func;
import arc.struct.LongMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.serialization.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**用于包装一个对象，实现此接口的对象可以自定义数据序列化为字节流的行为，可参考{@link java.io.Serializable}，不过这通常会快于java的序列化，因为往往我们并不需要传递一个对象的完整信息
 * <p>下面是一个用例：
 * <pre>{@code
 *   //声明一个可包装的类型
 *   public class Data implements DataPackable{
 *     private final static long typeID = 1587541965784324577L;
 *
 *     static{
 *       DataPackable.assignType(typeID, args -> new Data());
 *     }
 *
 *     String name;
 *     float health;
 *     boolean alive;
 *
 *     @Override
 *     public long typeID() {
 *       return typeID;
 *     }
 *
 *     @Override
 *     public void write(Writes write) {
 *       write.str(name);
 *       write.f(health);
 *       write.bool(alive);
 *     }
 *
 *     @Override
 *     public void read(Reads read) {
 *       name = read.str();
 *       health = read.f();
 *       alive = read.bool();
 *     }
 *   }
 * }</pre>
 * 那么，使用这个对象并完成数据包装和拆解：
 * <pre>{@code
 *   Data d = new Data();
 *   d.name = "EBwilson";
 *   d.health = 100;
 *   d.alive = true;
 *
 *   byte[] dataArr = a.pack();//包装数据
 *   Data d1 = DataPackable.readObject(dataArr);//直接读取为一个新的具有相同属性的实例
 *   Data d2 = new Data();
 *   d2.read(dataArr);//先实例化，再从数组读取属性
 * }</pre>
 * */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface DataPackable{
  LongMap<Func> objectProvMap = new LongMap<>();

  /**注册一个可包装类型的构造函数，构造函数会传入一个对象数组作为参数，返回处于初始状态的对象
   *
   * @param typeID 构造这个类型的ID
   * @param constructor 使用该ID执行数据解析时创建原始对象的方法*/
  static void assignType(long typeID, Func<Object[], Object> constructor){
    if(objectProvMap.put(typeID, constructor) != null)
      throw new SerializationException("conflicting id, type id: " + typeID + "was assigned");
  }

  /**从一个打包的字节数组读取并返回一个具有包装信息的新的对象，注意，传入的构造参数没有类型检查，通常不建议使用和注册含有参数的构造器*/
  static <T extends DataPackable> T readObject(byte[] bytes, Object... param){
    long id = new Reads(new DataInputStream(new ByteArrayInputStream(bytes))).l();
    Func<Object[], T> objProv = (Func<Object[], T>)objectProvMap.get(id);
    if(objProv == null)
      throw new SerializationException("type id: " + id + " was not assigned");
    
    T result = objProv.get(param);
    result.read(bytes);
    return result;
  }

  /**这个对象的类型标识ID，对于一个类而言，任何时候其实例的该方法都必须返回同一个ID*/
  long typeID();

  /**包装该实例写出对象信息的方法，实现时应当在这个方法写出对象需要打包的信息
   *
   * @param write 写出数据使用的工具*/
  void write(Writes write);

  /**在打开包装时用于读取包装信息的方法，这应当是{@link DataPackable#write(Writes)}逆操作，将数据反过来设置到对象当中
   *
   * @param read 传入的数据阅读工具*/
  void read(Reads read);

  /**将该对象包装为一个字节数组传出*/
  default byte[] pack(){
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Writes write = new Writes(new DataOutputStream(outputStream));
    write.l(typeID());
    write(write);
    
    return outputStream.toByteArray();
  }

  /**读取一个数据字节数组，这个数组必须是来自该对象包装所得到的数组，或者必须在结构上是一致的
   *
   * @param bytes 待读取的数据数组
   * @throws SerializationException 若给定的字节数组并不是这个对象打包得到的*/
  default void read(byte[] bytes){
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    Reads read = new Reads(new DataInputStream(inputStream));
    if(read.l() != typeID())
      throw new SerializationException("type id was unequal marked type id");
    read(read);
  }
}
