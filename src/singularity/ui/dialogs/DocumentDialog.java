package singularity.ui.dialogs;

import arc.Core;
import arc.func.Cons;
import arc.func.Intc;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import singularity.graphic.SglDrawConst;

public class DocumentDialog extends BaseDialog {
  @Nullable Intc rebuilder;
  @Nullable Runnable resize;
  @Nullable Table lastPane;

  private final Cell<Table> docCont;

  public final Table doc;

  public DocumentDialog() {
    super("");

    docCont = cont.table().pad(20).maxWidth(1280).grow();
    doc = docCont.get();

    addCloseButton();
    keyDown(KeyCode.escape, this::hide);

    hidden(doc::clearChildren);
  }

  public DocumentDialog contLayout(Cons<Cell<Table>> layout){
    layout.get(docCont);
    doc.invalidateHierarchy();
    return this;
  }

  //showDocument
 /* public void showDocument(String title, Markdown.MarkdownStyle mdStyle, String... markdowns){
    Markdown[] pages = new Markdown[markdowns.length];
    for (int i = 0; i < pages.length; i++) {
      pages[i] = new Markdown(markdowns[i], mdStyle);
    }

    showDocument(title, pages);
  }*/

  public void showDocument(String title, Cons<Table>... tableBuilder){
    Table[] pages = new Table[tableBuilder.length];
    for (int i = 0; i < pages.length; i++) {
      pages[i] = new Table(tableBuilder[i]);
    }

    showDocument(title, pages);
  }

  public void showDocument(String title, Element... documents){
    titleTable.clearChildren();
    titleTable.add(title).color(Pal.accent);

    int[] index = new int[1];
    doc.clearChildren();
    if (documents.length > 0){
      doc.top().table(table -> {
        Cons<Table> l = t -> {
          if (documents.length > 1) {
            if (Core.graphics.isPortrait()){
              t.defaults().growX().height(45);
            }
            else t.defaults().growY().width(40);

            ImageButton bu = t.button(Icon.leftOpen, Styles.clearNonei, () -> {
              index[0]--;
              rebuilder.get(-1);
            }).disabled(b -> index[0] <= 0).get();
            bu.getStyle().up = bu.getStyle().disabled = SglDrawConst.grayUIAlpha;
          }
        };

        Runnable build = () -> table.table(clip -> {
          table.top().defaults().top();
          clip.setClip(true);

          rebuilder = i -> {
            if (i != 0 && lastPane != null) {
              lastPane.actions(
                  Actions.parallel(
                      Actions.alpha(0, 0.5f, Interp.pow3In),
                      Actions.moveBy(-clip.getWidth()/2*i, 0, 0.5f, Interp.pow3In)
                  ),
                  Actions.run(() -> {
                    clip.removeChild(lastPane);
                    lastPane = clip.table(SglDrawConst.padGrayUIAlpha, page -> {
                      page.top().table().get().pane(Styles.smallPane, documents[index[0]]).scrollX(false).get().setFillParent(true);
                    }).scrollX(false).grow().get();
                    lastPane.color.a = 0;

                    float w = clip.getWidth(), h = clip.getHeight();
                    lastPane.actions(Actions.parallel(
                        Actions.alpha(1, 0.5f, Interp.pow3Out),
                        Actions.moveTo(w/2*i, 0),
                        Actions.sizeTo(w, h),
                        Actions.moveTo(0, 0, 0.5f, Interp.pow3Out)
                    ));
                  })
              );
            }
            else {
              lastPane = clip.table(SglDrawConst.padGrayUIAlpha, page -> {
                page.top().table().grow().get().pane(Styles.smallPane, documents[index[0]]).scrollX(false).grow().get().setFillParent(true);
              }).grow().get();
            }
          };
          rebuilder.get(0);
        }).grow();

        Cons<Table> r = t -> {
          if (documents.length > 1) {
            if (Core.graphics.isPortrait()){
              t.defaults().growX().height(45);
            }
            else t.defaults().growY().width(40);

            ImageButton bu = t.button(Icon.rightOpen, new ImageButton.ImageButtonStyle(Styles.clearNonei){{ up = SglDrawConst.grayUIAlpha; }}, () -> {
              index[0]++;
              rebuilder.get(1);
            }).disabled(b -> index[0] >= documents.length - 1).get();
            bu.getStyle().up = bu.getStyle().disabled = SglDrawConst.grayUIAlpha;
          }
        };

        resize = () -> {
          table.clearChildren();

          if (Core.graphics.isPortrait()){
            build.run();
            table.row();
            table.table(b -> {
              l.get(b);
              r.get(b);
            }).fillY().growX();
          }
          else{
            l.get(table.table().growY().fillX().get());
            build.run();
            r.get(table.table().growY().fillX().get());
          }
        };
        resize.run();

        resized(resize);
      }).grow();
      doc.row();
      doc.add("").update(l -> l.setText(Core.bundle.format("misc.page", index[0] + 1, documents.length)));
    }

    show();
  }
}
