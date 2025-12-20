-keep class org.** { *; }
-keep class kotlin.** { *; }
-keep class universecore.** { *; }
-keep class regexodus.** { *; }
-keep class mindustry.** { *; }
-keep class ice.Ice { *; }

-keepclassmembers class ice.Ice {
     *;
}
-keep class *

-dontwarn

-keepattributes Signature          # 保留泛型信息
-keepattributes InnerClasses       # 保留内部类信息
-keepattributes EnclosingMethod    # 保留匿名类信息
-keepattributes RuntimeVisibleAnnotations     # 保留运行时可见的注解
-keepattributes RuntimeInvisibleAnnotations   # 保留运行时不可见的注解
-keepattributes EnclosingMethod
-keepattributes KotlinMetadata      # 保留Kotlin元数据
-keepattributes *Annotation*        # 保留所有注解相关属性
-keepattributes RuntimeVisibleParameterAnnotations    # 保留方法参数的运行时可见注解
-keepattributes RuntimeInvisibleParameterAnnotations  # 保留方法参数的运行时不可见注解
-keepattributes AnnotationDefault    # 保留注解默认值

-keepattributes SourceFile          # 保留源文件名
-keepattributes LineNumberTable     # 保留行号信息
#-keepattributes LocalVariableTable  # 保留局部变量表
#-keepattributes LocalVariableTypeTable  # 保留局部变量类型表


# 保护所有枚举类及其子类
-keep class * extends java.lang.Enum {
    *;
}

# 或者更精确的规则
-keepclassmembers class * extends java.lang.Enum {
    *;
}

# 保护所有枚举类及其子类
-keep class * extends java.lang.Enum {
    *;
}

# 或者更精确的规则
-keepclassmembers class * extends java.lang.Enum {
    *;
}


# 保持所有类的内部类
-keep class *$* {
    *;
}

# 保持Kotlin反射所需的元数据
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keep class kotlin.jvm.internal.** { *; }

# 保持枚举类的特殊方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持数组相关的反射操作
-keep class java.lang.reflect.Array {
    public static ** newInstance(...);
    public static ** get(...);
    public static ** set(...);
}