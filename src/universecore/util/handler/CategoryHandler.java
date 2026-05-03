package universecore.util.handler;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyBind;
import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.type.Category;
import mindustry.ui.Styles;
import singularity.core.UpdatePool;

import java.util.Arrays;

/** 用于增加右下角方块选择栏分类条目的工具
 *
 * @author EBwilson
 * @since 1.0 */
public class CategoryHandler {
  protected final ObjectMap<Category, UncCategory> newCats = new ObjectMap<>();
  protected boolean hasNew = false;
  protected static final KeyBind empBind = KeyBind.add("unBind", KeyCode.unset);

  public Table lastable;

  {
    //适配mdtx的解禁 ps:这很愚蠢
    UpdatePool.INSTANCE.receive("CategoryHandler",()->{
      if (lastable!=null&&lastable.parent!=null&&lastable.parent.parent!=null&&!lastable.parent.parent.parent.hasParent()){
        Core.app.post(this::handleBlockFrag);
      }
    });
  }
  public void handleBlockFrag() {
    if (!hasNew) return;


    Table catTable = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "blockCatTable");
    lastable = catTable;


    Table blockSelect = (Table) catTable.getChildren().get(0);
    Table categories = (Table) catTable.getChildren().get(1);

    Cell<?> pane = blockSelect.getCells().get(0);
    pane.height(240f);

    Seq<Element> catButtons = new Seq<>(categories.getChildren());

    //catButtons应该全是带名字的Button,但是不止为何对多了两个没名字的Image
    catButtons.remove((e) -> e.name == null || e.name.isEmpty());

    categories.clearChildren();
    categories.pane(t -> {
      t.defaults().size(50);
      int count = 0;
      for (Element element : catButtons) {
        if (count++ % 2 == 0 && count != 0) t.row();
        t.add(element);
      }

      if (catButtons.size % 2 != 0) t.image(Styles.black6);
    }).size(catButtons.size > 12 ? 125 : 100, 300).update(pane1 -> {
      if (pane1.hasScroll()) {
        Element result = Core.scene.getHoverElement();
        if (result == null || !result.isDescendantOf(pane1)) {
          Core.scene.setScrollFocus(null);
        }
      }
    });
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   * @param name 类别的内部名称
   * @param ordinal 这个类别在选择栏的显示位置序数
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, int ordinal, String iconName) {
    return add(name, ordinal, null, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   * @param name 类别的内部名称
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, String iconName) {
    return add(name, null, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   * @param name 类别的内部名称
   * @param bind 这个类别绑定到的目标键位
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, KeyBind bind, String iconName) {
    return add(name, Category.values().length, bind, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   * @param name 类别的内部名称
   * @param ordinal 这个类别在选择栏的显示位置序数
   * @param bind 这个类别绑定到的目标键位
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, int ordinal, KeyBind bind, String iconName) {
    hasNew = true;
    UncCategory category = new UncCategory(name, ordinal, bind, iconName);
    newCats.put(category.cat, category);

    return category.cat;
  }

  public void init() {
    KeyBind[] arr = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "blockSelect");
    if (arr.length < Category.all.length) {
      arr = Arrays.copyOf(arr, Category.all.length);
      for (int i = 0; i < arr.length; i++) {
        UncCategory cat = newCats.get(Category.all[i]);
        if (arr[i] == null) {
          arr[i] = cat != null ? cat.bind : empBind;
        }
      }
    }

    FieldHandler.setValueDefault(Vars.ui.hudfrag.blockfrag, "blockSelect", arr);

    for (ObjectMap.Entry<Category, UncCategory> cat : newCats) {
      TextureRegion r = Core.atlas.find(cat.value.icon);
      Core.atlas.addRegion(cat.key.name(), r);
      Icon.icons.put(cat.key.name(), new TextureRegionDrawable(r) {
        @Override
        public float imageSize() {
          return 32f;
        }
      });
    }
  }

}
