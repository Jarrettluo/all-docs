import com.jiaruiblog.config.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LdapProperties ldapProperties;

    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapProperties.getUrls());
        contextSource.setBase(ldapProperties.getBase());
        contextSource.setUserDn(ldapProperties.getUsername());
        contextSource.setPassword(ldapProperties.getPassword());
        return contextSource;
    }

    @Bean
    public LdapUserSearch ldapUserSearch(LdapContextSource contextSource) {
        return new FilterBasedLdapUserSearch(
                "ou=people", // Base DN for user searches
                "uid={0}", // Search filter
                contextSource
        );
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator(LdapContextSource contextSource) {
        return new DefaultLdapAuthoritiesPopulator(contextSource, ldapProperties.getGroupSearchBase());
    }

    @Bean
    public LdapUserDetailsService ldapUserDetailsService(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator) {
        return new LdapUserDetailsService(userSearch, authoritiesPopulator);
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator) {
        BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource());
        authenticator.setUserDnPatterns(ldapProperties.getUserDnPatterns());

        DefaultLdapAuthoritiesPopulator authoritiesPopulatorBean = new DefaultLdapAuthoritiesPopulator(ldapContextSource(), ldapProperties.getGroupSearchBase());

        return new LdapAuthenticationProvider(authenticator, authoritiesPopulatorBean);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .permitAll();
    }
}
