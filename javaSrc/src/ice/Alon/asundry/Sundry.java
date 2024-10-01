package ice.Alon.asundry;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Log;
import ice.Alon.asundry.BaseTool.io.IceJval;
import mindustry.content.UnitTypes;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;

public class Sundry {
    public static void main(String[] args) {

        /*  IceDrawUpdate.drawUpdate.add(new IceDrawUpdate.DrawUpdate() {
            @Override
            public void draw() {
                Groups.unit.each((unit)->{
                    if (unit.type == UnitTypes.omura) {
                        Seq<Vec2> vec2s = new Seq<>();
                        ObjectMap<Float, Float> fl =ObjectMap.of(45f, 0f,0f, 30f,-40f, 25f,-40f, -25f,0f, -30f);
                        Log.info(fl.toString());
                      fl.forEach((a)->vec2s.add(new Vec2(a.key,a.value)));

                        vec2s.each(v->v.rotate(unit.rotation));
                        Log.info(Arrays.toString(vec2s.toArray()));
                        ObjectMap<Vec2, Vec2> entries = of(vec2s.items);
                        entries.put(vec2s.first(),vec2s.get(vec2s.size-1));
                        entries.forEach((v1)->{
                            Vec2 key = v1.key;
                            Vec2 value = v1.value;
                            Lines.line(unit.x + key.x, unit.y + key.y, unit.x + value.x, unit.y + value.y);
                        });

                        //   Lines.line(unit.x + vec2.x, unit.y + vec2.y, unit.x + vec21.x, unit.y + vec21.y);
                       *//* Lines.line(unit.x + 45, unit.y, unit.x, unit.y + 30);
                        Lines.line(unit.x, unit.y + 30, unit.x - 40, unit.y + 25);
                        Lines.line(unit.x - 40, unit.y + 25, unit.x - 40f, unit.y - 25f);
                        Lines.line(unit.x - 40f, unit.y - 25f, unit.x, unit.y - 30f);
                        Lines.line(unit.x, unit.y - 30f, unit.x + 45, unit.y);*//*
                        Draw.z(Layer.weather);
                    }
                });
            }
        });*/
      /*
        });*/
       // arc.util.OS
 /* Events.on(EventType.ClientLoadEvent.class, (e) -> {
            BaseDialog baseDialog = new BaseDialog("121");
            FLabel fLabel = new FLabel("1212222222222222222222222222222222222223333333333333333333333341414");
            fLabel.setDefaultToken("{rainbow}{shake}{wind}{wave}{ease}{Fade}{blink}{sick}{jump}{gradient}");
            baseDialog.cont.add(fLabel);
            baseDialog.row();
            baseDialog.visible(() -> true);
            baseDialog.show();
        });*/
      /*  Vars.content.blocks().each((block -> block.buildVisibility = BuildVisibility.shown));*/
        /*
        BasicBulletType g = new BasicBulletType(2, 45) {
            @Override
            public void update(Bullet b) {
                super.update(b);
            }{
            lifetime = 600;
        }};
        UnitTypes.alpha.weapons.first().bullet = g;*/

     /*   Events.on(EventType.ClientLoadEvent.class, (e) -> {
            IceBlockInventoryFragment textAT = new IceBlockInventoryFragment();
            Class<InputHandler> aClass = InputHandler.class;
            InputHandler input = Vars.control.input;
            Timer.schedule(()->{
                if (!Vars.mobile)
                {
                    try {
                        Log.info("name"+input.getClass().getName());
                        Field inv = aClass.getDeclaredField("inv");
                        Log.info("运行1");
                        inv.setAccessible(true);
                        Log.info("运行2");
                        try {
                            inv.set(input, textAT);
                            Log.info("运行3");
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (NoSuchFieldException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            },0,0.1f,-1);
        });*/
        /*BasicBulletType f = new BasicBulletType() {
            TextureRegion g = Core.atlas.find("ice-b3.png");
            @Override
            public void draw(Bullet b) {
                Draw.rect(g, b.x, b.y);
                super.draw(b);
            }
            public int i = 0;
            @Override
            public void update(Bullet b) {
                if (i == 120) i = 0;
                i++;
                float sin = Mathf.sin(i);
                b.y += sin / 60;
                b.x += i / 60;
            }
        };
        UnitTypes.alpha.weapons.first().bullet = f;
        UnitTypes.alpha.weapons.first().bullet.lifetime += 600;*/
        String k = """
                               
                
                """;
        IceJval read = IceJval.read(k);
        String string = read.toString();
        Log.info(string);
//
//        String replace = k.replace("#", "\\#");
//        Log.info(replace);
    }
}
