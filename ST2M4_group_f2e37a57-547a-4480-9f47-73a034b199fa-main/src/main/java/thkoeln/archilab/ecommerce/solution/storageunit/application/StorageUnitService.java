package thkoeln.archilab.ecommerce.solution.storageunit.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnitRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitService {

    private final StorageUnitRepository storageUnitRepository;

    public StorageUnit findById(UUID storageUnitId) {
        Optional<StorageUnit> storageUnitOptional = storageUnitRepository.findById(storageUnitId);

        if (storageUnitOptional.isEmpty())
            throw new ShopException("Storage with Id " + storageUnitId + " does not exist");
        return storageUnitOptional.get();

    }

    public List<StorageUnit> findAll() {
        List<StorageUnit> storageUnits = new ArrayList<>();
        storageUnitRepository.findAll().forEach(storageUnits::add);
        return storageUnits;
    }


}
