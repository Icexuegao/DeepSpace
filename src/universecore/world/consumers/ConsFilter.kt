package universecore.world.consumers

import arc.func.Prov
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import mindustry.ctype.Content
import universecore.components.blockcomp.ConsumerBuildComp

class ConsFilter {
    var optionalFilter: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>> = ObjectMap<ConsumeType<*>?, ObjectSet<Content?>>()
    var allFilter: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>> = ObjectMap<ConsumeType<*>?, ObjectSet<Content?>>()

    fun applyFilter(consumers: Iterable<BaseConsumers>?, optional: Iterable<BaseConsumers>?) {
        if (optional != null) {
            for (cons in optional) {
                handle(cons, optionalFilter)
                handle(cons, allFilter)
            }
        }

        if (consumers != null) {
            for (cons in consumers) {
                handle(cons, allFilter)
                for (access in cons.selfAccess) {
                    allFilter.get(access.key, Prov { ObjectSet() }).addAll()
                }
            }
        }
    }

    /**过滤器，将判断对当前选中的区域指定type下对输入的对象是否接受
     * 若可选过滤器已添加目标对象同样返回true
     *
     * @param type 过滤器种类
     * @param target 通过过滤器的目标对象
     * @param acceptAll 是否接受所有清单的需求
     * @return 布尔值，是否接受此对象
     */
    fun filter(entity: ConsumerBuildComp, type: ConsumeType<*>?, target: Content?, acceptAll: Boolean): Boolean {
        if (optionalFilter.containsKey(type) && optionalFilter.get(type).contains(target)) return true

        if (acceptAll) return allFilter.containsKey(type) && allFilter.get(type).contains(target)

        return entity.consumer.current != null && entity.consumer.current!!.filter(type, target)
    }

    private fun handle(cons: BaseConsumers, map: ObjectMap<ConsumeType<*>?, ObjectSet<Content?>>) {
        for (c in cons.all()) {
            if ((c.filter()) != null) {
                val all = allFilter.get(c.type(), Prov { ObjectSet() })
                val set = map.get(c.type(), Prov { ObjectSet() })
                for (o in c.filter()!!) {
                    set.add(o)
                    all.add(o)
                }
            }
        }
    }
}