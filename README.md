# zuul-handlerequest-demo
通过zuul修改请求参数——对请求参数进行解密
zuul是netflix开源的一个API Gateway 服务器, 本质上是一个web servlet应用，Zuul 在云平台上提供动态路由，监控，弹性，安全等边缘服务的框架，Zuul 相当于是设备和 Netflix 流应用的 Web 网站后端所有请求的前门。zuul的核心是一系列的filters, 其作用可以类比Servlet框架的Filter，或者AOP。

在基于 springcloud 构建的微服务系统中，通常使用网关zuul来进行一些用户验证等过滤的操作，比如 用户在 header 或者 url 参数中存放了 token ，网关层需要 用该 token 查出用户 的 userId ，并存放于 request 中，以便后续微服务可以直接使用而避免再去用 token 查询。

在这里，使用zuul的过滤器对请求参数验签（解密），然后发给后续的微服务。

共三个服务：注册中心，zuul服务，通过zuul能访问到的服务。

流程：zuul服务和另一个服务注册到注册中心上，带有加密过得参数的请求url经过zuul处理参数解密之后发给后续微服务。

首先获取到request，但是在request中只有getParameter（）而没有setParameter（）方法，所以直接修改url参数不可行，另外在request中虽然可以setAttribute（），但是可能由于作用域（request）的不同，一台服务器才能getAttribute（）出来，在这里设置的Attribute在后续的微服务中是获取不到的，因此必须考虑另外的方式：get方法和其他方法处理方式不同，post和put需重写HttpServletRequestWrapper，即获取请求的输入流，重写json参数，传入重写构造上下文中的request中。
