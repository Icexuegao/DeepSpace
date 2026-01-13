package universecore.world.particles;

import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.entities.EntityGroup;
import mindustry.gen.Bullet;
import mindustry.gen.Decal;
import mindustry.gen.Groups;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Particle extends Decal implements Iterable<Particle.Cloud> {

    public Vec2 dest = new Vec2();
    public float eff = 0f;

    public Interval timer = new Interval(6);
    public Bullet owner;
    public Bullet bullet;

    public float strength = 1;
    public float deflectAngle = 45;

    private static int counter = 0;
    /**
     * 粒子的最大共存数量，总量大于此数目时，创建新的粒子会清除最先产生的粒子
     */
    public static int maxAmount = 1024;

    protected static final ObjectSet<Particle> all = new ObjectSet<>();
    protected static final Seq<Particle> temp = new Seq<>();

    protected Vec2 startPos = new Vec2();
    protected float clipSize;

    Cloud currentCloud, firstCloud;
    int cloudCount;

    public int maxCloudCounts = -1;

    public Particle parent;

    /**
     * 粒子的速度，矢量
     */
    public Vec2 speed = new Vec2();
    /**
     * 粒子当前的尺寸
     */
    public float size;

    public float defSpeed;
    public float defSize;

    /**
     * 粒子模型，决定了该粒子的行为
     */
    public ParticleModel model;
    public float layer;

    public static int count() {
        return all.size;
    }

    public float cloudCount() {
        return cloudCount;
    }

    public static Seq<Particle> get(Boolf<Particle> filter) {
        temp.clear();
        for (Particle particle : all) {
            if (filter.get(particle)) temp.add(particle);
        }
        return temp;
    }

    @Override
    public void add() {
        index__all = Groups.all.addIndex(this);
        index__draw = Groups.draw.addIndex(this);

        all.add(this);

        counter++;

        currentCloud = Pools.get(Cloud.class, Cloud::new, 65536).obtain();
        currentCloud.x = x;
        currentCloud.y = y;
        currentCloud.size = 0;
        currentCloud.color.set(model.trailColor(this));

        firstCloud = currentCloud;

        added = true;

        model.init(this);

        if (counter >= maxAmount) {
            remove();
        }
    }

    @Override
    public void draw() {
        float l = Draw.z();
        Draw.z(layer);

        if (parent != null) {
            x += parent.x;
            y += parent.y;
        }

        model.draw(this);

        if (currentCloud != null) {
            model.drawTrail(this);
        }

        if (parent != null) {
            x -= parent.x;
            y -= parent.y;
        }

        Draw.z(l);
        Draw.reset();
    }

    @Override
    public void update() {
        model.deflect(this);
        x += speed.x * Time.delta;
        y += speed.y * Time.delta;
        size = model.currSize(this);
        model.update(this);
        Cloud c = Pools.get(Cloud.class, Cloud::new, 65536).obtain();
        c.x = parent == null ? x : x + parent.x;
        c.y = parent == null ? y : y + parent.y;
        c.size = size;
        c.color.set(model.trailColor(this));

        c.perCloud = currentCloud;
        currentCloud.nextCloud = c;

        currentCloud = c;

        cloudCount++;

        for (Cloud cloud : currentCloud) {
            model.updateTrail(this, cloud);
        }

        boolean mark = false;
        while (firstCloud.nextCloud != null) {
            if (maxCloudCounts > 0 && cloudCount > maxCloudCounts || model.isFaded(this, firstCloud)) {
                mark = !(maxCloudCounts > 0 && cloudCount > maxCloudCounts);
                popFirst();
            } else break;
        }

        if (!mark && (parent != null && !parent.isAdded() || model.isFinal(this))) {
            popFirst();
            if (cloudCount > 4) popFirst();
        }

        if (cloudCount <= 4 && model.isFinal(this)) remove();
    }

    private void popFirst() {
        Cloud n = firstCloud.nextCloud;
        n.perCloud = null;
        Pools.free(firstCloud);
        firstCloud = n;
        cloudCount--;
    }

    @Override
    public void remove() {
        if (added) {
            Groups.all.removeIndex(this, this.index__all);
            index__all = -1;
            Groups.draw.removeIndex(this, this.index__draw);
            index__draw = -1;
            Groups.queueFree(this);

            all.remove(this);
            counter--;
            added = false;
        }
    }

    @Override
    public int classId() {
        return 102;
    }

    @Override
    public float clipSize() {
        return clipSize = Math.max(Tmp.v1.set(x, y).sub(startPos).len(), clipSize);
    }

    @Override
    public void reset() {
        added = false;
        parent = null;
        id = EntityGroup.nextId();
        lifetime = 0;
        region = null;
        rotation = 0;
        time = 0;
        x = 0;
        y = 0;

        maxCloudCounts = -1;

        speed.setZero();
        startPos.setZero();

        layer = 0;
        clipSize = 0;

        while (firstCloud.nextCloud != null) {
            popFirst();
        }
        Pools.free(firstCloud);

        currentCloud = null;
        firstCloud = null;

        cloudCount = 0;
        size = 0;
        //extra().clear();

        model = null;

        color.set(Color.white);
    }

    @NotNull
    @Override
    public Iterator<Cloud> iterator() {
        return currentCloud.iterator();
    }

    public static class Cloud implements Pool.Poolable, Iterable<Cloud> {
        public final Color color = new Color();

        public float x, y, size;
        public Cloud perCloud, nextCloud;

        Itr itr = new Itr();

        public void draw() {
            draw(1, 1);
        }

        public void draw(float modulate, float modulateNext) {
            Draw.color(color);

            if (perCloud != null && nextCloud != null) {
                float angle = Angles.angle(x - perCloud.x, y - perCloud.y);
                float dx1 = Angles.trnsx(angle + 90, size * modulate);
                float dy1 = Angles.trnsy(angle + 90, size * modulate);
                angle = Angles.angle(nextCloud.x - x, nextCloud.y - y);
                float dx2 = Angles.trnsx(angle + 90, nextCloud.size * modulateNext);
                float dy2 = Angles.trnsy(angle + 90, nextCloud.size * modulateNext);

                Fill.quad(x + dx1, y + dy1, x - dx1, y - dy1, nextCloud.x - dx2, nextCloud.y - dy2, nextCloud.x + dx2, nextCloud.y + dy2);
            } else if (perCloud == null && nextCloud != null) {
                float angle = Angles.angle(nextCloud.x - x, nextCloud.y - y);
                float dx2 = Angles.trnsx(angle + 90, nextCloud.size * modulate);
                float dy2 = Angles.trnsy(angle + 90, nextCloud.size * modulate);

                Fill.quad(x, y, x, y, nextCloud.x - dx2, nextCloud.y - dy2, nextCloud.x + dx2, nextCloud.y + dy2);
            }
        }

        @Override
        public void reset() {
            x = 0;
            y = 0;
            size = 0;
            color.set(Color.clear);

            perCloud = null;
            nextCloud = null;
        }

        @NotNull
        @SuppressWarnings("ReturnOfInnerClass")
        @Override
        public Iterator<Cloud> iterator() {
            itr.reset();
            return itr;
        }

        class Itr implements Iterator<Cloud> {
            Cloud curr = Cloud.this;

            public void reset() {
                curr = Cloud.this;
            }

            @Override
            public boolean hasNext() {
                return curr.perCloud != null;
            }

            @Override
            public Cloud next() {
                return curr = curr.perCloud;
            }
        }
    }
}

