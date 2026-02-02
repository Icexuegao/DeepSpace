package singularity.world;

import arc.func.Boolf2;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

import java.util.Arrays;

public class SglUnitSorts {
  private static float temp;

  public static Units.Sortf denser = (e, x, y) -> {
    temp = e.maxHealth;
    e.team.data().unitTree.intersect(e.x - 64, e.y - 64, 128, 128, u -> {
      temp += u.maxHealth/u.dst(e);
    });

    return -temp;
  };

  private static int count;
  private static final float[][] costs = new float[16][];
  private static final float[][] cpriority = new float[16][];
  private static final Unit[][] result = new Unit[16][];

  static {
    for (int i = 0; i < 16; i++) {
      costs[i] = new float[i + 1];
      cpriority[i] = new float[i + 1];
      result[i] = new Unit[i + 1];
    }
  }

  public static Unit[] closetEnemies(int targets, Teamc team, float range){
    return closetEnemies(targets, team, team.x(), team.y(), range);
  }

  public static Unit[] closetEnemies(int targets, Teamc team, float x, float y, float range){
    return findEnemies(targets, team.team(), x, y, range, null, UnitSorts.closest, true);
  }

  public static Unit[] closetEnemies(int targets, Team team, float x, float y, float range){
    return findEnemies(targets, team, x, y, range, null, UnitSorts.closest, true);
  }

  public static Unit[] findEnemies(int targets, Teamc team, float range, Units.Sortf sortf){
    return findEnemies(targets, team.team(), team.x(), team.y(), range, null, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Teamc team, float range, Boolf2<Unit[], Unit> filter, Units.Sortf sortf){
    return findEnemies(targets, team.team(), team.x(), team.y(), range, filter, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Teamc team, float x, float y, float range, Boolf2<Unit[], Unit> filter, Units.Sortf sortf){
    return findEnemies(targets, team.team(), x, y, range, filter, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Teamc team, float x, float y, float range, Units.Sortf sortf){
    return findEnemies(targets, team.team(), x, y, range, null, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Team team, float x, float y, float range, Units.Sortf sortf){
    return findEnemies(targets, team, x, y, range, null, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Team team, float x, float y, float range, Boolf2<Unit[], Unit> filter, Units.Sortf sortf){
    return findEnemies(targets, team, x, y, range, filter, sortf, true);
  }

  public static Unit[] findEnemies(int targets, Team team, float x, float y, float range, Boolf2<Unit[], Unit> filter, Units.Sortf sortf, boolean ignoredNull){
    if (targets <= 0)
      throw new IllegalArgumentException("targets must bigger than 0");

    if (targets > 16)
      throw new IllegalArgumentException("targets maximum 16");

    count = 0;

    Unit[] res = result[targets - 1];
    float[] cost = costs[targets - 1];
    float[] priority = cpriority[targets - 1];

    Arrays.fill(res, null);
    Arrays.fill(priority, Float.MIN_VALUE);
    Arrays.fill(cost, Float.MAX_VALUE);

    Units.nearbyEnemies(team, x, y, range, u -> {
      if (filter != null && !filter.get(res, u)) return;

      for (int i = 0; i < targets; i++) {
        if (res[i] == null){
          count++;

          res[i] = u;
          cost[i] = sortf.cost(u, x, y);
          priority[i] = u.type.targetPriority;

          break;
        }
        else{
          float c = sortf.cost(u, x, y);
          float pri = u.type.targetPriority;

          if (pri > priority[i] && c < cost[i]){
            System.arraycopy(res, i, res, i + 1, res.length - i - 1);
            System.arraycopy(cost, i, cost, i + 1, cost.length - i - 1);
            System.arraycopy(priority, i, priority, i + 1, priority.length - i - 1);

            res[i] = u;
            cost[i] = c;
            priority[i] = pri;

            break;
          }
        }
      }
    });

    if (!ignoredNull && count < targets){
      Unit[] tmp = result[count - 1];

      System.arraycopy(res, 0, tmp, 0, count);

      return tmp;
    }

    return res;
  }
}
