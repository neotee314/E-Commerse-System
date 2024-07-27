package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

/**
 * This interface expresses the essence of a shop client
 */
public interface ClientType {
    public String getName();
    public EmailType getEmail();
    public HomeAddressType getHomeAddress();
}
