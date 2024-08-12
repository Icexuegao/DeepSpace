package scala

import mindustry.`type`.Item

class SCItems {
  var i, r: Item

  def df(): Unit = {
    i = new Item("i") {
      flammability = 1
    }
    r = new Item("r") {
      flammability = 1
    }
  }
}
