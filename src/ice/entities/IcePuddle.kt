package ice.entities

import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pools
import mindustry.game.Team
import mindustry.gen.Puddle

class IcePuddle : Puddle() {
  companion object {
    fun create(): IcePuddle {
      return Pools.obtain(IcePuddle::class.java, ::IcePuddle)
    }
  }

  var team: Team = Team.sharded
  override fun read(read: Reads?) {
    super.read(read)
    this.team = mindustry.io.TypeIO.readTeam(read)
  }

  override fun write(write: Writes?) {
    super.write(write)
    mindustry.io.TypeIO.writeTeam(write, team)
  }
}