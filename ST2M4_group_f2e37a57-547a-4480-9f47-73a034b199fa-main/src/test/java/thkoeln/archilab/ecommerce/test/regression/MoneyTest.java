package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.Test;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker.*;

class MoneyTest {

    @Test
    public void testGetAmountMoney() {
        // given
        Float inputAmount = 10.0f;

        // when
        MoneyType instance = instantiateMoney( inputAmount, "EUR" );

        // then
        assertEquals( inputAmount, instance.getAmount() );
    }

    @Test
    public void testGetCurrencyMoney() {
        // given
        String inputCurrency = "EUR";

        // when
        MoneyType instance = instantiateMoney( 10.0f, inputCurrency );

        // then
        assertEquals( inputCurrency, instance.getCurrency() );
    }

    @Test
    public void testAddToMoney() {
        // given
        MoneyType instance1 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 20.0f, "EUR" );
        MoneyType instance3 = instantiateMoney( 0.0f, "EUR" );

        // when
        MoneyType result12 = instance1.add( instance2 );
        MoneyType result13 = instance1.add( instance3 );

        // then
        assertEquals( 30.0f, result12.getAmount() );
        assertEquals( "EUR", result12.getCurrency() );
        assertEquals( 10.0f, result13.getAmount() );
        assertEquals( "EUR", result12.getCurrency() );
    }

    @Test
    public void testInvalidAddToMoney() {
        // given
        MoneyType instance1 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 20.0f, "CHF" );

        // when
        // then
        assertThrows( ShopException.class, () -> instance1.add( instance2 ) );
    }

    @Test
    public void testSubtractFromMoney() {
        // given
        MoneyType instance1 = instantiateMoney( 30.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 20.0f, "EUR" );
        MoneyType instance3 = instantiateMoney( 0.0f, "EUR" );
        MoneyType instance4 = instantiateMoney( 30.0f, "EUR" );

        // when
        MoneyType result12 = instance1.subtract( instance2 );
        MoneyType result13 = instance1.subtract( instance3 );
        MoneyType result14 = instance1.subtract( instance4 );

        // then
        assertEquals( 10.0f, result12.getAmount() );
        assertEquals( "EUR", result12.getCurrency() );
        assertEquals( 30.0f, result13.getAmount() );
        assertEquals( "EUR", result13.getCurrency() );
        assertEquals( 0.0f, result14.getAmount() );
        assertEquals( "EUR", result14.getCurrency() );
    }

    @Test
    public void testInvalidSubtractFromMoney() {
        // given
        MoneyType instance1 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 20.0f, "EUR" );
        MoneyType instance3 = instantiateMoney( 5.0f, "CHF" );

        // when
        // then
        assertThrows( ShopException.class, () -> instance1.subtract( instance2 ) );
        assertThrows( ShopException.class, () -> instance1.subtract( instance3 ) );
    }

    @Test
    public void testMultiplyByMoney() {
        // given
        MoneyType instance = instantiateMoney( 10.0f, "EUR" );

        // when
        MoneyType result = instance.multiplyBy( 3 );

        // then
        assertEquals( 30.0f, result.getAmount() );
        assertEquals( "EUR", result.getCurrency() );
    }

    @Test
    public void testFactoryValidMoney() {
        // given
        // when
        // then
        assertDoesNotThrow( () -> instantiateMoney( 10.0f, "EUR" ) );
    }

    @Test
    public void testFactoryInvalidMoney() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () -> instantiateMoney( -10.0f, "EUR" ) );
        assertThrows( ShopException.class, () -> instantiateMoney( 10.0f, "USD" ) );
        assertThrows( ShopException.class, () -> instantiateMoney( null, "EUR" ) );
        assertThrows( ShopException.class, () -> instantiateMoney( 10.0f, null ) );
    }

    @Test
    public void testLargerThanMoney() {
        // given
        MoneyType instance1 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 20.0f, "EUR" );
        MoneyType instance3 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance4 = instantiateMoney( 10.0f, "CHF" );

        // when
        // then
        assertTrue( instance2.largerThan( instance1 ) );
        assertFalse( instance1.largerThan( instance2 ) );
        assertFalse( instance1.largerThan( instance3 ) );
        assertThrows( ShopException.class, () -> instance1.largerThan( instance4 ) );
    }


    @Test
    public void testEqualityMoney() {
        // given
        // when
        MoneyType instance1 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance2 = instantiateMoney( 10.0f, "EUR" );
        MoneyType instance3 = instantiateMoney( 20.0f, "EUR" );

        // then
        assertEquals( instance1, instance2 );
        assertNotEquals( instance1, instance3 );
    }

    @Test
    public void testImmutabilityMoney() {
        // given
        // when
        MoneyType instance = instantiateMoney( 10.0f, "EUR" );

        // then
        try {
            instance.getClass().getMethod( "setAmount", Float.class );
            fail( "setAmount method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }

        try {
            instance.getClass().getMethod( "setCurrency", String.class );
            fail( "setCurrency method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
    }
}
