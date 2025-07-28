package ice.library.type.components

interface NutrientConcentration {
    var nutrientConcentration: Float

    fun getNutrient(): Float {
        return nutrientConcentration
    }

    fun setNutrient(amount: Float) {
        nutrientConcentration = amount
    }

    fun addNutrient(amount: Float) {
        nutrientConcentration += amount
    }
}