package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.Test;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker.instantiateEmail;

class EmailTest {

    @Test
    public void testToStringEmail() {
        // given
        String input = "test@example.com";

        // when
        EmailType instance = instantiateEmail( input );

        // then
        assertEquals( input, instance.toString() );
    }

    @Test
    public void testEqualityEmail() {
        // given
        // when
        EmailType instance1 = instantiateEmail( "test@example.com" );
        EmailType instance2 = instantiateEmail( "test@example.com" );
        EmailType instance3 = instantiateEmail( "different@example.com" );

        // then
        assertEquals( instance1, instance2 );
        assertNotEquals( instance1, instance3 );
    }

    @Test
    public void testImmutabilityEmail() {
        // given
        // when
        EmailType instance = instantiateEmail( "test@example.com" );

        // then
        try {
            instance.getClass().getMethod( "setEmail", String.class );
            fail( "setEmail method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
    }

    @Test
    public void testFactoryValidEmail() {
        // given
        // when
        // then
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "99Z@example.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "GGGhh@s77.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "a@4.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "Max.Hammer@example.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "Max.Gideon.Hammer@example.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.this.com" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.de" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.at" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.ch" ) );
        assertDoesNotThrow( () -> FactoryMethodInvoker.instantiateEmail( "test@example.org" ) );
    }

    @Test
    public void testFactoryInvalidEmail() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( null ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "testexample.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "@example.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "Max..Gideon.Hammer@example.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@examplecom" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@example..com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@example@that.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test example@that.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test#example@that.com" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@example.biz" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@example.biz" ) );
        assertThrows( ShopException.class, () -> FactoryMethodInvoker.instantiateEmail( "test@example.42" ) );
    }

}
