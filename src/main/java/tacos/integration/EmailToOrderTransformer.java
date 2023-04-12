package tacos.integration;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mail.transformer.AbstractMailMessageTransformer;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailToOrderTransformer extends AbstractMailMessageTransformer<EmailOrder> {
    private static Logger log = LoggerFactory.getLogger(EmailToOrderTransformer.class);
    private static final String SUBJECT_KEYWORDS = "TACO ORDER";

    @Override
    protected AbstractIntegrationMessageBuilder<EmailOrder> doTransform(Message mailMessage) {
        EmailOrder emailOrder = processPayload(mailMessage);
        return new DefaultMessageBuilderFactory().withPayload(emailOrder);
    }

    private EmailOrder processPayload(Message mailMessage) {
        try  {
            String subject = mailMessage.getSubject();
            if (subject.toUpperCase().contains(SUBJECT_KEYWORDS)) {
                String email = ((InternetAddress) mailMessage.getFrom()[0]).getAddress();
                int count =  ((MimeMultipart) mailMessage.getContent()).getCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    BodyPart bodyPart = ((MimeMultipart) mailMessage.getContent()).getBodyPart(i);
                    if (bodyPart.getContentType().toLowerCase().startsWith("text/plain")) {
                        return parseEmailToOrder(email, bodyPart.getContent().toString());
                    }
                }
            }
        } catch (MessagingException e) {
            log.error("MessagingException: {%}", e);
        } catch (IOException e) {
            log.error("IOException: {%}", e);
        }

        return null;
    }

    private EmailOrder parseEmailToOrder(String email, String content) {
        EmailOrder emailOrder = new EmailOrder(email);
        String[] lines = content.split("\\r?\\n");
        for (int i = 0; i < lines.length / 2; i++) {
            if (lines[0].trim().length() > 0) {
                String tacoName = lines[0].trim();
                String ingredients = lines[1].trim();
                String[] ingredientsSplit = ingredients.split(",");
                List<String> ingredientsCodes = new ArrayList<>();
                for (String ingredientName : ingredientsSplit) {
                    String code = lookupIngredientCode(ingredientName.trim());
                    if (code != null) {
                        ingredientsCodes.add(code);
                    }
                }

                Taco taco = new Taco(tacoName);
                taco.setIngredients(ingredientsCodes);
                emailOrder.addTaco(taco);
            }
        }
        return emailOrder;
    }

    private String lookupIngredientCode(String ingredientName) {
        for (Ingredient ingredient : ALL_INGREDIENTS) {
            String ucIngredientName = ingredientName.toUpperCase();
            if (LevenshteinDistance.getDefaultInstance().apply(ucIngredientName, ingredient.name()) < 3 ||
            ucIngredientName.contains(ingredient.name()) ||
                    ingredient.name().contains(ucIngredientName)) {
                return ingredient.code();
            }
        }
        return null;
    }

    private static Ingredient[] ALL_INGREDIENTS = new Ingredient[] {
            new Ingredient("FLTO", "FLOUR TORTILLA"),
            new Ingredient("COTO", "CORN TORTILLA"),
            new Ingredient("GRBF", "GROUND BEEF"),
            new Ingredient("CARN", "CARNITAS"),
            new Ingredient("TMTO", "TOMATOES"),
            new Ingredient("LETC", "LETTUCE"),
            new Ingredient("CHED", "CHEDDAR"),
            new Ingredient("JACK", "MONTERREY JACK"),
            new Ingredient("SLSA", "SALSA"),
            new Ingredient("SRCR", "SOUR CREAM")
    };
}
