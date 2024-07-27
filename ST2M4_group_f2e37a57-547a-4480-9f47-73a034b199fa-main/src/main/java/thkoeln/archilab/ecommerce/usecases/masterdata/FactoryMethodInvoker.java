package thkoeln.archilab.ecommerce.usecases.masterdata;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;

public class FactoryMethodInvoker {

    public static EmailType instantiateEmail( String emailAsString ) {
        Method factoryMethod = null;
        try {
            Class<?> interfaceClass = Class.forName(
                    "thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType" );
            Class<?> implementingClass = findImplementation( interfaceClass );

            factoryMethod = implementingClass.getDeclaredMethod( "of", String.class );
            assertNotNull( factoryMethod );
            int modifiers = factoryMethod.getModifiers();
            assertTrue( Modifier.isStatic( modifiers ), "The method 'of' should be static" );
        } catch (Exception e) {
            fail( "Failed to find implementation for EmailType", e );
        }

        Object instance = null;
        try {
            instance = factoryMethod.invoke( null, emailAsString );
        } catch (Exception e) {
            if ( e instanceof InvocationTargetException && ( (InvocationTargetException) e ).getTargetException()
                    instanceof ShopException ) throw new ShopException( e.getMessage() );
            fail( "Failed to invoke factory method 'of' for '" + emailAsString + "'", e );
        }
        assertNotNull( instance );
        return (EmailType) instance;
    }


    public static HomeAddressType instantiateHomeAddress(
            String street, String city, ZipCodeType zipCode ) {
        Method factoryMethod = null;
        try {
            Class<?> interfaceClass = Class.forName(
                    "thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType" );
            Class<?> implementingClass = findImplementation( interfaceClass );

            factoryMethod = implementingClass.getDeclaredMethod( "of",
                    String.class, String.class, ZipCodeType.class );
            assertNotNull( factoryMethod );
            int modifiers = factoryMethod.getModifiers();
            assertTrue( Modifier.isStatic( modifiers ), "The method 'of' should be static" );
        } catch (Exception e) {
            fail( "Failed to find implementation for HomeAddressType", e );
        }

        Object instance = null;
        try {
            instance = factoryMethod.invoke( null, street, city, zipCode );
        } catch (Exception e) {
            if ( e instanceof InvocationTargetException && ( (InvocationTargetException) e ).getTargetException()
                    instanceof ShopException ) throw new ShopException( e.getMessage() );
            fail( "Failed to invoke factory method 'of' for '" +
                    street + "', '" + city + "', '" + zipCode + "'", e );
        }
        assertNotNull( instance );
        return (HomeAddressType) instance;
    }


    public static ZipCodeType instantiateZipCode( String zipCodeAsString ) {
        Method factoryMethod = null;
        try {
            Class<?> interfaceClass = Class.forName(
                    "thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType" );
            Class<?> implementingClass = findImplementation( interfaceClass );

            factoryMethod = implementingClass.getDeclaredMethod( "of", String.class );
            assertNotNull( factoryMethod );
            int modifiers = factoryMethod.getModifiers();
            assertTrue( Modifier.isStatic( modifiers ), "The method 'of' should be static" );
        } catch (Exception e) {
            fail( "Failed to find implementation for ZipCodeType", e );
        }

        Object instance = null;
        try {
            instance = factoryMethod.invoke( null, zipCodeAsString );
        } catch (Exception e) {
            if ( e instanceof InvocationTargetException && ( (InvocationTargetException) e ).getTargetException()
                    instanceof ShopException ) throw new ShopException( e.getMessage() );
            fail( "Failed to invoke factory method 'of' for '" + zipCodeAsString + "'", e );
        }
        assertNotNull( instance );
        return (ZipCodeType) instance;
    }


    public static MoneyType instantiateMoney( Float amount, String currency ) {
        Method factoryMethod = null;
        try {
            Class<?> interfaceClass = Class.forName(
                    "thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType" );
            Class<?> implementingClass = findImplementation( interfaceClass );

            factoryMethod = implementingClass.getDeclaredMethod( "of", Float.class, String.class );
            assertNotNull( factoryMethod );
            int modifiers = factoryMethod.getModifiers();
            assertTrue( Modifier.isStatic( modifiers ), "The method 'of' should be static" );
        } catch (Exception e) {
            fail( "Failed to find implementation for MoneyType", e );
        }

        Object instance = null;
        try {
            instance = factoryMethod.invoke( null, amount, currency );
        } catch (Exception e) {
            if ( e instanceof InvocationTargetException && ( (InvocationTargetException) e ).getTargetException()
                    instanceof ShopException ) throw new ShopException( e.getMessage() );
            fail( "Failed to invoke factory method 'of' for '" + amount + "', '" + currency + "'", e );
        }
        assertNotNull( instance );
        return (MoneyType) instance;
    }


    private static Class<?> findImplementation( Class<?> interfaceClass ) {
        Method factoryMethod = null;
        try {
            String packageName = "thkoeln.archilab.ecommerce.domainprimitives";
            Class<?> implementingClass = findImplementingClass( packageName, interfaceClass );
            assertNotNull( implementingClass );
            return implementingClass;
        } catch (Exception e) {
            fail( "Cannot find implementation for " + interfaceClass.getSimpleName(), e );
            return null; // This line will never be reached, but it's necessary to satisfy the compiler
        }
    }


    private static Class<?> findImplementingClass( String packageName, Class<?> interfaceClass )
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace( '.', '/' );
        Enumeration<URL> resources = classLoader.getResources( path );
        int count = 0;
        Class<?> result = null;
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File( URLDecoder.decode( resource.getFile(), StandardCharsets.UTF_8 ) );
            File[] files = directory.listFiles();
            for ( File file : files ) {
                if ( file.getName().endsWith( ".class" ) ) {
                    String className = packageName + '.' + file.getName().substring( 0, file.getName().length() - 6 );
                    Class<?> clazz = Class.forName( className );
                    if ( interfaceClass.isAssignableFrom( clazz ) && !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
                        count++;
                        result = clazz;
                    }
                }
            }
        }
        assertEquals( 1, count, "There should be exactly one implementing class" );
        return result;
    }
}
