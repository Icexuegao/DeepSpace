package ice.library.world

/**
 * Load 接口定义了一个加载行为的基本规范
 * 该接口包含一个抽象方法 load(),实现该接口的类需要提供具体的加载逻辑,否则只对类进行初始化
 */
interface Load {
    /**类初始化调用*/
    fun setup() {}

    /**模组类调用init()*/
    fun init() {}

    /**模组类调用load()*/
    fun load() {}
}