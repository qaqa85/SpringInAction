package tacos.integration;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.dsl.Mail;

@Configuration
@EnableIntegration
public class TacoOrderEmailIntegrationConfig {

    @Bean
    public IntegrationFlow tacoOrderEmailFlow(
            EmailProperties emailProps,
            EmailToOrderTransformer emailToOrderTransformer,
            OrderLoggerHandler orderSubmitHandler) {
        return IntegrationFlow
                .from(Mail.imapInboundAdapter(emailProps.getImapUrl())
                        .javaMailProperties(p -> p.put("mail.imap.folder", "INBOX"))
                        .simpleContent(true)
                                .javaMailAuthenticator(new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(emailProps.getUsername(), emailProps.getPassword());
                                    }}),
                        e -> e.poller(
                                Pollers.fixedDelay(emailProps.getPoolRate())))
                .transform(emailToOrderTransformer)
                .handle(orderSubmitHandler)
                .get();
    }

}
