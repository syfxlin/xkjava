# XK-Java

> 一个轻量的 Java 框架

> 因为有个 Java EE 的课，所以偶尔会用这个框架写点稀奇古怪的东西。

## 描述 Description

本项目开发的动机同 [XK-PHP](https://github.com/syfxlin/xkphp) 一样，是一个为了熟悉对应的语言开发而开发的项目，由于先前也没有开发 Java
项目的经验，也没有开发过常驻内存的项目，所以本项目中可能会有很多不合理的设计，存在线程不安全的情况，或者有许多缺陷和漏洞，同 XK-PHP
一样，本项目不建议用于生产环境，仅用于学习就可以啦，若您有更好的建议或者发现不足的地方欢迎反馈。

项目是依照先前的 XK-PHP 的架构开发的，所以可能会有一些设计并不遵循 Java 的规范，

接口设计大部分是参考自 XK-PHP，不过也参考了 Spring 的部分设计，比如注解，切面，DispatchServlet 等等。

集成了一个 IoC 容器和添加了 Aop 的支持，IoC 容器中并不实际存储实例，实例通过 Context 的实现类进行管理，类似于 Spring 的 BeanFactory，如
ApplicationContext 和 RequestContext，这样就可以动态的添加拥有不同特性的 Context，比如 RequestContext
是线程安全的，而且可以动态的删除和创建，而 ApplicationContext 则没有这些功能，只是一个简单的存储容器。

Binding 和 Alias 直接存储于 IoC 容器中，为了保证 Request 作用域实例的线程安全，实例化后的对象并未存储于 IoC 容器（Binding）中而是存储于不同的 Context
中，当通过 `Binding.getInstance()` 的时候，实际上是到对应的 Context 中 `getInstance()`。拿 Request 作用域的实例举例吧，在 IoC
启动的时候会将 Request 作用域的实例绑定到 IoC 容器中，但是并不进行实例化，当首次使用的时候才会实例化。在请求结束的时候会清空 RequestContext，当下次请求到来的时候，由于
RequestContext 被清空了，相当于 Request 作用域的实例被重置为非实例化的状态，此时会再次实例化。这样就保证了在每次请求的时候都能保证 Request 作用域的线程安全。

由于 Java 是常驻内存的，不同于 PHP 每次请求都重新加载容器，所以 Java 需要考虑到不同客户端请求间的线程安全，不同线程间独立的实例存储于 RequestContext，如
Request，Response 等，然后通过 ObjectFactory 动态代理的方式通过 getObject 从 RequestContext
动态获取该线程下的实例。RequestContext 使用 ThreadLocal 保证线程安全。

IoC 容器可以自定义注入器，默认的注入器可以支持大部分场景，如果有无法实现的场景可以使用自定义注入器，不过需要注意线程安全。

注入的范围包括字段，方法，构造器。构造器注入无需使用 @Autowired 注解。字段注入如果有 WriteMethod 则无需使用 @Autowired 注解，否则你需要在字段上添加
@Autowired 注解。方法注入分为两种，一种是类似于 Spring 的 Aware 接口，用于对象实例化后立即注入，此情形下你需要添加 @Autowired
注解；还有一种是普通的方法，普通的方法如果使用 Application.call 的方法调用则会自动注入，无需使用 @Autowired 注解。

注入的时候容器会自动进行类型转换，如果查找不到依赖则会注入该类型的默认值。

集成中间件处理器，可以很方便的对请求和响应进行拦截，预处理，后处理等。过滤器，监听器，Servlet 目前已集成到框架中，对应的注解是
@Filter，@Listener，@Servlet。被这些注解标记的 Servlet 注解会被容器托管，所以支持自动注入和其他组件的特性，同时也可以使用 @Order 来进行排序。

请求和响应扩展自 Jetty 的 Request 和 Response，添加了一些类似 Laravel 的接口，并内置 RequestBody 到 JsonNode 的转换功能。

在控制器中，请求中的参数会自动注入到方法中，无需使用注解标注，同时也支持自动封装成对象，同 Spring MVC 封装的对象也支持嵌套，你只需定义好参数的名称，并使用点分隔即可。

最新的版本重构了 Response 的部分，添加了 HttpResult 一系列的响应对象，你可以直接返回这些对象，后续的中间件会把这些对象转换成对应的响应。使用的方式也很简单，你可以使用
Result 抽象类中的一系列静态方法，也可以 new 出来。

添加了很多同 Spring 的注解，不过有一些小改动，注解大部分都支持通过 @Order 进行排序，同时注解也可以使用 @AliasFor
使用别名，同时提供一个通用的注解处理抽象类，如果需要处理自定义注解可以通过继承该类快速实现。

支持组合注解（注解继承），子注解设置的值如果设置了 @AliasFor 到父注解，则在获取的时候会进行一次扫描，扫描时会将子注解的值同步到父注解上。修改的方式是通过反射修改
memberValues，所以会造成值留存的现象，暂时没有比较好的解决方案。所以在使用组合注解的时候应在需要的时候临时获取，尽量不要保存。

由于支持了组合注解和注解继承，所以已具备一定程度上的自动配置功能。

目前框架已经将大部分组件使用注解进行加载和配置，替换和修改组件变得更加容易了，不用再为各种依赖头疼。

支持 AspectJ 切面，需要经过 IoC 容器处理后才能生效，同时需要实现 Advice 接口，可以直接继承自 AbstractAdvice，从容器中 Make 或者 Call，以及绑定到容器的
Bean 都可以注入切面。切面实现使用的是 Cglib。

路由是参考 PHP 的 FastRoute 制作而成的，RouteCollector 会将 Handler 封装成集成了中间件的 RouteHandler，然后依照静态路由或动态路由的方式存入到
staticRoutes 或 variableRoutes。路由匹配的方式采用的是和 FastRoute 一样的匹配方式，在路由调度器创建的时候，RouteGenerator
会将所有动态路由的表达式合成成一个路由表达式，在匹配的时候就只需要进行一次匹配，可以在一定程度上提高路由的匹配速度。

支持使用注解开启事务，不过做的很简单，可能会有一些问题。

替换了 Jetty 自带的 ErrorHandler，改用支持动态响应 HTML 和 JSON 数据内容的 ErrorHandler。

最新版去掉了门面，改用 Helper 静态方法，你可以使用静态导入的方式使用这些 Helper 方法。

ORM 使用的是 Mybatis Plus，视图采用 Thymeleaf, FreeMarker 渲染，HttpServer 采用 Jetty，参数验证使用的是
HibernateValidator（由于后续才引入的 HuTool，就懒得改了），类型转换器和一些其他工具使用的是 HuTool，数据源使用的是 HikariCP，JSON 库采用的是
Jackson，由于 Java 默认不会保留参数名称，加编译选项在我这出现时好时坏的情况，所以本项目直接采用了 ASM 来获取参数名称。使用 SpringEL（第三方库，去除了 Spring Core
依赖） 来解析 @Value 的表达式。

添加了 @PostConstruct 和 @PreDestroy 的支持

添加了 @DataBind 和 WebDataBinder 的支持，@DataBind 用于标注传入参数的名称或前缀，如 GET 请求参数为
name=name1&user.name=name2，如果在 @DataBind 中设置名称为 user，那么会使用 WebDataBinder 注入，注入的值就是
name2。若注入的不是请求参数而是绑定到容器的对象，那么会使用 DefaultDataBinder 注入，注入值是在容器中指定名称的对象或值。

@Autowired 和 @DataBind 都已经支持设置 required，一旦无法找到注入的对象或值，那么就会抛出 NullPointerException。

同时添加了 @Valid 校验注解，使用方式和 Spring 差不多，目前只支持在参数上使用，在参数上添加该注解，容器在注入后就会使用 HibernateValidator
对对象进行验证，如果验证失败会将信息封装到 ValidResult 和 ValidGroup 中，如果有多个错误，那么 ValidResult 只会保留最后一个。同 Spring
一样，如果注入的参数不包含 ValidResult 或 ValidGroup（在 Spring 中是 BindingResult），一旦校验失败则会抛出
ValidException。如果注入的参数中包含 ValidResult 或 ValidGroup，那么在校验失败的时候，容器不会抛出 ValidException，此时就需要在
Controller 中进行错误处理。

## TODO

-   添加完整的 Test Case
-   多级缓存
-   自动配置
-   FileResult，StreamResult
-   异步控制器

## 文档 Doc

暂无

## 维护者 Maintainer

XK-Java 由 [Otstar Lin](https://ixk.me/)
和下列 [贡献者](https://github.com/syfxlin/xkjava/graphs/contributors) 的帮助下撰写和维护。

> Otstar Lin - [Personal Website](https://ixk.me/) · [Blog](https://blog.ixk.me/) · [Github](https://github.com/syfxlin)

## 许可证 License

![License](https://img.shields.io/github/license/syfxlin/xkjava.svg?style=flat-square)

根据 Apache License 2.0 许可证开源。
