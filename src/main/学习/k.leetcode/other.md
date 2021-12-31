# Table of Contents

* [[15]三数之和](#15三数之和)
* [[2]两数相加](#2两数相加)



#   [15]三数之和

- 利用Arrays.sort（）对数组进行排序

- 声明变量res用于接收结果

- 固定左指针k，开始遍历：从k=0 到 k= nums.length - 2; 

  >  k= nums.length - 2 是因为可能就只有三个数字

- 声明另外两个指针，左指针i= k+1; 右指针 j=num.length - 1

 ```java
  public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if(null==nums||nums.length<3){
            return result;
        }
        Arrays.sort(nums);
        for(int k = 0; k < nums.length - 2; k++){
            if(nums[k] > 0) break;
            if(k > 0 && nums[k] == nums[k - 1]) continue;
            int i = k + 1, j = nums.length - 1;
            while(i < j){
                int sum = nums[k] + nums[i] + nums[j];
                if(sum < 0){
                    while(i < j && nums[i] == nums[++i]);
                } else if (sum > 0) {
                    while(i < j && nums[j] == nums[--j]);
                } else {
                    result.add(new ArrayList<Integer>(Arrays.asList(nums[k], nums[i], nums[j])));
                    while(i < j && nums[i] == nums[++i]);
                    while(i < j && nums[j] == nums[--j]);
                }
            }
        }
        return result;
    }
 ```






#    [2]两数相加 


这里就是加法进位的问题  

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        ListNode result = new ListNode(0);
    
        if (null == l1 && l2 == null) {
            return result;
        }
    
        int a = 0;
        ListNode temp1 = l1;
        ListNode temp2 = l2;
        ListNode resultTemp = result;


        while (null != temp1 || null != temp2) {
    
            int count = temp1.val + temp2.val + a;
            resultTemp.next = new ListNode(count % 10);
            resultTemp = resultTemp.next;
            a = 0;
            if (count % 10 > 0) {
                a = 1;
            }
    
            temp1 = temp1.next;
            temp2 = temp2.next;
        }
    
        if (a != 0) {
            resultTemp.next = new ListNode(a);
            resultTemp = resultTemp.next;
        }
    
        return result.next;
    
    }
```


