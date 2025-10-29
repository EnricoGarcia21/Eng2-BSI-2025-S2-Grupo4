package DOARC.mvc.security;



import DOARC.mvc.security.AccessFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AccessFilter> accessFilter() {
        FilterRegistrationBean<AccessFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AccessFilter());
        registrationBean.addUrlPatterns("/api/*"); // Aplica a todas as APIs
        registrationBean.setOrder(1);
        return registrationBean;
    }
}