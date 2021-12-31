# Table of Contents

* [AnnotatedBeanDefinitionReader](#annotatedbeandefinitionreader)




[toc]

# AnnotatedBeanDefinitionReader

无论是从前的SSM还是现在的SpringBoot,都是基于AbstractApplicationContext开始容器初始化过程。今天我们以

```java
AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
```


```java
public AnnotationConfigApplicationContext() {
    //创建一个AnnotatedBeanDefinitionReader
    this.reader = new AnnotatedBeanDefinitionReader(this);
    //创建一个ClassPathBeanDefinitionScanner
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}	

public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    //初始化AnnotationConfigApplicationContext
    this();
    //配置类注册到容器中
    register(componentClasses);
    //刷新容器
    refresh();
}
```

AnnotatedBeanDefinitionReader
这个类的主要作用是注册`BeanDefinition`，与之相类似的功能的类还有一个就是`ClassPathBeanDefinitionScanner`。它们最大的不同在于`AnnotatedBeanDefinitionReader`支持注册单个的`BeanDefinition`，而`ClassPathBeanDefinitionScanner`会一次注册所有扫描到的`BeanDefinition`。

```java
public class AnnotatedBeanDefinitionReader {
	//BeanDefinition注册表
	private final BeanDefinitionRegistry registry;
	//Bean名称的生成器，生成BeanName
	private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
	//解析@Scope注解
	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
	//解析@Conditional注解
	private ConditionEvaluator conditionEvaluator;
```

创建AnnotatedBeanDefinitionReader的过程中做了什么?

+ 创建environment

  ```java
  public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
  		this(registry, getOrCreateEnvironment(registry));
  }
  public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
      Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
      Assert.notNull(environment, "Environment must not be null");
      this.registry = registry;
      
      this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
      //从这个方法可以看出，Spring在创建reader对象的时候就开始注册bd了，那么Spring注册了哪些bd呢？注册的bd有什么用呢？我们接着往下看
      AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
  }
  ```

+ 注册容器初始bd
  + **指定容器使用的比较器，通过这个比较器能够解析@Order注解以及Ordered接口**
  + **指定容器使用的`AutowireCandidateResolver`**
  + **注册`ConfigurationClassPostProcessor`**
  + 注册`AutowiredAnnotationBeanPostProcessor`
  + 如果支持`JSR-250`，注册`CommonAnnotationBeanPostProcessor`
  + 如果支持`jpa`,注册`PersistenceAnnotationBeanPostProcessor`
  + 注册`EventListenerMethodProcessor`，用于处理`@EventListener`注解
  + 注册`DefaultEventListenerFactory`

看下注册bean的方法

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

 // Spring在这里写死了，直接new了一个AnnotatedGenericBeanDefinition，也就是说通过reader对象注册的BeanDefinition都是AnnotatedGenericBeanDefinition。
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
//调用conditionEvaluator的shouldSkip方法 判断当前的这个bd是否需要被注册
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
			return;
		}
		// 在注册时可以提供一个instanceSupplier
		abd.setInstanceSupplier(supplier);
		//解析@Scope注解，得到一个ScopeMetadata
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    	//将@Scope注解中的信息保存到bd中
		abd.setScope(scopeMetadata.getScopeName());
        // 调用beanNameGenerator生成beanName
        // 所谓的注册bd就是指定将bd放入到容器中的一个beanDefinitionMap中
        // 其中的key就是beanName,value就是解析class后得到的bd
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

		// 这句代码将进一步解析class上的注解信息，Spring在创建这个abd的信息时候就已经将当前的class放入其中了，所有这行代码主要做的就是通过class对象获取到上面的注解（包括@Lazy，@Primary，@DependsOn注解等等），然后将得到注解中对应的配置信息并放入到bd中的属性中
		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    
    //这一块不用关注
//就是说我们手动直接注册了一个类，但是我们没有在类上添加@Lazy，@Primary注解，但是我们又希望能将其标记为Primary为true/LazyInit为true,这个时候就手动传入Primary.class跟Lazy.class即可。 
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				if (Primary.class == qualifier) {
					abd.setPrimary(true);
				}
				else if (Lazy.class == qualifier) {
					abd.setLazyInit(true);
				}
				else {
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
    //我们注册时，我们可以传入一些回调方法，在解析得到bd后调用
		if (customizers != null) {
			for (BeanDefinitionCustomizer customizer : customizers) {
				customizer.customize(abd);
			}
		}
 // bd中是没有beanName属性的，BeanDefinitionHolder中就是保存了beanName以及对应的BeanDefinition
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);	
    // 这个地方主要是解析Scope中的ProxyMode属性，默认为no，不生成代理对象
  // 后文做详细分析
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    //// 注册bd到容器中，实际上最终就是将bd放到了beanFactory中的一个map里（beanDefinitionMap）
        // key为beanName,value为bd
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
	}

