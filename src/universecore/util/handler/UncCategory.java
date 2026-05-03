package universecore.util.handler;

import arc.input.KeyBind;
import arc.util.Nullable;
import mindustry.type.Category;

class UncCategory {
  private static final EnumHandler<Category> handler = new EnumHandler<>(Category.class);

  final Category cat;
  @Nullable
  final KeyBind bind;
  int ordinal;
  final String icon;

  UncCategory(Category cat, KeyBind bind, String icon){
    this.cat = cat;
    this.icon = icon;
    ordinal = cat.ordinal();
    this.bind = bind;
  }

  UncCategory(String name, int ordinal, KeyBind bind, String icon){
    this(handler.addEnumItem(name, ordinal), bind, icon);
    FieldHandler.setValueDefault(Category.class, "all", Category.values());
    this.ordinal = ordinal;
  }
}
