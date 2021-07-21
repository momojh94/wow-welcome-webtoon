/*
package com.www.api.common.aop;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns= "/*")
public class PrintURLFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("PrintURLFilter init() 시작 ------------------------------------");
        String FilterParam = config.getInitParameter("FilterParam");

        System.out.println("FilterParam: " + FilterParam + " -----------------");
        System.out.println("PrintURLFilter init() 끝--------------------------------------------------");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.println("PrintURLFilter doFileter() 시작");

        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println(req.getMethod() + " " + req.getRequestURI() + "\n");
        chain.doFilter(request, response);

        //System.out.println("PrintURLFilter doFileter() 끝\n");
    }

    @Override
    public void destroy() {
        System.out.println("PrintURLFilter destroy() ---------------------------------");
    }
}*/
