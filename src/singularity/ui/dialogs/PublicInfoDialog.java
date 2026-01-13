package singularity.ui.dialogs;

import arc.Core;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

public class PublicInfoDialog extends BaseDialog {
    protected static final String directory = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/publicInfo/directory.hjson";
    protected static final String langDir = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/publicInfo/$locale$/$docName$";

    protected static final String localePart = "$locale$";
    protected static final String docNamePart = "$docName$";

    boolean loading;
    float loadProgress;
    @Nullable
    Throwable error;
    Table mainLayout, listView, messageView;

    @Nullable
    Collapser coll;

    // MsgEntry current;

    // protected Seq<MsgEntry> messages = new Seq<>();
    // protected ObjectMap<String, Markdown> documents = new ObjectMap<>();

    public PublicInfoDialog() {
        super(Core.bundle.get("misc.publicInfo"));

        addCloseButton();

        mainLayout = cont.table().grow().get();
        cont.row();
        cont.image().color(Pal.accent).growX().height(4).colspan(2).pad(0).margin(0);

       /* rebuild();

        shown(() -> {
            rebuild();
            refresh();
        });
        resized(this::rebuild);*/
    }

  /*protected void refresh(){
    error = null;
    loading = true;
    current = null;
    messages.clear();

    setupInfos();

    Http.get(directory, res -> {
      if (!loading) return;

      messages.clear();
      String directory = getToString(res.getResultAsStream(), res.getContentLength());
      for (Jval jval : Jval.read(directory).get("pages").asArray()) {
        messages.add(new MsgEntry(jval));
      }

      messages.sort();

      Core.app.post(() -> {
        loading = false;
        if (current == null) current = messages.first();
        setupInfos();
      });
    }, this::handleError);
  }

  protected void handleError(Throwable e) {
    error = e;
    loading = false;

    Core.app.post(this::setupError);
  }

  protected void rebuild() {
    coll = null;

    mainLayout.clearChildren();
    if (Core.graphics.isPortrait()){
      mainLayout.table(SglDrawConst.grayUIAlpha, msg -> {
        messageView = msg;
      }).grow().pad(4);
      mainLayout.row();

      coll = new Collapser(t -> t.pane(Styles.smallPane, p -> {
        listView = p;
        listView.top().defaults().top().growX().height(74).padTop(6).padLeft(4).padRight(4);
      }).growX().height(Core.graphics.getHeight()/2f), true).setDuration(0.5f);
      Table tab = new Table(SglDrawConst.grayUIAlpha, ta -> ta.add(coll).growX().fillY());
      mainLayout.addChild(tab);

      mainLayout.button(Icon.up, Styles.clearNonei, 32, () -> {
        coll.setCollapsed(!coll.isCollapsed(), true);
      }).growX().height(40).update(i -> {
        i.getStyle().imageUp = coll.isCollapsed() ? Icon.upOpen : Icon.downOpen;
        tab.setSize(tab.parent.getWidth(), tab.getPrefHeight());
        tab.setPosition(i.x, i.y + i.getPrefHeight() + 4, Align.bottomLeft);
      });
    }
    else {
      mainLayout.table(SglDrawConst.grayUIAlpha).growY().width(420).padLeft(40).get().top().pane(list -> {
        listView = list;
        listView.defaults().growX().height(74).padLeft(6).padRight(6).padBottom(6);
      }).growX().fillY().top();
      mainLayout.image().padLeft(5).padRight(5).color(Color.lightGray).width(3).growY();
      mainLayout.table(SglDrawConst.grayUIAlpha, msg -> {
        messageView = msg;
      }).grow().padRight(40);
    }

    setupInfos();
  }

  protected void setupInfos() {
    if (error != null) setupError();

    if (loading){
      loadProgress = 0;

      messageView.clearChildren();
      messageView.table(ld -> {
        ld.center().defaults().center();
        ld.add(new Element(){
          @Override
          public void draw() {
            super.draw();
            Draw.color(Pal.accent);
            Draw.alpha(Draw.getColor().a * parentAlpha);
            Fill.square(x + width/2, y + height/2, width/2/Mathf.sqrt2, Time.time);
            Fill.square(x + width/2, y + height/2, width/2/Mathf.sqrt2, 45 + 2*Time.time);
          }
        }).size(80);
        ld.row();
        ld.add("").update(l -> l.setText(Core.bundle.get("misc.loading") + loadingPoints()));
        ld.row();
        ld.add("").update(l -> l.setText(Mathf.round(loadProgress*100) + "%"));
      }).grow();
    }
    else if (current != null){
      messageView.clearChildren();

     *//* Markdown md = documents.get(current.docName);
      if (md == null){
        loading = true;
        setupInfos();

        MsgEntry curr = current;
        Http.get(current.getDocURL(Core.bundle.getLocale()), response -> {
          if (!loading) return;

          String doc = getToString(response.getResultAsStream(), response.getContentLength());

          Core.app.post(() -> {
            //documents.put(current.docName, new Markdown(doc, MarkdownStyles.defaultMD));
            if (current.equals(curr)){
              loading = false;
              setupInfos();
            }
          });
        }, this::handleError);
      }
      else messageView.table().grow().pad(9).top().get().pane(Styles.smallPane, md).grow().top().scrollX(false).get().setFillParent(true);
    }*//*

     *//*listView.clearChildren();
    MsgTag c = null;
    for (MsgEntry entry : messages) {
      if (c != entry.tag){
        c = entry.tag;
        listView.add(c.localized()).set(Cell.defaults()).color(Pal.accent).fillY().left().padLeft(6);
        listView.row();
        listView.image().color(Pal.accent).growX().height(3).padTop(4).padBottom(4);
        listView.row();
      }

      listView.add(buildEntryButton(entry));
      listView.row();
    }*//*
  }

  protected void setupError() {
    messageView.clear();
    messageView.table(t -> {
      t.center().defaults().center();
      t.add(Core.bundle.get("dialog.publicInfo.getFailed"));
      t.row();

      Collapser col = new Collapser(s -> s.pane(Styles.smallPane, e -> e.add(Strings.getStackTrace(error))).fill(), true);

      t.table(bu -> {
        bu.defaults().size(210, 60);
        bu.button(Core.bundle.get("misc.refresh"), Styles.flatt, this::refresh);
        bu.button(Core.bundle.get("misc.unfold"), Styles.flatt, col::toggle).update(b -> b.setText(Core.bundle.get(col.isCollapsed()? "misc.unfold": "misc.fold")));
      }).pad(6);
      t.row();
      t.add(col).growX().fill();
    }).grow();
  }

  protected String getToString(InputStream in, long len) throws IOException {
    loadProgress = 0;
    InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);

    StringWriter writer = new StringWriter();

    long count = 0;
    for (int i = reader.read(); i != -1; i = reader.read()) {
      count++;
      writer.write(i);
      loadProgress = (float) count/len;
    }

    return writer.toString();
  }

  protected Button buildEntryButton(MsgEntry entry){
    return new Button(SglStyles.underline){
      {
        left().defaults().left();

        table(img -> {
          img.image().growY().padTop(-12).width(40f).color(entry.color);
          img.row();
          img.image().height(6).width(40f).color(entry.color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
        }).growY().fillX().padLeft(-12).padBottom(-12);

        table(t -> {
          t.defaults().left().growX();
          t.add(entry.getTitle(Core.bundle.getLocale())).color(Pal.accent);
          t.row();
          t.add(entry.date == null? "no date":
              DateFormat.getDateInstance(DateFormat.DEFAULT, Core.bundle.getLocale()).format(entry.date)).color(Pal.gray);
        }).grow().padLeft(5);

        Element drawer = entry.tag.getDrawer();
        if (drawer != null) add(drawer).size(48).get().addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(Core.bundle.get("infos.msgTag." + entry.tag.name()))));

        update(() -> setChecked(entry.equals(current)));

        clicked(() -> {
          if (entry.equals(current)) return;

          if (coll != null && !coll.isCollapsed()) coll.toggle();

          current = entry;
          setupInfos();
        });
      }

      @Override
      protected void drawBackground(float x, float y) {
        if (entry.equals(current)){
          Draw.color(Color.darkGray);
          Draw.alpha(Draw.getColor().a*parentAlpha);
          Fill.rect(x + width/2, y + height/2, width, height);
        }
        else super.drawBackground(x, y);
      }
    };
  }

  @SuppressWarnings("StringRepeatCanBeUsed")
  private String loadingPoints() {
    StringBuilder res = new StringBuilder(".");

    int points = (int) (Time.time%60/20);
    //I want to use String.repeat, but...
    //WHO WILL USE JAVA 8 TO PLAY GAME??? YEAH! IS EXIST! FU*K!
    for (int i = 0; i < points; i++) {
      res.append(".");
    }

    return res.toString();
  }

  protected enum MsgTag{
    errorWarn(){
      @Override
      public Element getDrawer() {
        Image img = new Image(Icon.warning).setScaling(Scaling.fit);
        return img.update(() -> img.setColor(Tmp.c1.set(Color.crimson).lerp(Pal.accent, Mathf.absin(6, 1))));
      }
    },
    updateLog(Color.sky),
    note(Pal.accent),
    normal(){
      @Override
      public Element getDrawer() {
        return null;
      }
    };

    Color color = Pal.accent;

    MsgTag(){};

    MsgTag(Color color){
      this.color = color;
    }

    @Nullable
    public Element getDrawer() {
      return new Element(){
        @Override
        public void draw() {
          super.draw();
          Draw.color(Tmp.c1.set(MsgTag.this.color).lerp(Color.black, 0.3f));
          Draw.alpha(parentAlpha);
          Fill.square(x + width/2, y + height/2 - Scl.scl(8), width/6, 45);
          Draw.color(MsgTag.this.color);
          Draw.alpha(parentAlpha);
          Fill.square(x + width/2, y + height/2, width/6, 45);
        }
      };
    }

    public String localized() {
      return Core.bundle.get("infos.title." + name());
    }
  }

  protected static class MsgEntry implements Comparable<MsgEntry>{
    public String docName;
    public Date date;
    public Color color;
    public String[] languages;
    public String[] titles;
    public MsgTag tag = MsgTag.normal;

    public MsgEntry(Jval entry){
      docName = entry.getString("docName");

      try {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        date = format.parse(entry.getString("date"));
      } catch (ParseException e) {
        date = null;
      }

      color = Color.valueOf(entry.getString("color", "#ffd37f"));

      Jval.JsonArray lgs = entry.get("languages").asArray();
      languages = new String[lgs.size];
      for (int i = 0; i < lgs.size; i++) {
        languages[i] = lgs.get(i).asString();
      }

      Jval.JsonArray tits = entry.get("title").asArray();
      titles = new String[tits.size];
      for (int i = 0; i < tits.size; i++) {
        titles[i] = tits.get(i).asString();
      }

      if (entry.has("tag")){
        tag = MsgTag.valueOf(entry.getString("tag"));
      }
    }

    public String getDocURL(Locale locale){
      String loc = locale.toString();
      loc = loc.isEmpty()? "en_US": loc;

      for (String language : languages) {
        if (language.equals(loc)) return langDir.replace(localePart, loc).replace(docNamePart, docName);
      }

      return langDir.replace(localePart, "zh_CN").replace(docNamePart, docName);
    }

    public String getTitle(Locale locale){
      String loc = locale.toString();
      loc = loc.isEmpty()? "en_US": loc;

      for (int i = 0; i < languages.length; i++) {
        if (languages[i].equals(loc)) return titles[i];
      }

      return getTitle(Locale.SIMPLIFIED_CHINESE);
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) return true;
      if (object == null || getClass() != object.getClass()) return false;
      MsgEntry msgEntry = (MsgEntry) object;
      return Objects.equals(docName, msgEntry.docName);
    }

    @Override
    public int hashCode() {
      return Objects.hash(docName);
    }

    @Override
    public int compareTo(MsgEntry o) {
      return o.tag == tag && date != null && o.date != null? -date.compareTo(o.date): tag.compareTo(o.tag);
    }
  }*/
}
