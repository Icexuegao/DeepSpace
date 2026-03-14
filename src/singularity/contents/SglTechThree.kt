package singularity.contents

import arc.util.Time
import ice.content.IItems
import ice.content.IItems.FEX水晶
import ice.content.IItems.以太能
import ice.content.IPlanets.阿德里
import ice.content.IUnitTypes.伊普西龙
import mindustry.content.Blocks
import mindustry.content.UnitTypes
import singularity.game.researchs.Inspire.*
import singularity.game.researchs.ResearchManager.ResearchSDL
import singularity.game.researchs.ResearchProject
import singularity.game.researchs.RevealGroup.ResearchReveal

class SglTechThree : ResearchSDL(), ContentList {
  companion object {
    lateinit var test1: ResearchProject
    lateinit var test2: ResearchProject
    lateinit var test3: ResearchProject
    lateinit var test4: ResearchProject
    lateinit var test5: ResearchProject
    lateinit var test6: ResearchProject
    lateinit var test7: ResearchProject
    lateinit var test8: ResearchProject
    lateinit var test9: ResearchProject
    lateinit var test10: ResearchProject
    lateinit var test11: ResearchProject
    lateinit var test12: ResearchProject
    lateinit var test13: ResearchProject
    lateinit var test14: ResearchProject
    lateinit var test15: ResearchProject
    lateinit var test16: ResearchProject
    lateinit var test17: ResearchProject
    lateinit var test18: ResearchProject
    lateinit var test19: ResearchProject
    lateinit var test20: ResearchProject
  }

  override fun load() {
    makePlanetContext(阿德里) {
      test1 = research("test-1", 180, 34) {
        contents(IItems.低碳钢, IItems.高碳钢)
      }
      test2 = research("test-2", 180) {
        contents(FEX水晶)
        dependencies("test-1")
        inspire(ResearchInspire(test4))
      }

      test3 = research("test-3", 180) {
        contents(FEX水晶, FEX水晶)
        dependencies("test-1")
        inspire(ResearchInspire(test2))
      }
      test4 = research("test-4", 180) {
        contents(FEX水晶, FEX水晶)
        dependencies("test-1")
        inspire(ResearchInspire(test3))
      }
      test5 = research("test-5", 180) {
        contents(FEX水晶)
        dependencies("test-1")
      }

      test6 = research("test-6", 180) {
        contents(FEX水晶, FEX水晶, FEX水晶)
        dependencies("test-2")
        inspire(PlaceBlockInspire(Blocks.daciteBoulder))
      }
      test7 = research("test-7", 180) {
        contents(FEX水晶, FEX水晶)
        dependencies("test-2")
        inspire(PlaceBlockInspire(Blocks.daciteBoulder, 4))
      }
      test8 = research("test-8", 180) {
        contents(FEX水晶, FEX水晶)
        dependencies("test-3")
        inspire(ResearchInspire(test5))
      }
      test9 = research("test-9", 180) {
        contents(FEX水晶)
        dependencies("test-3")
        inspire(ResearchInspire(test4))
      }
      test10 = research("test-10", 180) {
        contents(以太能, 以太能, 以太能)
        dependencies("test-2", "test-4")
        inspire(CreateUnitInspire(伊普西龙))
      }
      test11 = research("test-11", 180) {
        contents(以太能)
        dependencies("test-9")
      }
      test12 = research("test-12", 180) {
        contents(以太能, 以太能)
        dependencies("test-7")
        inspire(ResearchInspire(test11))
      }
      test13 = research("test-13", 180) {
        contents(以太能, 以太能)
        dependencies("test-8")
      }
      test14 = research("test-14", 180) {
        contents(以太能, 以太能)
        dependencies("test-12", "test-15")
        inspire(PlaceBlockInspire(Blocks.daciteBoulder))
      }
      test15 = research("test-15", 180) {
        contents(以太能)
        dependencies("test-10")
      }
      test16 = research("test-16", 180) {
        contents(以太能)
        dependencies("test-11")
        inspire(PlaceBlockInspire(Blocks.daciteBoulder))
      }
      test17 = research("test-17", 180) {
        contents(以太能)
        dependencies("test-15")
        inspire(CreateUnitInspire(UnitTypes.anthicus))
      }
      reveal(ResearchReveal("reveal_test", test14)) {
        test18 = research("test-18", 180) {
          contents(以太能)
          dependencies("test-17")
          showRevealess()
        }
        test19 = research("test-19", 180) {
          contents(以太能)
          dependencies("test-18")
        }
        test20 = research("test-20", 180) {
          contents(以太能)
          dependencies("test-18")
        }
      }
    }

    Time.run(1f) {}
  }
}