package tacos.integration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "tacocloud.email")
@Component
public class EmailProperties {
    private String username;
    private String password;
    private String host;
    private String mailbox;
    private long poolRate = 30000;

    public String getImapUrl() {
        return "imaps://imap.gmail.com:993/INBOX";
    }
}
