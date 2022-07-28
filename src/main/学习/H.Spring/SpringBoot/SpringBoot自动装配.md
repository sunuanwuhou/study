# Table of Contents

* [基础知识](#基础知识)
* [selectImports](#selectimports)
* [getAutoConfigurationEntry](#getautoconfigurationentry)
* [总结](#总结)




SpringBoot自动配置的核心就在`@EnableAutoConfiguration`注解上，这个注解通过`@Import(AutoConfigurationImportSelector)`来完成自动配置。



# 基础知识

1. `@Import`注解是什么？有什么用？
2. ImportSelector接口是什么？有什么用？



给spring注入组件。





# selectImports

```java
  public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!this.isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        } else {
            AutoConfigurationImportSelector.AutoConfigurationEntry autoConfigurationEntry = this.getAutoConfigurationEntry(annotationMetadata);
            return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
        }
    }
```

# getAutoConfigurationEntry

```java
protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
   // 判断是否开启自动配置
   if (!isEnabled(annotationMetadata)) {
      return EMPTY_ENTRY;
   }
   // 获取@EnableAutoConfiguration注解的属性
   AnnotationAttributes attributes = getAttributes(annotationMetadata);
   // 从spring.factories文件中获取配置类的全限定名数组
   List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
   // 去重
   configurations = removeDuplicates(configurations);
   // 获取注解中exclude或excludeName排除的类集合
   Set<String> exclusions = getExclusions(annotationMetadata, attributes);
   // 检查被排除类是否可以实例化，是否被自动配置所使用，否则抛出异常
   checkExcludedClasses(configurations, exclusions);
   // 去除被排除的类
   configurations.removeAll(exclusions);
   // 使用spring.factories配置文件中配置的过滤器对自动配置类进行过滤
   configurations = getConfigurationClassFilter().filter(configurations);
   // 抛出事件
   fireAutoConfigurationImportEvents(configurations, exclusions);
   return new AutoConfigurationEntry(configurations, exclusions);
}
```





# 总结

1. spring boot会默认加载spring.factories和当前启动类所在包下。
