package ice.vars;

import arc.util.Time;
import ice.content.IPlanets;
import ice.vars.researchs.Inspire;
import ice.vars.researchs.ResearchManager;
import ice.vars.researchs.ResearchProject;
import ice.vars.researchs.RevealGroup;
import mindustry.content.UnitTypes;

import static mindustry.content.Blocks.*;
import static mindustry.content.Items.silicon;
import static mindustry.content.UnitTypes.oct;
import static mindustry.content.UnitTypes.zenith;

public class SglTechThree extends ResearchManager.ResearchSDL {
    public static ResearchProject test1, test2, test3, test4, test5, test6, test7, test8, test9, test10, test11, test12, test13, test14, test15, test16, test17, test18, test19, test20;


    public static void load() {
        makePlanetContext(IPlanets.INSTANCE.get阿德里(), () -> {
            test1 = research("test-1", 180, () -> {
                contents(silicon);
            });

          /*
*/
            test2 = research("test-2", 180, () -> {
                contents(silicon);
                dependencies("test-1");
            });
            test3 = research("test-3", 180, () -> {
                contents(silicon, silicon);
                dependencies("test-1");
                inspire(new Inspire.ResearchInspire(test2));
            });
            test4 = research("test-4", 180, () -> {
                contents(silicon, silicon);
                dependencies("test-1");
                inspire(new Inspire.ResearchInspire(test3));
            });

            research("121-1", 180, () -> {
                contents(silicon);
                dependencies("test-1");
            });
            research("1212-1", 180, () -> {
                contents(silicon);
                dependencies("test-1");
            });
            research("12221-1", 180, () -> {
                contents(silicon);
                dependencies("test-1");
            });
            research("222-1", 180, () -> {
                contents(silicon);
                dependencies("test-1");
            });

            test5 = research("test-5", 180, () -> {
                contents(additiveReconstructor);
                dependencies("test-1");
            });
            test6 = research("test-6", 180, () -> {
                contents(silicon, silicon, silicon);
                dependencies("test-2");
                inspire(new Inspire.PlaceBlockInspire(afflict));
            });
            test7 = research("test-7", 180, () -> {
                contents(silicon, silicon);
                dependencies("test-2");
                inspire(new Inspire.PlaceBlockInspire(afflict, 4));
            });
            test8 = research("test-8", 180, () -> {
                contents(daciteBoulder, daciteBoulder);
                dependencies("test-3");
                inspire(new Inspire.ResearchInspire(test5));
            });
            test9 = research("test-9", 180, () -> {
                contents(daciteBoulder);
                dependencies("test-3");
                inspire(new Inspire.ResearchInspire(test4));
            });
            test10 = research("test-10", 180, () -> {
                contents(daciteBoulder, daciteBoulder, daciteBoulder);
                dependencies("test-2", "test-4");
                inspire(new Inspire.CreateUnitInspire(UnitTypes.alpha));
            });
            test11 = research("test-11", 180, () -> {
                contents(liquidContainer);
                dependencies("test-9");
            });
            test12 = research("test-12", 180, () -> {
                contents(daciteBoulder, daciteBoulder);
                dependencies("test-7");
                inspire(new Inspire.ResearchInspire(test11));
            });
            test13 = research("test-13", 180, () -> {
                contents(daciteBoulder, daciteBoulder);
                dependencies("test-8");
            });
            test14 = research("test-14", 180, () -> {
                contents(daciteBoulder, daciteBoulder);
                dependencies("test-12", "test-15");
                inspire(new Inspire.PlaceBlockInspire(daciteBoulder));
            });
            test15 = research("test-15", 180, () -> {
                contents(daciteBoulder);
                dependencies("test-10");
            });
            test16 = research("test-16", 180, () -> {
                contents(daciteBoulder);
                dependencies("test-11");
                inspire(new Inspire.PlaceBlockInspire(daciteBoulder));
            });
            test17 = research("test-17", 180, () -> {
                contents(zenith);
                dependencies("test-15");
                inspire(new Inspire.CreateUnitInspire(oct));
            });

            reveal(new RevealGroup.ResearchReveal("reveal_test", test14), () -> {
                test18 = research("test-18", 180, () -> {
                    contents(daciteBoulder);
                    dependencies("test-17");
                    showRevealess();
                });

                test19 = research("test-19", 180, () -> {
                    contents(daciteBoulder);
                    dependencies("test-18");
                });

                test20 = research("test-20", 180, () -> {
                    contents(liquidSource);
                    dependencies("test-18");
                });
            });
        });

        Time.run(1, () -> {
            test14.reset();
        });
    }
}
