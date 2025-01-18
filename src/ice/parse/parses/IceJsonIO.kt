package ice.parse.parses

import arc.util.serialization.Json
import arc.util.serialization.JsonValue
import ice.Ice
import mindustry.Vars
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack

object IceJsonIO {
    fun apply(json: Json) {
        json.setSerializer(Liquid::class.java, object : Json.Serializer<Liquid> {
            override fun write(json: Json, liquid: Liquid, knownType: Class<*>?) {
                json.writeValue(liquid.name)
            }

            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): Liquid {
                val asString = jsonData.asString()
                return Vars.content.liquid(asString) ?: Vars.content.liquid("${Ice.NAME}-$asString")
            }
        })
        json.setSerializer(LiquidStack::class.java, object : Json.Serializer<LiquidStack> {
            override fun write(json: Json, liquids: LiquidStack, knownType: Class<*>?) {
                json.writeValue("${liquids.liquid.name}/${liquids.amount}")
            }

            override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): LiquidStack {
                val liquidAmount = jsonData.asString().split("/")
                val liquid = json.getSerializer(Liquid::class.java).read(json, JsonValue(liquidAmount[0]), type)
                return LiquidStack(liquid, liquidAmount[1].toFloat())
            }
        })

        json.setSerializer(Item::class.java, object : Json.Serializer<Item> {
            override fun write(json: Json, item: Item, knownType: Class<*>) {
                json.writeValue(item.name)
            }

            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): Item {
                val asString = jsonData.asString()
                return Vars.content.item(asString) ?: Vars.content.item("${Ice.NAME}-$asString")
            }
        })
        json.setSerializer(ItemStack::class.java, object : Json.Serializer<ItemStack> {

            override fun write(json: Json, items: ItemStack, knownType: Class<*>) {
                json.writeValue("${items.item.name}/${items.amount}")
            }

            override fun read(json: Json, jsonData: JsonValue, type: Class<*>): ItemStack {
                val itemAmount = jsonData.asString().split("/")
                val item = json.getSerializer(Item::class.java).read(json, JsonValue(itemAmount[0]), type)
                return ItemStack(item, itemAmount[1].toInt())
            }
        })
    }
}