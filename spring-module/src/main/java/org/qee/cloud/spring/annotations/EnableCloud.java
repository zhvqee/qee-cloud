package org.qee.cloud.spring.annotations;

import org.qee.cloud.spring.processors.CloudImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CloudImportBeanDefinitionRegistrar.class)
public @interface EnableCloud {

    String[] scanPackages();
}
