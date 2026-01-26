package singularity.world.blocks.distribute;

import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Cons3;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import singularity.world.distribution.GridChildType;
import universecore.util.DataPackable;
import universecore.util.Empties;

public class TargetConfigure implements DataPackable {
  public static final long typeID = 6253491887543618527L;
  public static final int FLIP_X = 0b0101;
  public static final int FLIP_Y = 0b1010;

  public int offsetPos = Point2.pack(0, 0);
  public int priority;

  protected ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> data = new ObjectMap<>();
  protected ObjectMap<GridChildType, ObjectMap<UnlockableContent, byte[]>> directBits = new ObjectMap<>();

  public void set(GridChildType type, UnlockableContent content, byte[] dirBit) {
    data.get(type, ObjectMap::new).get(content.getContentType(), ObjectSet::new).add(content);
    directBits.get(type, ObjectMap::new).put(content, dirBit);
  }

  public boolean remove(GridChildType type, UnlockableContent content) {
    boolean result = data.get(type, Empties.nilMapO()).get(content.getContentType(), Empties.nilSetO()).remove(content);
    directBits.get(type, Empties.nilMapO()).remove(content);
    return result;
  }

  public boolean get(GridChildType type, UnlockableContent content) {
    return data.get(type, Empties.nilMapO()).get(content.getContentType(), Empties.nilSetO()).contains(content);
  }

  public void each(Cons3<GridChildType, ContentType, UnlockableContent> cons) {
    for (ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> entry : data) {
      for (ObjectMap.Entry<ContentType, ObjectSet<UnlockableContent>> contEntry : entry.value) {
        for (UnlockableContent content : contEntry.value) {
          cons.get(entry.key, contEntry.key, content);
        }
      }
    }
  }

  public void eachChildType(Cons2<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> cons) {
    for (ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> entry : data) {
      for (ObjectSet<UnlockableContent> value : entry.value.values()) {
        if (!value.isEmpty()) {
          cons.get(entry.key, entry.value);
          break;
        }
      }
    }
  }

  public byte[] getDirectBit(GridChildType type, UnlockableContent content) {
    return directBits.get(type, Empties.nilMapO()).get(content, new byte[1]);
  }

  public boolean directValid(GridChildType type, UnlockableContent content, byte match) {
    byte bit = getDirectBit(type, content)[0];
    if (bit <= 0 || match <= 0) return false;
    return (bit & match) != 0;
  }

  public ObjectSet<UnlockableContent> get(GridChildType type, ContentType t) {
    return data.get(type, Empties.nilMapO()).get(t, Empties.nilSetO());
  }

  public ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> get() {
    clip();
    return data;
  }

  public boolean any() {
    for (ObjectMap<ContentType, ObjectSet<UnlockableContent>> conts : data.values()) {
      for (ObjectSet<UnlockableContent> cont : conts.values()) {
        if (!cont.isEmpty()) return true;
      }
    }
    return false;
  }

  public void clip() {
    for (ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> entry : data) {
      if (entry.value != null) {
        if (entry.value.isEmpty()) data.remove(entry.key);
        else {
          for (ObjectMap.Entry<ContentType, ObjectSet<UnlockableContent>> setEntry : entry.value) {
            if (setEntry.value != null && setEntry.value.isEmpty()) entry.value.remove(setEntry.key);
          }
        }
      }
    }
  }

  public boolean isContainer() {
    for (GridChildType type : data.keys()) {
      if (type == GridChildType.container) {
        return true;
      }
    }
    return false;
  }

  @Override
  public long typeID() {
    return typeID;
  }

  @Override
  public void write(Writes write) {
    write.i(offsetPos);
    write.i(priority);

    write.i(data.size);
    for (ObjectMap.Entry<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> entry : data) {
      write.i(entry.key.ordinal());
      write.i(entry.value.size);
      for (ObjectMap.Entry<ContentType, ObjectSet<UnlockableContent>> dataEntry : entry.value) {
        write.i(dataEntry.key.ordinal());
        write.i(dataEntry.value.size);
        for (UnlockableContent v : dataEntry.value) {
          write.i(v.id);
        }
      }
    }

    write.i(directBits.size);
    for (ObjectMap.Entry<GridChildType, ObjectMap<UnlockableContent, byte[]>> entry : directBits) {
      write.i(entry.key.ordinal());
      write.i(entry.value.size);
      for (ObjectMap.Entry<UnlockableContent, byte[]> cEntry : entry.value) {
        write.i(cEntry.key.getContentType().ordinal());
        write.i(cEntry.key.id);
        write.b(cEntry.value[0]);
      }
    }
  }

