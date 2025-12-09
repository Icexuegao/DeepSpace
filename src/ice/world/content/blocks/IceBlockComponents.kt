package ice.world.content.blocks

import arc.util.Interval
import mindustry.gen.Building

object IceBlockComponents {
    fun calwavetimeremain(progress: Float, progressIncrease: Float): String {
        if (progressIncrease == 0f || 1 / progressIncrease < 240f) {
            return ""
        }
        val time = (1 - progress) / progressIncrease
        var wavetimeremain = " [orange]~"
        val m = time.toInt() / 60
        if (m == 0) {
            return ""
        }
        wavetimeremain += m.toString()
        wavetimeremain += "min"
        return wavetimeremain
    }

    fun sharingHealth(b: Building) {
        // 遍历邻近的方块实体，设为otherl
        for (other in b.proximity) {
            // 如果other的享元方块 和 b的享元方块 不一样，则跳过
            if (other.block !== b.block) continue
            // 获得b的生命值
            val thisH = b.health
            // 获取other的生命值
            val otherH = other.health
            // 如果 b的生命值 大于 other的生命值 且
            // b的生命值 大于 1 —— 防止因为生命值传递而死
            // other的生命值 小于 其最大生命值
            for (i in 1..10) {
                if (thisH > otherH && thisH > 1 && otherH < other.maxHealth) {
                    // 传递生命值
                    other.health += 0.01f
                    b.health -= 0.01f
                }
            }
        }
    }

    fun <T : Building> T.timesex(index: Int, time: Float): Boolean {
        if (index >= timer.times.size) {
            timer(Interval(timer.times.size + 1))
        }
        return timer[index, time]
    }
}