package scala

import mindustry.`type`.Item

class SCItems {
  object SCItems {
    var i, r: Item = _

    def load(): Unit = {
      i = new Item("i") {
        flammability = 1
      }
      r = new Item("r") {
        flammability = 1
      }
    }
  }
}


