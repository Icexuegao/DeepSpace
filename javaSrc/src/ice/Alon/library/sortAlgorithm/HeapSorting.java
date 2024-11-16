package ice.Alon.library.sortAlgorithm;

import java.util.Arrays;

/**
 * 堆排序（Heapsort）是指利用堆这种数据结构所设计的一种排序算法。
 * 堆积是一个近似完全二叉树的结构，并同时满足堆积的性质：即子结点的键值或索引总是小于（或者大于）它的父节点。
 * 堆排序可以说是一种利用堆的概念来排序的选择排序。分为两种方法：
 * 大顶堆：每个节点的值都大于或等于其子节点的值，在堆排序算法中用于升序排列；
 * 小顶堆：每个节点的值都小于或等于其子节点的值，在堆排序算法中用于降序排列；
 * 堆排序的平均时间复杂度为 Ο(nlogn)。
 */
public class HeapSorting {
    public static class HeapSorting1 {
        public int[] sort(int[] sourceArray) {
            int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

            int len = arr.length;

            buildMaxHeap(arr, len);

            for (int i = len - 1; i > 0; i--) {
                swap(arr, 0, i);
                len--;
                heapify(arr, 0, len);
            }
            return arr;
        }

        public void buildMaxHeap(int[] arr, int len) {
            for (int i = (int) Math.floor(len / 2); i >= 0; i--) {
                heapify(arr, i, len);
            }
        }

        public void heapify(int[] arr, int i, int len) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;

            if (left < len && arr[left] > arr[largest]) {
                largest = left;
            }

            if (right < len && arr[right] > arr[largest]) {
                largest = right;
            }

            if (largest != i) {
                swap(arr, i, largest);
                heapify(arr, largest, len);
            }
        }

        public void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    /**
     * 堆排序是不稳定的排序
     * 既然如此，每次构建大顶堆时，在 父节点、左子节点、右子节点取三者中最大者作为父节点就行。
     * 我们追寻的只是最终排序后的结果，所以可以简化其中的步骤。
     */
    public static class HeapSorting2 {
        public int[] sort(int a[]) {
            int len = a.length - 1;
            for (int i = len; i > 0; i--) {
                maxHeap(a, i);
                //交换 跟节点root 与 最后一个子节点i 的位置
                swap(a, 0, i);
                //i--无序数组尺寸减少了
            }
            return a;
        }

        /**
         * 构建一个大顶堆（完全二叉树 ）
         * 从  最后一个非叶子节点  开始，若父节点小于子节点，则互换他们两的位置。然后依次从右至左，从下到上进行！
         * 最后一个非叶子节点，它的叶子节点 必定包括了最后一个（叶子）节点，所以 最后一个非叶子节点是 a[（n+1）/2-1]
         *
         * @param a
         * @param lastIndex 这个数组的最后一个元素
         */
        public void maxHeap(int a[], int lastIndex) {
            for (int i = (lastIndex + 1) / 2 - 1; i >= 0; i--) {
                //反正 堆排序不稳定，先比较父与左子，大则交换；与右子同理。（不care 左子与右子位置是否变了！）
                if (i * 2 + 1 <= lastIndex && a[i] < a[i * 2 + 1]) {
                    swap(a, i, i * 2 + 1);
                }
                if (i * 2 + 2 <= lastIndex && a[i] < a[i * 2 + 2]) {
                    swap(a, i, i * 2 + 2);
                }
            }
        }

        public void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
