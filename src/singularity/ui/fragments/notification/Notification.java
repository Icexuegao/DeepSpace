package singularity.ui.fragments.notification;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import singularity.Sgl;
import singularity.game.researchs.Inspire;
import singularity.game.researchs.ResearchProject;
import singularity.graphic.SglDrawConst;
import singularity.ui.UIUtils;
import universecore.util.DataPackable;

import java.util.Date;

public abstract class Notification implements DataPackable {
  public final Date date = new Date();

  public String title;
  public String information;

  public boolean buildWindow, activeWindow;
  public float duration = 5f;

  public boolean readed;

  public abstract Drawable getIcon();
  public abstract void activity();
  public abstract void buildWindow(Table table);
  public abstract Color getIconColor();
  public abstract Color getTitleColor();
  public abstract Color getInformationColor();

  public Notification(String title, String information){
    this.title = title;
    this.information = information;
  }

  public Notification activeWindow(){
    this.activeWindow = true;
    return this;
  }

  public Notification duration(float duration) {
    this.duration = duration;
    return this;
  }

  @Override
  public void write(Writes write) {
    write.l(date.getTime());
    write.bool(readed);
    write.str(title);
    write.str(information);
  }

  public void read(Reads read) {
    date.setTime(read.l());
    readed = read.bool();
    title = read.str();
    information = read.str();
  }

  public static class Note extends Notification{
    public static final long typeID = 6373849572987459234L;

    public static void assign(){ DataPackable.assignType(typeID, args -> new Note()); }

    //Internal usage
    Note() {super("", "");}

    public Note(String title, String information) {
      super(title, information);
    }

    @Override public Drawable getIcon() { return SglDrawConst.techPoint; }
    @Override public void activity() {}
    @Override public void buildWindow(Table table) {}
    @Override public Color getIconColor() { return SglDrawConst.matrixNet; }
    @Override public Color getTitleColor() { return Pal.accent; }
    @Override public Color getInformationColor() { return Color.white; }

    @Override
    public long typeID() { return typeID; }
  }

  public static class Warning extends Note{
    public static final long typeID = 7824385902876518494L;

    public static void assign(){ DataPackable.assignType(typeID, args -> new Warning()); }

    {
      duration = -1;
    }

    //Internal usage
    Warning() {super("", "");}

    public Warning(String title, String information) {
      super(title, information);
    }

    @Override public Drawable getIcon() { return Icon.warning; }
    @Override public Color getIconColor() { return Tmp.c1.set(Color.crimson).lerp(Color.white, Mathf.absin(10, 1)); }
    @Override public Color getTitleColor() { return SglDrawConst.fexCrystal; }

    @Override public long typeID() { return typeID; }
  }

  public static class ResearchCompleted extends Notification{
    public static final long typeID = 3467196789236210395L;

    public static void assign(){ DataPackable.assignType(typeID, args -> new ResearchCompleted()); }

    {
      buildWindow = true;
      activeWindow = true;
    }

    public ResearchProject project;

    //Internal usage
    ResearchCompleted() {super("", "");}

    public ResearchCompleted(String title, String information, ResearchProject project) {
      super(title, information);
      this.project = project;
    }

    @Override public Drawable getIcon() { return SglDrawConst.techPoint; }
    @Override public void activity() {}
    @Override public void buildWindow(Table table) { UIUtils.buildResearchComplete(table, project); }
    @Override public Color getIconColor() { return SglDrawConst.matrixNet; }
    @Override public Color getTitleColor() { return Pal.accent; }
    @Override public Color getInformationColor() { return Color.white; }

    @Override public long typeID() { return typeID; }

    @Override
    public void write(Writes write) {
      super.write(write);
      write.str(project.group.onPlanet.name);
      write.str(project.name);
    }

    @Override
    public void read(Reads read) {
      super.read(read);
      project = Sgl.researches.getGroup(Vars.content.planet(read.str())).getResearch(read.str());
    }
  }

  public static class Inspired extends ResearchCompleted{
    public static final long typeID = 8275982475983298723L;

    public static void assign(){ DataPackable.assignType(typeID, args -> new Inspired()); }

    public Inspire inspire;

    //Internal usage
    Inspired() {super();}

    public Inspired(String title, String information, Inspire inspire, ResearchProject project) {
      super(title, information, project);
      this.inspire = inspire;
    }

    @Override public Drawable getIcon() { return SglDrawConst.inspire; }
    @Override public void buildWindow(Table table) { UIUtils.buildResearchInspired(table, inspire, project); }
    @Override public Color getTitleColor() { return SglDrawConst.matrixNet; }

    @Override public long typeID() { return typeID; }

    @Override
    public void read(Reads read) {
      super.read(read);
      inspire = project.inspire;
    }
  }
}
