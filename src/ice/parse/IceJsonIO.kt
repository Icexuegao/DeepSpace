package ice.parse

import arc.util.serialization.Json
import arc.util.serialization.JsonValue
import mindustry.io.JsonIO
import mindustry.type.Liquid
import mindustry.type.LiquidStack

object IceJsonIO {
    fun apply(json: Json) {
        json.setSerializer(LiquidStack::class.java, object : Json.Serializer<LiquidStack> {
            override fun write(json: Json, liquids: LiquidStack, knownType: Class<*>?) {
                json.writeObjectStart()
                json.writeValue("liquid", liquids.liquid)
                json.writeValue("amount", liquids.amount)
                json.writeObjectEnd()
            }

            override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): LiquidStack {
                return LiquidStack(
                    JsonIO.json.getSerializer(Liquid::class.java).read(
                        json, jsonData["liquid"], Liquid::class.java
                    ), jsonData.getFloat("amount")
                )
            }
        })
    }
}