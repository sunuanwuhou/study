# Table of Contents

* [TwoSum I](#twosum-i)




先说总结:

> 本质就是让我们学会如何使用哈希表处理问题，这个在工作中经常用到。



# TwoSum I




```java
 public static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i <= nums.length - 1; i++) {

            int temp = target - nums[i];

            if(linkedHashMap.containsKey(temp)){
                result[0] = linkedHashMap.get(temp);
                result[1] = i;
                break;
            }
            linkedHashMap.put(nums[i],i);
        }
        return result;
    }
```





**一般情况下，我们会首先把数组排序再考虑双指针技巧**。TwoSum 启发我们，HashMap 或者 HashSet 也可以帮助我们处理无序数组相关的简单问题。

另外，设计的核心在于权衡，利用不同的数据结构，可以得到一些针对性的加强。

最后，如果 TwoSum I 中给的数组是有序的，应该如何编写算法呢？答案很简单，前文 [双指针技巧汇总](http://mp.weixin.qq.com/s?__biz=MzU0MDg5OTYyOQ==&mid=2247484119&idx=1&sn=4e7a1389ced3b45de694605c03750d5d&chksm=fb336295cc44eb832640d174844f3622457c69b48c4a18e2f599a88eacb797af4f30bfe3312c&scene=21#wechat_redirect) 写过：



```java
int[] twoSum(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    while (left < right) {
        int sum = nums[left] + nums[right];
        if (sum == target) {
            return new int[]{left, right};
        } else if (sum < target) {
            left++; // 让 sum 大一点
        } else if (sum > target) {
            right--; // 让 sum 小一点
        }
    }
    // 不存在这样两个数
    return new int[]{-1, -1};
}
```

