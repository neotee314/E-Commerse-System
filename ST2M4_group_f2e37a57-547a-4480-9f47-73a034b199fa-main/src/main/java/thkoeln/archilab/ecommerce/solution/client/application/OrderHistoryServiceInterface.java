package thkoeln.archilab.ecommerce.solution.client.application;


import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import java.util.Map;
import java.util.UUID;

public interface OrderHistoryServiceInterface {
    Map<UUID, Integer> getOrderHistory(EmailType clientEmail);
}
