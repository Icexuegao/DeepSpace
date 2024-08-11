package Ice.asundry;

import Ice.asundry.BaseTool.io.IceJval;
import arc.Events;
import arc.flabel.FLabel;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.ui.dialogs.BaseDialog;

public class Sundry {
    public static void main(String[] args) {
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
                                
                {"name":"dff",
                "type#":"rt"
                }
                """;
        IceJval read = IceJval.read(k);
        String string = read.toString();
        Log.info(string);
//
//        String replace = k.replace("#", "\\#");
//        Log.info(replace);
    }
}
