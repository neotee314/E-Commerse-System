package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class HomeAddress implements HomeAddressType {

    @Embedded
    private ZipCode zipCode;
    private String street;
    private String city;


    private HomeAddress(String street, String city, ZipCodeType zipCode)  {
        if (street == null || street.trim().isEmpty() || city == null || city.trim().isEmpty() ||zipCode == null) {
            throw new ShopException("invalid inputs");
        }

        this.street = street;
        this.city = city;
        this.zipCode = (ZipCode) zipCode;
    }

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public ZipCodeType getZipCode() {
        return zipCode;
    }


    public static HomeAddressType of(String street, String city, ZipCodeType zipCode)  {
        if (zipCode == null) {
            throw new ShopException("invalid zipcode");
        }
        return new HomeAddress(street, city,  zipCode);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HomeAddressType homeAddressType = (HomeAddressType) obj;
        return  city.equals(homeAddressType.getCity()) && street.equals(homeAddressType.getStreet())
                && getZipCode().equals(homeAddressType.getZipCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode);
    }
}
