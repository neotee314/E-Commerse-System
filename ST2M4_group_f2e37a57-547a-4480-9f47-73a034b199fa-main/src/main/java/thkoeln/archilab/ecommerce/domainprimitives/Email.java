package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;


@Getter
@NoArgsConstructor
@Embeddable
public class Email implements EmailType {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String[] VALID_DOMAINS = {".de", ".at", ".ch", ".com", ".org"};
    private  String emailAddress;

    private Email(String emailAddress) {
        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new ShopException("Invalid email format");
        }
        if (!EMAIL_PATTERN.matcher(emailAddress).matches()) {
            throw new ShopException("Invalid email format");
        }
        String[] parts = emailAddress.split("@");
        if (parts.length != 2) {
            throw new ShopException("Invalid email format");
        }
        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty() || domainPart.isEmpty() || localPart.contains(" ") || domainPart.contains(" ")) {
            throw new ShopException("Invalid email format");
        }
        if (localPart.contains("..") || domainPart.contains("..")) {
            throw new ShopException("Invalid email format");
        }
        boolean validDomain = false;
        for (String domain : VALID_DOMAINS) {
            if (domainPart.endsWith(domain)) {
                validDomain = true;
                break;
            }
        }

        if ( !validDomain) {
            throw new ShopException("Invalid email format");
        }
        this.emailAddress = emailAddress;
    }

    public static Email of(String emailAsString) {
        return new Email(emailAsString);
    }



    @Override
    public EmailType sameIdentifyerDifferentDomain(String domainString) {
        if (domainString == null) {
            throw new ShopException("Domain cannot be null");
        }
        String[] parts = this.emailAddress.split("@");
        String newEmail = parts[0] + "@" + domainString;
        return new Email(newEmail);
    }

    @Override
    public EmailType sameDomainDifferentIdentifyer(String identifyerString) {
        if (identifyerString == null) {
            throw new ShopException("Identifyer cannot be null");
        }
        String[] parts = this.emailAddress.split("@");
        String newEmail = identifyerString + "@" + parts[1];
        return new Email(newEmail);
    }

    @Override
    public String toString() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Email other = (Email) obj;
        return emailAddress.equals(other.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }
}
