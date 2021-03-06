package org.qee.cloud.spring.processors;

import org.qee.cloud.common.model.URL;
import org.qee.cloud.rpc.api.RegistryCenterService;
import org.qee.cloud.spring.annotations.EnableCloud;
import org.qee.cloud.spring.log.LogHandlerAspect;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class CloudImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableCloud.class.getName()));
        String[] basePackages = annotationAttributes.getStringArray("scanPackages");
        BeanDefinitionBuilder serviceBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CloudServiceBeanPostProcessor.class);
        serviceBeanDefinitionBuilder.addPropertyValue("scanPackages", basePackages);
        AbstractBeanDefinition serviceBeanDefinition = serviceBeanDefinitionBuilder.getBeanDefinition();
        serviceBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinitionRegistry.registerBeanDefinition("cloudServiceBeanPostProcessor", serviceBeanDefinition);

        BeanDefinitionBuilder referenceBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CloudReferenceBeanPostProcessor.class);
        AbstractBeanDefinition referenceDefinition = referenceBeanDefinitionBuilder.getBeanDefinition();
        referenceDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        beanDefinitionRegistry.registerBeanDefinition("cloudReferenceBeanPostProcessor", referenceDefinition);
        String registryAddress = annotationAttributes.getString("registryAddress");
        URL url = URL.valueOf(registryAddress);
        url.addParameter("service.registry.protocol", annotationAttributes.getString("serviceRegistryProtocol"));
        url.addParameter("service.registry.port", annotationAttributes.get("serviceRegistryPort") + "");
        RegistryCenterService.addRegistryUrl(url);

        boolean openTraceLog = annotationAttributes.getBoolean("openTraceLog");
        if (openTraceLog) {
            BeanDefinitionBuilder logHandlerBuilder = BeanDefinitionBuilder.rootBeanDefinition(LogHandlerAspect.class);
            AbstractBeanDefinition logBeanDefinition = logHandlerBuilder.getBeanDefinition();
            logBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            beanDefinitionRegistry.registerBeanDefinition("logHandlerAspect", logBeanDefinition);
        }

    }
}
