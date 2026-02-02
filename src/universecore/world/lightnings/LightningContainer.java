package universecore.world.lightnings;

import arc.func.Cons;
import arc.func.Cons2;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import universecore.world.lightnings.generator.LightningGenerator;

import java.util.Iterator;

/**
 * 闪电容器，使用一个闪电生成器产生闪电，由容器进行处理和绘制，通常用于一类闪电用同一个容器存储
 * @author EBwilson
 * @since 1.5
 *  移动到图形模块中
 */

public class LightningContainer implements Iterable<Lightning> {
    /**
     * 闪电从产生到完全出现需要的时间，这会平摊给每一段闪电，fps为当前帧率
     * 但如果这个值为0,那么闪电会立即出现
     */
    public float time = 0;
    /**
     * 闪电的扩散速度，小于或等于0时默认使用time提供的路径扩散计算方式，否则使用给出的速度来处理闪电的扩散（单位：/tick）
     * @deprecated 规范化，此API不再可用
     */
    @Deprecated
    public float speed = 0;
    /** 闪电的存在时间 */
    public float lifeTime = 30;
    /** 闪电消逝的过渡时间，若不设置则消失过度时间等于闪电的存在时间 */
    public float fadeTime = -1;
    /** 闪电整体的宽度是否随闪电的持续时间淡出 */
    public boolean fade = true;
    /** 闪电是否随淡出过程从起点开始消失 */
    public boolean backFade = false;
    /** 闪电每一段宽度的随机区间 */
    public float minWidth = 2.5f, maxWidth = 4.5f;
    /** 闪电的衰减变换器，传入的数值为闪电的存在时间进度 */
    public Interp lerp = Interp.pow2Out;

    /** 闪电分支创建时调用的回调函数，一般用于定义闪电的分支子容器属性 */
    public Cons<Lightning> branchCreated;

    /** 闪电顶点触发器，当一个闪电节点已到达后触发，传入前一个顶点和这一个顶点 */
    public Cons2<LightningVertex, LightningVertex> trigger;
    public boolean headClose, endClose;

    protected float clipSize;

    protected final Seq<Lightning> lightnings = new Seq<>();

    /** 使用给出的闪电生成器在容器中创建一道新的闪电 */
    public void create(LightningGenerator generator) {
        generator.branched(branchCreated);
        Lightning lightning = Lightning.create(generator, Mathf.random(minWidth, maxWidth), lifeTime, fadeTime > 0 ? fadeTime : lifeTime, lerp, time, fade, backFade, trigger);
        lightning.headClose = headClose;
        lightning.endClose = endClose;
        lightnings.add(lightning);
    }

    @Override
    public Iterator<Lightning> iterator() {
        return lightnings.iterator();
    }

    /** 更新一次当前容器中所有子闪电的状态 */
    public void update() {
        Iterator<Lightning> itr = lightnings.iterator();
        while (itr.hasNext()) {
            Lightning lightning = itr.next();
            clipSize = Math.max(clipSize, lightning.clipSize);

            float progress = (Time.time - lightning.startTime) / lifeTime;
            if (progress > 1) {
                itr.remove();
                Pools.free(lightning);
                clipSize = 0;
                continue;
            }

            lightning.update();
        }
    }

    /**
     * 绘制容器，这会将容器中保存的所有闪电进行绘制
     * @param x 绘制闪电的原点x坐标
     * @param y 绘制闪电的原点y坐标
     */
    public void draw(float x, float y) {
        for (Lightning lightning : lightnings) {
            lightning.draw(x, y);
        }
    }

    public float clipSize() {
        return clipSize;
    }

    /** 闪电分支容器，用于绘制分支闪电，会递归绘制所有的子分支 */
    public static class PoolLightningContainer extends LightningContainer implements Pool.Poolable {
        public static PoolLightningContainer create(float lifeTime, float minWidth, float maxWidth) {
            PoolLightningContainer result = Pools.obtain(PoolLightningContainer.class, PoolLightningContainer::new);
            result.lifeTime = lifeTime;
            result.minWidth = minWidth;
            result.maxWidth = maxWidth;

            return result;
        }

        @Override
        public void reset() {
            time = 0;
            lifeTime = 0;
            clipSize = 0;
            maxWidth = 0;
            minWidth = 0;
            lerp = f -> 1 - f;
            branchCreated = null;
            trigger = null;

            for (Lightning lightning : lightnings) {
                Pools.free(lightning);
            }
            lightnings.clear();
        }
    }
}
