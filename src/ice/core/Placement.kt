package ice.core

import arc.func.Boolf
import arc.math.Mathf
import arc.math.geom.Point2
import arc.struct.Seq
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.input.Placement
import mindustry.world.Block
import mindustry.world.blocks.distribution.DirectionBridge

object Placement {
    val plans1 = Seq<BuildPlan>()
    fun calculateBridges(plans: Seq<BuildPlan>, bridge: TransferNode) {
        calculateBridges(plans, bridge, false, Boolf { t: Block? -> false })
    }
    fun calculateBridges(plans: Seq<BuildPlan>, bridge: TransferNode, hasJunction: Boolean, avoid: Boolf<Block>) {
        if (Placement.isSidePlace(plans) || plans.size == 0) return
        //check for orthogonal placement + unlocked state
        if (!(plans.first().x == plans.peek().x || plans.first().y == plans.peek().y) || !bridge.unlockedNow()) {
            return
        }
        val placeable = Boolf { plan: BuildPlan? ->
            (plan!!.placeable(Vars.player.team()) || (plan.tile() != null && plan.tile().block() === plan.block)) &&  //don't count the same block as inaccessible
                    !(plan !== plans.first() && plan.build() != null && plan.build().rotation != plan.rotation && avoid.get(plan.tile().block()))
        }
        val result =plans1.clear()
        val rotated = plans.first().tile() != null && plans.first().tile().absoluteRelativeTo(plans.peek().x, plans.peek().y).toInt() == Mathf.mod(plans.first().rotation + 2, 4)
        var i = 0
        outer@ while (i < plans.size) {
            val cur = plans.get(i)
            result.add(cur)
            //gap found
            if (i < plans.size - 1 && placeable.get(cur) && !placeable.get(plans.get(i + 1))) {
                var wereSame = true
                //find the closest valid position within range
                for (j in i + 1..<plans.size) {
                    val other = plans.get(j)
                    //out of range now, set to current position and keep scanning forward for next occurrence
                    if (!bridge.positionsValid(cur.x, cur.y, other.x, other.y)) {
                        //add 'missed' conveyors
                        for (k in i + 1..<j) {
                            result.add(plans.get(k))
                        }
                        i = j
                        continue@outer
                    } else if (placeable.get(other)) {
                        if (wereSame && hasJunction) {
                            //the gap is fake, it's just conveyors that can be replaced with junctions
                            i++
                            continue@outer
                        } else {
                            //found a link, assign bridges
                            cur.block = bridge
                            other.block = bridge
                            if (rotated) {
                                other.config = Point2(cur.x - other.x, cur.y - other.y)
                            } else {
                                cur.config = Point2(other.x - cur.x, other.y - cur.y)
                            }

                            i = j
                            continue@outer
                        }
                    }

                    if (other.tile() != null && !avoid.get(other.tile().block())) {
                        wereSame = false
                    }
                }
                //if it got here, that means nothing was found. this likely means there's a bunch of stuff at the end; add it and bail out
                for (j in i + 1..<plans.size) {
                    result.add(plans.get(j))
                }
                break
            } else {
                i++
            }
        }
        plans.set(result)
    }

    fun calculateBridges(plans: Seq<BuildPlan>, bridge: DirectionBridge, hasJunction: Boolean, avoid: Boolf<Block?>) {
        if (Placement.isSidePlace(plans) || plans.size == 0) return
        //check for orthogonal placement + unlocked state
        if (!(plans.first().x == plans.peek().x || plans.first().y == plans.peek().y) || !bridge.unlockedNow()) {
            return
        }
        val placeable = Boolf { plan: BuildPlan? ->
            (plan!!.placeable(Vars.player.team()) || (plan.tile() != null && plan.tile().block() === plan.block)) &&  //don't count the same block as inaccessible
                    !(plan !== plans.first() && plan.build() != null && plan.build().rotation != plan.rotation && avoid.get(plan.tile().block()))
        }
        val result =plans1.clear()
        var i = 0
        outer@ while (i < plans.size) {
            val cur = plans.get(i)
            result.add(cur)
            //gap found
            if (i < plans.size - 1 && placeable.get(cur) && !placeable.get(plans.get(i + 1))) {
                var wereSame = true
                //find the closest valid position within range
                for (j in i + 1..<plans.size) {
                    val other = plans.get(j)
                    //out of range now, set to current position and keep scanning forward for next occurrence
                    if (!bridge.positionsValid(cur.x, cur.y, other.x, other.y)) {
                        //add 'missed' conveyors
                        for (k in i + 1..<j) {
                            result.add(plans.get(k))
                        }
                        i = j
                        continue@outer
                    } else if (placeable.get(other)) {
                        if (wereSame && hasJunction) {
                            //the gap is fake, it's just conveyors that can be replaced with junctions
                            i++
                            continue@outer
                        } else {
                            //found a link, assign bridges
                            cur.block = bridge
                            other.block = bridge
                            i = j
                            continue@outer
                        }
                    }

                    if (other.tile() != null && !avoid.get(other.tile().block())) {
                        wereSame = false
                    }
                }
                //if it got here, that means nothing was found. this likely means there's a bunch of stuff at the end; add it and bail out
                for (j in i + 1..<plans.size) {
                    result.add(plans.get(j))
                }
                break
            } else {
                i++
            }
        }
        plans.set(result)
    }
}