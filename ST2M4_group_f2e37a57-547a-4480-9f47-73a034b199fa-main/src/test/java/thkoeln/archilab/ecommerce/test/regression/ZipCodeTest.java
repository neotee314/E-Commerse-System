package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker.*;

class ZipCodeTest {

    @Test
    public void testToStringZipCode() {
        // given
        String input = "12345";

        // when
        ZipCodeType instance = instantiateZipCode( input );

        // then
        assertEquals( input, instance.toString() );
    }

    @Test
    public void testFactoryValidZipCode() {
        // given
        // when
        // then
        assertDoesNotThrow( () -> instantiateZipCode( "12345" ) );
    }

    @Test
    public void testFactoryInvalidZipCode() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () -> instantiateZipCode( "123456" ) );
        assertThrows( ShopException.class, () -> instantiateZipCode( "1234" ) );
        assertThrows( ShopException.class, () -> instantiateZipCode( "20000" ) );
        assertThrows( ShopException.class, () -> instantiateZipCode( null ) );
    }

    @Test
    public void testEqualityZipCode() {
        // given
        // when
        ZipCodeType instance1 = instantiateZipCode( "12345" );
        ZipCodeType instance2 = instantiateZipCode( "12345" );
        ZipCodeType instance3 = instantiateZipCode( "54321" );

        // then
        assertEquals( instance1, instance2 );
        assertNotEquals( instance1, instance3 );
    }

    @Test
    public void testImmutabilityZipCode() {
        // given
        // when
        ZipCodeType instance = instantiateZipCode( "12345" );

        // then
        try {
            instance.getClass().getMethod( "setZipCode", String.class );
            fail( "setZipCode method should not exist" );
        } catch (NoSuchMethodException e) {
            // Success: the object is immutable
        }
    }


    private ZipCodeType _12345, _12345b, _23455, _01234, _01235, _01238, _01263, _01213, _01823, _01312, _04233, _02544,
            _99123, _56323, _79332;


    @BeforeEach
    public void setUp() {
        _12345 = instantiateZipCode( "12345" );
        _12345b = instantiateZipCode( "12345" );
        _23455 = instantiateZipCode( "23455" );
        _01234 = instantiateZipCode( "01234" );
        _01235 = instantiateZipCode( "01235" );
        _01238 = instantiateZipCode( "01238" );
        _01263 = instantiateZipCode( "01263" );
        _01213 = instantiateZipCode( "01213" );
        _01823 = instantiateZipCode( "01823" );
        _01312 = instantiateZipCode( "01312" );
        _04233 = instantiateZipCode( "04233" );
        _02544 = instantiateZipCode( "02544" );
        _99123 = instantiateZipCode( "99123" );
        _56323 = instantiateZipCode( "56323" );
        _79332 = instantiateZipCode( "79332" );
    }

    @Test
    public void testInvalidParameters() {
        // given
        // when
        // then
        assertThrows( ShopException.class, () ->
                _12345.difference( null ) );
    }


    @Test
    public void testSameZipCodeType() {
        // given
        // when
        int diff = _12345.difference( _12345b );

        // then
        assertEquals( 0, diff );
    }


    /**
     *      - The difference is > 0 if both zip codes differ in the last digit, like 5673x
     *        and 5673y (with x != y). However, the exact numbers for x and y don't matter.
     *        So, 56733 and 56734 have the same difference as 56733 and 56739.
     *      - The difference grows if more digits (counted from the right side) differ.
     *        So, 5abcd and 5rstu have a larger difference than 53bcd and 53stu (if abcd
     *        and rstu are not the same). Again, the precise numbers don't matter. Therefore,
     *        53876 and 54876 have the same difference as 53876 and 57261.
     */
    @Test
    public void testZipCodeTypedifferenceInAllButFirstDigit() {
        // given
        // when
        int diff5a = _01234.difference( _01235 );
        int diff5b = _01234.difference( _01238 );
        int diff4a = _01234.difference( _01263 );
        int diff4b = _01234.difference( _01213 );
        int diff3a = _01234.difference( _01823 );
        int diff3b = _01234.difference( _01312 );
        int diff2a = _01234.difference( _04233 );
        int diff2b = _01234.difference( _02544 );

        // then
        assertEquals( diff5a, diff5b, "Difference 01234-01235 must be the same as difference 01234-01238" );
        assertEquals( diff4a, diff4b, "Difference 01234-01263 must be the same as difference 01234-01213" );
        assertEquals( diff3a, diff3b, "Difference 01234-01823 must be the same as difference 01234-01312" );
        assertEquals( diff2a, diff2b, "Difference 01234-04233 must be the same as difference 01234-02544" );

        assertTrue( "Difference 01234-01235 must be smaller than difference 01234-01263", diff5a < diff4a );
        assertTrue( "Difference 01234-01263 must be smaller than difference 01234-01823", diff4a < diff3a );
        assertTrue( "Difference 01234-01823 must be smaller than difference 01234-04233", diff3a < diff2a );
    }


    /**
     *      - However, the difference between 5abcd and 6rstu must be smaller than the one
     *        between 5abcd and 7rstu, and this difference in turn must be smaller than the
     *        one between 5abcd and 9rstu.
     *      - This last condition reflects the fact the first digits of a zip code marks a region
     *        that is (usually) next to the region for an adjacent number. I.e. the "4" region
     *        is next to the "5" number. Same applies to "0" and "9", they are also next to each
     *        other.
     */
    @Test
    public void testZipCodeTypedifferenceInFirstDigit() {
        // given
        // when
        int diff1_1 = _01234.difference( _99123 );
        int diff1_2 = _01234.difference( _23455 );
        int diff1_5 = _01234.difference( _56323 );
        int diff1_3 = _01234.difference( _79332 );

        // then
        assertTrue( "Difference 01234-99123 must be smaller than difference 01234-23455", diff1_1 < diff1_2 );
        assertTrue( "Difference 01234-23455 must be smaller than difference 01234-79332", diff1_2 < diff1_3 );
        assertTrue( "Difference 01234-79332 must be smaller than difference 01234-56323", diff1_3 < diff1_5 );
    }
}
