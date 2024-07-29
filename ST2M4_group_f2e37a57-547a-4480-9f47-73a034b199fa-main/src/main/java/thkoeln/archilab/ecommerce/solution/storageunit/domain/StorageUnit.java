package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.domainprimitives.ZipCode;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevel;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StorageUnit {
    @Id
    private UUID storageId = UUID.randomUUID();

    private String name;
    @Embedded
    private HomeAddress address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<StockLevel> stockLevels = new ArrayList<>();

    public StorageUnit(HomeAddressType address, String name) {
        this.address = (HomeAddress) address;
        this.name = name;
    }



}
