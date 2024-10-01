package ice.Alon.async;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.async.AsyncProcess;
import mindustry.async.PhysicsProcess.PhysicRef;
import mindustry.content.UnitTypes;
import mindustry.entities.EntityGroup;
import mindustry.gen.Groups;
import mindustry.gen.Physicsc;
import mindustry.gen.Unit;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.*;

/**
 * 莲莲大蛇的碰撞代码
 */
public class CeProcess implements AsyncProcess {
    private static final Vec2 vec = new Vec2();
    private static final org.jbox2d.common.Vec2 b2vec = new org.jbox2d.common.Vec2();
    private static final org.jbox2d.common.Vec2[] polyVecs;

    static {
        polyVecs = new org.jbox2d.common.Vec2[]{new org.jbox2d.common.Vec2(45f, 0),

                new org.jbox2d.common.Vec2(0, 30f),

                new org.jbox2d.common.Vec2(-40f, 25f),

                new org.jbox2d.common.Vec2(-40f, -25f),

                new org.jbox2d.common.Vec2(0, -30f)};
    }

    private static final int layers = 4, layerAll = 0, layerGround = 0b0001, layerLegs = 0b0010, layerFlying = 0b0100;

    private float scl = 1.25f;
    private CeWorld physics;
    private Seq<CeRef> refs = new Seq<>(false);
    //目前仅对 Units 启用
    private EntityGroup<Unit> group = Groups.unit;

    @Override
    public void begin() {
        if (physics == null) return;
        //boolean local = !Vars.net.client();

        //remove stale entities
        refs.removeAll(ref->{
            if (!ref.entity.isAdded()) {
                physics.remove(ref.b);
                ref.entity.physref(null);
                return true;
            }
            return false;
        });

        //find Units without bodies and assign them
        for (Unit entity : group) {
            if (entity == null || entity.type == null || !entity.type.physics) continue;

            if (entity.physref == null) {
                CircleShape cs = new CircleShape();
                cs.m_radius = entity.hitSize / 2f;
                PolygonShape ps = new PolygonShape();
                ps.set(polyVecs, polyVecs.length);
                /**指定type*/
                Shape s = (entity.type == UnitTypes.omura) ? ps : cs;

                BodyDef bd = new BodyDef();
                bd.type = BodyType.DYNAMIC;
                bd.position.set(entity.x, entity.y);
                bd.angle = entity.rotation * Mathf.degRad;
                //bd.bullet = true;

                FixtureDef fd = new FixtureDef();
                fd.density = 1f;
                fd.shape = s;

                CeRef ref = new CeRef(entity, physics.add(bd, fd));
                refs.add(ref);
                entity.physref = ref;
            }

            //save last position
            CeRef ref = (CeRef) entity.physref;

            for (Fixture fixture = ref.b.getFixtureList(); fixture != null; fixture = fixture.m_next) {
                Filter filter = fixture.m_filter;
                filter.categoryBits = filter.maskBits = entity.type.allowLegStep && entity.type.legPhysicsLayer ? layerLegs : entity.isGrounded() ? layerGround : layerFlying;
            }
            ref.x = entity.x;
            ref.y = entity.y;
            ref.rotation = entity.rotation;
            //ref.body.local = local || entity.isLocal();
        }
    }

    @Override
    public void process() {
        if (physics == null) return;

        //get last position vectors before step
        for (CeRef ref : refs) {
            //force set target position
            ref.b.setTransform(b2vec.set(ref.x, ref.y), ref.rotation * Mathf.degRad);
        }

        physics.update();
    }

    @Override
    public void end() {
        if (physics == null) return;

        //move entities
        for (CeRef ref : refs) {
            Body body = ref.b;
            Unit entity = (Unit) ref.entity;
            org.jbox2d.common.Vec2 p = body.m_xf.p;
            float rot = body.m_sweep.a * Mathf.radDeg;

            //move by delta
            entity.move((p.x - ref.x) * scl, (p.y - ref.y) * scl);
            entity.rotation += rot - ref.rotation;
        }
    }

    @Override
    public void reset() {
        if (physics != null) {
            refs.clear();
            physics = null;
        }
    }

    @Override
    public void init() {
        reset();

        b2vec.setZero();
        physics = new CeWorld(b2vec);
    }

    public static class CeRef extends PhysicRef {
        public Body b;
        public float rotation;
        //public Vec2 vel;

        public CeRef(Physicsc entity, Body body) {
            super(entity, null);
            this.b = body;
        }
    }

    //world 用于在不同的线程中模拟物理
    public static class CeWorld {
        private static final int times = 30;
        private static final float timeStep = 1f / times;
        private static final int iterations = 20;

        private final World boxWorld;
        //private final Seq<CeBody> bodies = new Seq<>(false, 16, CeBody.class);

        private float timer;

        public CeWorld(org.jbox2d.common.Vec2 gravity) {
            boxWorld = new World(gravity);
            boxWorld.setAllowSleep(false);
        }

        public Body add(BodyDef bd, FixtureDef fd) {
            Body body = boxWorld.createBody(bd);
            body.createFixture(fd);
            return body;
        }

        public void remove(Body body) {
            if (body != null) {
                boxWorld.destroyBody(body);
            }
        }

        public void update() {
            boxWorld.step(timeStep, iterations, iterations);
            boxWorld.clearForces();
        }
    }
}
