/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.ControllerAdvice;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.kernel.InitBinderHandlerResolver;

@AnnotationProcessor
@Order(Order.HIGHEST_PRECEDENCE + 3)
public class InitBinderAnnotationProcessor extends AbstractAnnotationProcessor {

  public InitBinderAnnotationProcessor(XkJava app) {
    super(app);
  }

  @Override
  public void process() {
    List<InitBinderHandlerResolver> handlerResolvers = new ArrayList<>();
    List<Class<?>> controllerAdvices =
      this.getTypesAnnotated(ControllerAdvice.class);
    for (Class<?> adviceType : controllerAdvices) {
      InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
        adviceType
      );
      if (resolver.hasInitBinderList()) {
        handlerResolvers.add(resolver);
      }
    }
    this.app.setAttribute("adviceInitBinderHandlerResolver", handlerResolvers);

    Map<Class<?>, InitBinderHandlerResolver> controllerResolvers = new LinkedHashMap<>();
    List<Class<?>> controllers = this.getTypesAnnotated(Controller.class);
    for (Class<?> controller : controllers) {
      InitBinderHandlerResolver resolver = new InitBinderHandlerResolver(
        controller
      );
      if (resolver.hasInitBinderList()) {
        controllerResolvers.put(controller, resolver);
      }
    }
    this.app.setAttribute(
        "controllerInitBinderHandlerResolver",
        controllerResolvers
      );
  }
}
