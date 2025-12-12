-ignorewarnings

-keep class org.** { *; }
-keep class kotlin.** { *; }
-keep class universecore.** { *; }
-keep class regexodus.** { *; }
-keep class mindustry.** { *; }
-keep class ice.Ice { *; }

-keepclassmembers class ice.Ice {
    public *;
}


-dontwarn **
-microedition
-dontusemixedcaseclassnames
-dontshrink
-dontpreverify
