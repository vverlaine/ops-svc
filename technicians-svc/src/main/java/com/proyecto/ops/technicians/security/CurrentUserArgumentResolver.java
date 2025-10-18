package com.proyecto.ops.technicians.security;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
  public static final String REQ_ATTR = "AUTH_USER";

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentUser.class)
        && Objects.equals(parameter.getParameterType(), AuthenticatedUser.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
    return req.getAttribute(REQ_ATTR);
  }
}