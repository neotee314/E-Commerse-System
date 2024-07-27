package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class ZipCode implements ZipCodeType {

    private String zipCode;

    private ZipCode(String zipCode)  {

        this.zipCode = zipCode;
    }
    public static ZipCodeType of(String zipCode) {
        if (zipCode == null || !isValidZipCode(zipCode)) {
            throw new ShopException("Invalid zip code");
        }
        return new ZipCode(zipCode);
    }

    private static boolean isValidZipCode(String zipCode) {
        if (zipCode.length() != 5) {
            return false;
        }
        if (!zipCode.matches("\\d{5}")) {
            return false;
        }
        return !zipCode.endsWith("0000");
    }

    @Override
    public String toString() {
        return zipCode;
    }

    @Override
    public int difference(ZipCodeType otherZipCode) throws ShopException {
        if (otherZipCode == null) {
            throw new ShopException("otherZipCode cannot be null");
        }

        String zip1 = this.zipCode;
        String zip2 = otherZipCode.toString();

        if (zip1.equals(zip2)) {
            return 0;
        }

        int length = Math.min(zip1.length(), zip2.length());
        int difference = 0;

        // Calculate differences from the right
        for (int i = 1; i <= length; i++) {
            char char1 = zip1.charAt(zip1.length() - i);
            char char2 = zip2.charAt(zip2.length() - i);

            if (char1 != char2) {
                difference += 1;
            }
        }

        // Consider regional proximity for the first digit
        if (zip1.length() > 0 && zip2.length() > 0) {
            char firstChar1 = zip1.charAt(0);
            char firstChar2 = zip2.charAt(0);

            if (firstChar1 != firstChar2) {
                int regionDifference = Math.min(Math.abs(firstChar1 - firstChar2), 10 - Math.abs(firstChar1 - firstChar2));
                difference += regionDifference * 10; // Scale it to reflect regional importance
            }
            if ( firstChar1 == firstChar2 && zip1.charAt(1)!=zip2.charAt(1)) {
                // If only the first digit differs, return 5
                return 5;
            }
        }

        return difference;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ZipCode other = (ZipCode) obj;
        return zipCode.equals(other.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode);
    }
}
