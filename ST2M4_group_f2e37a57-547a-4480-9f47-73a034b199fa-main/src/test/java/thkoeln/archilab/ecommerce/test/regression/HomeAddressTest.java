package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.Test;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker.*;

class HomeAddressTest {

    @Test
    public void testGetterHomeAddress() {
        // given
        String street = "Irgendeinestraße 42";
        String city = "Irgendeinestadt";
        ZipCodeType plz = instantiateZipCode( "12345" );

        // when
        HomeAddressType instance = instantiateHomeAddress( street, city, plz );

        // then
        assertEquals( street, instance.getStreet() );
        assertEquals( city, instance.getCity() );
        assertEquals( plz, instance.getZipCode() );
    }

    @Test
    public void testFactoryValidHomeAddress() {
        // given
        String street = "Irgendeinestraße 42";
        String city = "Irgendeinestadt";
        ZipCodeType plz = instantiateZipCode( "12345" );

        // when
        // then
        assertDoesNotThrow( () -> instantiateHomeAddress( street, city, plz ) );
    }

    @Test
    public void testFactoryInvalidHomeAddress() {
        // given
        String street = "Irgendeinestraße 42";
        String city = "Irgendeinestadt";
        ZipCodeType plz = instantiateZipCode( "12345" );

        // when
        // then
        assertThrows( ShopException.class, () -> instantiateHomeAddress( null, city, plz ) );
        assertThrows( ShopException.class, () -> instantiateHomeAddress( "", city, plz ) );
        assertThrows( ShopException.class, () -> instantiateHomeAddress( street, null, plz ) );
        assertThrows( ShopException.class, () -> instantiateHomeAddress( street, "", plz ) );
        assertThrows( ShopException.class, () -> instantiateHomeAddress( street, city, null ) );
    }

    @Test
    public void testEqualityHomeAddress() {
        // given
        String street = "Irgendeinestraße 42";
        String city = "Irgendeinestadt";
        ZipCodeType plz = instantiateZipCode( "12345" );

        // when
        HomeAddressType instance1 = instantiateHomeAddress( street, city, plz );
        HomeAddressType instance2 = instantiateHomeAddress( street, city, plz );
        HomeAddressType instance3 = instantiateHomeAddress( "Anderestr. 12", city, plz );
        HomeAddressType instance4 = instantiateHomeAddress( street, "AndereStadt", plz );
        HomeAddressType instance5 = instantiateHomeAddress( street, city, instantiateZipCode( "54321" ) );

        // then
        assertEquals( instance1, instance2 );
        assertNotEquals( instance1, instance3 );
        assertNotEquals( instance1, instance4 );
        assertNotEquals( instance1, instance5 );
    }

    @Test
    public void testImmutabilityHomeAddress() {
        // given
        String street = "Irgendeinestraße 42";
        String city = "Irgendeinestadt";
        ZipCodeType plz = instantiateZipCode( "12345" );

        // when
        HomeAddressType instance = instantiateHomeAddress( street, city, plz );

        // then
        try {
            instance.getClass().getMethod( "setStreet", String.class );
            fail( "setStreet method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
        try {
            instance.getClass().getMethod( "setCity", String.class );
            fail( "setCity method should not exist" );

        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
        try {
            instance.getClass().getMethod( "setZipCode", ZipCodeType.class );
            fail( "setZipCode method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
    }
}
