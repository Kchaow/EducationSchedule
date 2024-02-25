package org.letunov;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@ComponentScan
public class Main
{
    public static void main(String[] args)
    {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    }
}