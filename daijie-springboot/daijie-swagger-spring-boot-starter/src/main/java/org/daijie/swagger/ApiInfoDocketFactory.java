package org.daijie.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.daijie.common.bean.RegisterBeanFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态注册swagger的Docket
 * 默认注册一个Docket
 * 通过配置中的swagger.groupName注册多个bean
 * @author daijie_jay
 * @since 2018年1月2日
 */
public abstract class ApiInfoDocketFactory implements DocketFactory, RegisterBeanFactory {
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		List<SwaggerProperties> properties = new ArrayList<SwaggerProperties>();
		docket(properties, registry);
		for (SwaggerProperties swaggerProperties : properties) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PackRewirteDocket.class);
			builder.addConstructorArgValue(DocumentationType.SWAGGER_2);
			builder.addPropertyValue("apiInfo", apiInfo(swaggerProperties));
			builder.addPropertyValue("groupName", swaggerProperties.getGroupName() == null ? Docket.DEFAULT_GROUP_NAME : swaggerProperties.getGroupName());
			builder.addPropertyValue("apiSelector", apiSelector(swaggerProperties));
			registerBean(swaggerProperties.getGroupName() + "Bean", builder, registry);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder()
		.title(swaggerProperties.getTitle())
		.description(swaggerProperties.getDescription())
		.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
		.contact(swaggerProperties.getContact())
		.version(swaggerProperties.getVersion())
		.build();
	}
	
	public ApiSelector apiSelector(SwaggerProperties swaggerProperties){
		Predicate<RequestHandler> requestHandlerSelectors = null;
		if(StringUtils.isNotEmpty(swaggerProperties.getBasePackage())){
			requestHandlerSelectors = RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage());
		}else{
			requestHandlerSelectors = ApiSelector.DEFAULT.getRequestHandlerSelector();
		}
		return new ApiSelector(Predicates.and(requestHandlerSelectors, transform(PathSelectors.any())), PathSelectors.any());
	}
	
	private Predicate<RequestHandler> transform(final Predicate<String> pathSelector) {
		return new Predicate<RequestHandler>() {
			@Override
			public boolean apply(RequestHandler input) {
				return Iterables.any(input.getPatternsCondition().getPatterns(), pathSelector);
			}
		};
	}
	
	@Override
	public void registerBean(String beanName, BeanDefinitionBuilder builder, BeanDefinitionRegistry registry){
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
	}
}
