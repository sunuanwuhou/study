# Table of Contents

* [MySQL如何分组拼接字符串？](#mysql如何分组拼接字符串)


# MySQL如何分组拼接字符串？

```mysql
SELECT  GROUP_CONCAT(DISTINCT home_town ORDER  BY home_town DESC) AS '领导关怀地区'
    FROM employees;
```
