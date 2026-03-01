package singularity.game.planet;

import arc.func.Cons2;
import arc.func.Func;
import arc.struct.Seq;
import mindustry.game.Team;

import java.util.Iterator;

public interface ChunkContextIncubator extends Iterator<ChunkContext> {
  void begin(Team team);
  Class<? extends ChunkContext> peekType();

  default void forEach(Team team, Cons2<Class<? extends ChunkContext>, ChunkContext> cons) {
    begin(team);

    while (hasNext()) {
      cons.get(peekType(), next());
    }
  }

  @SafeVarargs
  static List list(Func<Team, ChunkContext>... contexts){
    return new List(contexts);
  }

  static List list(Object... contexts){
    return new List(contexts);
  }

  static <T extends ChunkContext> Single<T> single(Class<T> type, Func<Team, T> context){
    return new Single<>(type, context);
  }

  static <T extends ChunkContext> Single<T> single(Func<Team, T> context){
    return new Single<>(context);
  }

  class Single<T extends ChunkContext> implements ChunkContextIncubator {
    private final Func<Team, T> context;
    private final Class<T> type;
    private boolean finished = true;
    private Team team;

    public Single(Class<T> type, Func<Team, T> context) {
      this.context = context;
      this.type = type;
    }

    public Single(Func<Team, T> context) {
      this.context = context;
      this.type = null;
    }

    @Override
    public void begin(Team team) {
      finished = false;
      team = this.team;
    }

    @Override public Class<T> peekType() {return type;}
    @Override public boolean hasNext() {return !finished;}
    @Override public ChunkContext next() {finished = true;return context.get(team);}
  }

  class List implements ChunkContextIncubator {
    private static class Pair{
      Class<? extends ChunkContext> type;
      Func<Team, ChunkContext> prov;

      private Pair(Class<? extends ChunkContext> type, Func<Team, ChunkContext> prov) {
        this.type = type;
        this.prov = prov;
      }
    }

    private final Seq<Pair> list = new Seq<>();
    private int index;
    private Team team;

    @SafeVarargs
    public List(Func<Team, ChunkContext>... contexts) {
      for (Func<Team, ChunkContext> prov : contexts) {
        list.add(new Pair(null, prov));
      }
    }

    @SuppressWarnings("unchecked")
    public List(Object... contexts) {
      for (int i = 0; i < contexts.length; i += 2) {
        list.add(new Pair(
            (Class<? extends ChunkContext>) contexts[i],
            (Func<Team, ChunkContext>) contexts[i + 1]
        ));
      }
    }

    @Override
    public void begin(Team team) {
      index = 0;
      this.team = team;
    }

    @Override
    public Class<? extends ChunkContext> peekType() {
      return list.get(index).type;
    }

    @Override
    public boolean hasNext() {
      return index < list.size;
    }

    @Override
    public ChunkContext next() {
      ChunkContext next = list.get(index).prov.get(team);
      index++;
      return next;
    }
  }
}
