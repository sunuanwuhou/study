# Table of Contents

* [应用场景](#应用场景)
* [暴力破解法](#暴力破解法)
* [KMP](#kmp)
  * [部分匹配表](#部分匹配表)
  * [图列分析](#图列分析)
  * [代码实现(不太懂)](#代码实现不太懂)






# 应用场景

如果给定文本串S:`BBC ABCDAB ABCDABCDABDE`，和模式串P:`ABCDABD`，现在要拿模式串P去跟文本串S匹配，

判断S是否含有P,如果存在，就返回第一次出现的位置，如果没有，返回-1.



# 暴力破解法

 其实就是不断的将模式串与主串进行匹配，如果匹配不成功，主串回溯到原来位置的下一个位置，然后模式串回溯到最开头。具体代码为:

```java
public static int match(String s, String p) {
        char[] s1 = s.toCharArray();
        char[] p1 = p.toCharArray();
        int i = 0;
        int j = 0;
        while (i < s1.length && j < p1.length) {
            if(s1[i]==p1[j]){
                i++;
                j++;
            }else {
                i = i - j + 1;
                j = 0;
            }
        }
        if(j== p1.length){
            return i-j;
        }else {
            return -1;
        }
    }
```

# KMP

KMP 算法是 D.E.Knuth、J,H,Morris 和 V.R.Pratt 三位神人共同提出的，称之为 Knuth-Morria-Pratt 算法，简称 KMP 算法。该算法相对于 Brute-Force（暴力）算法有比较大的改进，主要是消除了主串指针的回溯，从而使算法效率有了某种程度的提高。


> 利用之前判断过的信息，通过一个next数组，也叫部分匹配表，保存**模式串**中最长公共子序列的长度，每次回溯时，通过next数组找到。



文章参考：https://www.pdai.tech/md/algorithm/alg-domain-char-match-kmp.html#%E7%AE%97%E6%B3%95%E5%9B%BE%E4%BE%8B


## 部分匹配表

先，要了解两个概念："前缀"和"后缀"。 "前缀"指除了最后一个字符以外，一个字符串的全部头部组合；"后缀"指除了第一个字符以外，一个字符串的全部尾部组合。



![](.images/alg-kpm-14.png)

"部分匹配值"就是"前缀"和"后缀"的最长的共有元素的长度。以"ABCDABD"为例，

```java
－　"A"的前缀和后缀都为空集，共有元素的长度为0；

－　"AB"的前缀为[A]，后缀为[B]，共有元素的长度为0；

－　"ABC"的前缀为[A, AB]，后缀为[BC, C]，共有元素的长度0；

－　"ABCD"的前缀为[A, AB, ABC]，后缀为[BCD, CD, D]，共有元素的长度为0；

－　"ABCDA"的前缀为[A, AB, ABC, ABCD]，后缀为[BCDA, CDA, DA, A]，共有元素为"A"，长度为1；

－　"ABCDAB"的前缀为[A, AB, ABC, ABCD, ABCDA]，后缀为[BCDAB, CDAB, DAB, AB, B]，共有元素为"AB"，长度为2；

－　"ABCDABD"的前缀为[A, AB, ABC, ABCD, ABCDA, ABCDAB]，后缀为[BCDABD, CDABD, DABD, ABD, BD, D]，共有元素的长度为0。

```



![](.images/alg-kpm-15.png)



"部分匹配"的实质是，有时候，**字符串头部和尾部会有重复**。

比如，"ABCDAB"之中有两个"AB"，那么它的"部分匹配值"就是2（"AB"的长度）。搜索词移动的时候，第一个"AB"向后移动4位（字符串长度-部分匹配值），就可以来到第二个"AB"的位置。



## 图列分析

知道了上面的`部分匹配`后我们来看看。



![](.images/alg-kpm-9.png)

已知空格与D不匹配时，前面六个字符"ABCDAB"是匹配的。**(主要这里2个字符串都是六个字符"ABCDAB")**

查表可知，最后一个匹配字符B对应的"部分匹配值"为2，因此按照下面的公式算出向后移动的位数：

> 移动位数 = 已匹配的字符数 - 对应的部分匹配值

因为 6 - 2 等于4，所以将搜索词向后移动4位。

依次类推即可。



<font color=red>重点其实时部分匹配表的思想</font>

## 代码实现(不太懂)

```java
public static int match(String s, String p) {

        int[] next = getMatch(p);
        for (int i = 1, j = 0; i < s.length(); i++) {

            while (j > 0 && s.charAt(i) != p.charAt(j)) {
                j = next[j - 1];
            }
            if (s.charAt(i) == p.charAt(j)) {
                j++;
            }
            if (j == p.length()) {
                return i - j + 1;
            }
        }

        return -1;
    }


    /**
     * 获取部分匹配表
     *
     * @param dest
     * @return
     */
    public static int[] getMatch(String dest) {
        int[] next = new int[dest.length()];
        //只有一个时 数组肯定为0
        next[0] = 0;
        for (int i = 1, j = 0; i < dest.length(); i++) {

            while (j > 0 && dest.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            if (dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }

//这种好理解下 也符合正常人思路
public static int[] next(String s) {
    int a[] = new int[s.length()];
    a[0] = 0;
    for (int i = 1; i < s.length(); i++) {
        for (int u = i - 1; u>=0; u--) {
            if (s.substring(0, u+1).equals(s.substring(i - u, i+1))) {
                a[i] = u+1;
                break;
            }
        }
    }
    return a;
}
```



