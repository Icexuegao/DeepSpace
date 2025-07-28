package ice.library.sortAlgorithm;

/**
 * 插入排序的代码实现虽然没有冒泡排序和选择排序那么简单粗暴，但它的原理应该是最容易理解的了，
 * 因为只要打过扑克牌的人都应该能够秒懂。插入排序是一种最简单直观的排序算法
 * ，它的工作原理是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。
 * 插入排序和冒泡排序一样，也有一种优化算法，叫做拆半插入。
 */

object InsertionSorting {
    fun sort(sourceArray: IntArray): IntArray {
        // 对 arr 进行拷贝，不改变参数内容
        /* Arrays.copyOf(sourceArray, sourceArray.length);*/
        // 从下标为1的元素开始选择合适的位置插入，因为下标为0的只有一个元素，默认是有序的
        for (i in 1 until sourceArray.size) {
            // 记录要插入的数据
            val tmp = sourceArray[i]
            // 从已经排序的序列最右边的开始比较，找到比其小的数
            var j = i
            while (j > 0 && tmp < sourceArray[j - 1]) {
                sourceArray[j] = sourceArray[j - 1]
                j--
            }
            // 存在比其小的数，插入
            if (j != i) {
                sourceArray[j] = tmp
            }
        }
        return sourceArray
    }
}