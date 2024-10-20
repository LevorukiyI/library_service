package com.modsensoftware.library_service.config.swagger;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;

@Configuration
public class SpringdocPreAuthorize {
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            Optional<PreAuthorize> preAuthorizeAnnotation =
                    Optional.ofNullable(handlerMethod.getMethodAnnotation(PreAuthorize.class));
            StringBuilder stringBuilder = new StringBuilder();
            if (preAuthorizeAnnotation.isPresent()) {
                stringBuilder.append("This api requires **")
                        .append((preAuthorizeAnnotation.get()).value().replaceAll("hasAuthority|\\(|\\)|\\'", ""))
                        .append("** authority.");
            } else {
                stringBuilder.append("This api is **public**");
            }
            stringBuilder.append("<br /><br />");
            stringBuilder.append(operation.getDescription() == null ? "" : operation.getDescription());
            operation.setDescription(stringBuilder.toString());
            return operation;
        };
    }
}
