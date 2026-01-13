package universecore.override.dialogs;

import arc.struct.ObjectSet;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;

/**重写database对话框以重新排序类型（鬼知道臭猫什么时候才能把读写机制改一改）
 * @author EBwilson
 * @since 1.0*/
public class UncDatabaseDialog{
 // private static final DynamicClass UncDatabase = DynamicClass.get("UncDatabase");
  private static final ObjectSet<UnlockableContent> hiddenContents = new ObjectSet<>();

  static {
  /*  UncDatabase.setFunction("rebuild", (s, su, a) -> {
      Table all = s.getVar("all");
      TextField search = s.getVar("search");

      all.clear();
      var text = search.getText();

      for(int j = 0; j < UncContentType.displayContentList.length; j++){
        ContentType type = UncContentType.displayContentList[j];

        Seq<UnlockableContent> array = content.getBy(type).select(c -> c instanceof UnlockableContent u
                && !u.isHidden() && !hiddenContents.contains(u)
                && (text.isEmpty() || u.localizedName.toLowerCase().contains(text.toLowerCase()))).as();

        if(array.size == 0) continue;

        all.add("@content." + type.name() + ".name").growX().left().color(Pal.accent);
        all.row();
        all.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
        all.row();
        all.table(list -> {
          list.left();

          int cols = (int)Mathf.clamp((Core.graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 12), 1, 22);
          int count = 0;

          for(int i = 0; i < array.size; i++){
            UnlockableContent unlock = array.get(i);

            Image image = unlocked(unlock) ? new Image(unlock.uiIcon).setScaling(Scaling.fit) : new Image(Icon.lock, Pal.gray);

            //banned cross
            if(state.isGame() && (unlock instanceof UnitType u && u.isBanned() || unlock instanceof Block b && state.rules.isBanned(b))){
              list.stack(image, new Image(Icon.cancel){{
                setColor(Color.scarlet);
                touchable = Touchable.disabled;
              }}).size(8 * 4).pad(3);
            }else{
              list.add(image).size(8 * 4).pad(3);
            }

            ClickListener listener = new ClickListener();
            image.addListener(listener);
            if(!mobile && unlocked(unlock)){
              image.addListener(new HandCursorListener());
              image.update(() -> image.color.lerp(!listener.isOver() ? Color.lightGray : Color.white, Mathf.clamp(0.4f * Time.delta)));
            }

            if(unlocked(unlock)){
              image.clicked(() -> {
                if(Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0){
                  Core.app.setClipboardText((char)Fonts.getUnicode(unlock.name) + "");
                  ui.showInfoFade("@copied");
                }else{
                  ui.content.show(unlock);
                }
              });
              image.addListener(new Tooltip(t -> t.background(Tex.button).add(unlock.localizedName + (settings.getBool("console") ? "\n[gray]" + unlock.name : ""))));
            }

            if((++count) % cols == 0){
              list.row();
            }
          }
        }).growX().left().padBottom(10);
        all.row();
      }

      if(all.getChildren().isEmpty()){
        all.add("@none.found");
      }
    });*/
  }

  public static void hide(UnlockableContent content){
    hiddenContents.add(content);
  }

  /*public static DatabaseDialog make() {
    return UncCore.classes.getDynamicMaker().newInstance(Vars.ui.database.getClass(), new Class[]{RebuildAsp.class}, UncDatabase).objSelf();
  }*/
  
  private static boolean unlocked(UnlockableContent content){
    return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
  }

//  @AspectInterface
  public interface RebuildAsp{
    void rebuild();
  }
}