```

**AnnotationConfigUtils.applyScopedProxyMode做了什么？**

```java
// 这个方法名称直译过来就是----应用Scoped中的ProxyMode属性
// 这个属性有什么用呢？
// ProxyMode属性一共有下面几种取值
// 1.DEFAULT:默认值，默认情况下取no
// 2.NO:不开启代理
// 3.INTERFACES:使用jdk动态代理
// 4.TARGET_CLASS:使用cglib代理
// 假设我们有一个单例的对象A，其中有一个属性B，B的作用域是session的，这个时候容器在启动时创建A的过程中需要为A注入属性B，但是属性B的作用域为session,这种情况下注入必定会报错的
// 但是当我们将ProxyMode属性配置为INTERFACES/TARGET_CLASS时，它会暴露一个代理对象，ProxyMode可以配置代理对象的生成策略是使用jdk动态代理还是生成cglib动态代理,那么当我们在创建A时，会先注入一个B的代理对象而不是直接报错
static BeanDefinitionHolder applyScopedProxyMode(
    ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
 // 根据scopedProxyMode进行判断，如果是NO，直接返回原有的bd并添加到容器的bdMap中
    ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
    if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
        return definition;
    }
    // 是否采用cglib代理
    boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
    // 调用ScopedProxyCreator的createScopedProxy，创建代理对象对应的bd
    return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
}


```

ScopedProxyUtils.createScopedProxy

```java
// 根据目标对象的bd生成代理对象的bd,并且会将目标对象的bd注册到容器中
public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
                                                     BeanDefinitionRegistry registry, boolean proxyTargetClass) {
 // 目标对象的名称
    String originalBeanName = definition.getBeanName();
    // 目标对象的bd
    BeanDefinition targetDefinition = definition.getBeanDefinition();
    // 将来会将目标对象的bd注册到容器中，targetBeanName作为注册时的key
    // targetBeanName = "scopedTarget."+originalBeanName
    String targetBeanName = getTargetBeanName(originalBeanName);

 // 创建代理对象的bd,可以看到代理对象会是一个factoryBean的介绍请参考《》，关于factoryBean的介绍请参考我的Spring官网阅读系列第七篇，这里不做过多介绍
    RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
    // 代理对象所装饰的bd就是目标对象的bd
    // 拷贝了部分目标对象bd中的属性到代理对象的bd中
    proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
    proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
    proxyDefinition.setSource(definition.getSource());
    proxyDefinition.setRole(targetDefinition.getRole());
 
    proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
    if (proxyTargetClass) {
        targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
        // ScopedProxyFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
    }
    else {
        proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
    }

    // Copy autowire settings from original bean definition.
    proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
    proxyDefinition.setPrimary(targetDefinition.isPrimary());
    if (targetDefinition instanceof AbstractBeanDefinition) {
        proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
    }

    // The target bean should be ignored in favor of the scoped proxy.
    targetDefinition.setAutowireCandidate(false);
    targetDefinition.setPrimary(false);

    // 这一步会将原始的bd注册到容器中，其中的key="scopedTarget."+originalBeanName
    registry.registerBeanDefinition(targetBeanName, targetDefinition);

 // 返回这个用于创建代理对象的bd
    return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
}
```

从上面的代码可以看出，当我们选择在@Scope注解中配置了`proxyMode`属性时（INTERFACES/TARGET_CLASS），那么Spring会在注册bd时，在容器中注册一个代理的bd，这个bd是一个`ScopedProxyFactoryBean`类型的bd，并且没有特别指定它的作用域，所以它是单例的，并且这个`FactoryBean`返回就是对应的目标对象的代理对象。基于此，Spring就可以利用这个bd来完成在启动阶段对session/request域对象的注入。





```java
public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				initMessageSource();

				// Initialize event multicaster for this context.
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				onRefresh();

				// Check for listener beans and register them.
				registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```



