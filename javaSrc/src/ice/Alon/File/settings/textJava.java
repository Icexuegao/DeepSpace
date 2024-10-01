package ice.Alon.File.settings;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;
import arc.util.serialization.JsonWriter;
import ice.Ice;
import rhino.json.JsonParser;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import static arc.util.serialization.JsonValue.ValueType.object;

public class textJava {
    public static class JsVa {
        public IdentityHashMap<String, Boolean> map = new IdentityHashMap<>();
        public int s2;
        public int s3;

        public JsVa() {
            s2 = 1;
            s3 = 1;
        }
    }

    public static void load() throws IllegalAccessException, IOException {


         Json json =new Json(JsonWriter.OutputType.json);
        JsVa jsVa1 = new JsVa();
        jsVa1.s2=2;
        String json1 = json.toJson(jsVa1);
        Log.info(json1);
       JsonReader jsonReader=new JsonReader();
        JsonValue parse = jsonReader.parse(json1);

        /*  JsVa jsVa = new JsVa();
        JsonReader jsonReader = new JsonReader();
        JsonValue parse = jsonReader.parse(new Fi(Core.files.getLocalStoragePath() + "\\iceConfig.json"));

        Field[] fields = JsVa.class.getFields();
        for (Field field : fields) {
            JsonValue a = parse.get(field.getName());
            if (field.get(jsVa) instanceof Map<?,?>){
                IdentityHashMap<String, Boolean> objectObjectIdentityHashMap = new IdentityHashMap<>();
                for (JsonValue entry = a.child; entry != null; entry = entry.next){
                    objectObjectIdentityHashMap.put(entry.name, entry.asBoolean());
                }
                field.set(jsVa,objectObjectIdentityHashMap );
            }
            if(a!=null&&a.type()== JsonValue.ValueType.longValue){
                try {
                    field.set(jsVa,a.asInt());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Log.info(jsVa.toString());*/

        /*JsonReader json = new JsonReader();
        JsonValue parse = json.parse(new Fi(Core.files.getLocalStoragePath() + "\\iceConfig.json"));
        parse.toJson(JsonWriter.OutputType.json);
        JsonValue jsonValue = new JsonValue(object);
        jsVa.map.put("2",true);
        jsVa.map.put("3",false);
        jsVa.map.put("6",false);
        jsVa.map.put("7",true);
        jsVa.map.forEach((a,b)->{
            jsonValue.addChild(a,new JsonValue(b));
        });
        parse.addChild("map",jsonValue);
        try {
            FileOutputStream fos = new FileOutputStream(Core.files.getLocalStoragePath() + "\\iceConfig.json", false);
            fos.write(parse.toJson(JsonWriter.OutputType.json).getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

       /* Log.info( parse.toJson(JsonWriter.OutputType.json));
        Log.info( si);*/
    }
}