  @Override
  public void read(Reads read) {
    offsetPos = read.i();
    priority = read.i();

    data = new ObjectMap<>();
    int count = read.i(), count2, amount;
    for (int i = 0; i < count; i++) {
      ObjectMap<ContentType, ObjectSet<UnlockableContent>> map = data.get(GridChildType.values()[read.i()], ObjectMap::new);
      count2 = read.i();
      for (int l = 0; l < count2; l++) {
        ContentType type = ContentType.values()[read.i()];
        ObjectSet<UnlockableContent> set = map.get(type, ObjectSet::new);
        amount = read.i();
        for (int i1 = 0; i1 < amount; i1++) {
          set.add(Vars.content.getByID(type, read.i()));
        }
      }
    }

    directBits = new ObjectMap<>();
    int size = read.i(), length;
    for (int i = 0; i < size; i++) {
      ObjectMap<UnlockableContent, byte[]> map = directBits.get(GridChildType.values()[read.i()], ObjectMap::new);
      length = read.i();
      for (int l = 0; l < length; l++) {
        int typeId = read.i();
        map.get(Vars.content.getByID(ContentType.values()[typeId], read.i()), () -> new byte[]{read.b()});
      }
    }
  }

  public void rotateDir(int direction) {
    for (ObjectMap<UnlockableContent, byte[]> bitMap : directBits.values()) {
      for (byte[] arr : bitMap.values()) {
        int bits = arr[0];
        if (direction >= 0) {
          bits = bits << 1;
          if ((bits & (1 << 4)) != 0) {
            bits = ((bits | 1) & 15);
          }
        } else {
          boolean b = (bits & 1) != 0;
          bits = bits >> 1;
          if (b) bits = bits | 1 << 3;
        }
        arr[0] = (byte) bits;
      }
    }
  }

  public void flip(boolean x) {
    for (ObjectMap<UnlockableContent, byte[]> bitMap : directBits.values()) {
      for (byte[] arr : bitMap.values()) {
        int bits = arr[0];

        if (x){
          if ((bits & FLIP_X) != 0 && (bits & FLIP_X) != FLIP_X){
            bits ^= FLIP_X;
          }
        }
        else{
          if ((bits & FLIP_Y) != 0 && (bits & FLIP_Y) != FLIP_Y){
            bits ^= FLIP_Y;
          }
        }

        arr[0] = (byte) bits;
      }
    }
  }

  public void clear() {
    priority = 0;
    data.clear();
    directBits.clear();
  }

  public boolean isClear() {
    if (data.isEmpty()) return true;
    for (ObjectMap<ContentType, ObjectSet<UnlockableContent>> map : data.values()) {
      for (ObjectSet<UnlockableContent> value : map.values()) {
        if (!value.isEmpty()) return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "TargetConfigure{" +
        "position=" + Point2.unpack(offsetPos) +
        ", priority=" + priority +
        ", data=" + data +
        ", directBits=" + directBits +
        '}';
  }

  @Override
  public TargetConfigure clone() {
    TargetConfigure conf = new TargetConfigure();
    conf.read(pack());
    return conf;
  }

  public void configHandle(Cons<Point2> transformer){
    Point2 res = Point2.unpack(offsetPos);
    transformer.get(res);
    offsetPos = res.pack();

    Point2 t1 = new Point2(4, 0);
    transformer.get(t1);

    if(t1.x == 0 && t1.y > 0){
      rotateDir(1);
    }
    else if(t1.x == 0 && t1.y < 0){
      rotateDir(-1);
    }
    else flip(t1.x < 0 && t1.y == 0);
  }
}
