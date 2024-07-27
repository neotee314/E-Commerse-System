package thkoeln.archilab.ecommerce.usecases.masterdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.ClientRegistrationUseCases;
import thkoeln.archilab.ecommerce.usecases.ClientType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This is a test helper class that initializes and registers clients in the system,
 * using the given interface(s).
 */
@Component
@Slf4j
@SuppressWarnings("PMD")
public class ClientMasterDataInitializer {

    private ClientRegistrationUseCases clientRegistrationUseCases;
    private Random random = new Random();

    public static final String EUR = "EUR";

    @Autowired
    public ClientMasterDataInitializer( ClientRegistrationUseCases clientRegistrationUseCases ) {
        this.clientRegistrationUseCases = clientRegistrationUseCases;
    }

    public final static String[] CLIENT_NAME = new String[]{
            "Max Müller",
            "Sophie Schmitz",
            "Irene Mihalic",
            "Emilia Fischer",
            "Filiz Polat",
            "Lina Wagner",
            "Leon Becker",
            "Agnieszka Kalterer",
            "Felix Bauer",
            "Lara Schulz"
    };

    public final static EmailType[] CLIENT_EMAIL = new EmailType[]{
            FactoryMethodInvoker.instantiateEmail( "99Z@example.com" ),
            FactoryMethodInvoker.instantiateEmail( "a@4.com" ),
            FactoryMethodInvoker.instantiateEmail( "irene@wearefreedomnow.com" ),
            FactoryMethodInvoker.instantiateEmail( "emilia.fischer@example.com" ),
            FactoryMethodInvoker.instantiateEmail( "j877d3@example.this.com" ),
            FactoryMethodInvoker.instantiateEmail( "lina.marie.wagner@example.com" ),
            FactoryMethodInvoker.instantiateEmail( "0.1.2.3.4.5.6.7.8.becker@example.com" ),
            FactoryMethodInvoker.instantiateEmail( "agna@here.ch" ),
            FactoryMethodInvoker.instantiateEmail( "felix.bauer@example.org" ),
            FactoryMethodInvoker.instantiateEmail( "lara.schulz@example.at" )
    };

    // The following array is used to create a list of homeAddresss for the clients.
    // The indices are coded into the house number.
    // - Persons 0 - 5 are used for proximity tests with one delivery package.
    public final static HomeAddressType[] CLIENT_ADDRESS = new HomeAddressType[]{
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Marktstraße 0", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02314" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Hauptstraße 1", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02368" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Kirchplatz 2", "Niemandstown",
                    FactoryMethodInvoker.instantiateZipCode( "12345" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Schulstraße 3", "Geisterhausen",
                    FactoryMethodInvoker.instantiateZipCode( "31463" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Rosenweg 4", "Kuhhausen",
                    FactoryMethodInvoker.instantiateZipCode( "72162" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Wiesenstraße 5", "Waldschenkensdorf",
                    FactoryMethodInvoker.instantiateZipCode( "82195" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Mühlenweg 6", "Köln",
                    FactoryMethodInvoker.instantiateZipCode( "50667" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Goethestraße 7", "Frankfurt am Main",
                    FactoryMethodInvoker.instantiateZipCode( "60311" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Dorfstraße 8", "Stuttgart",
                    FactoryMethodInvoker.instantiateZipCode( "70173" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Bahnhofstraße 9", "München",
                    FactoryMethodInvoker.instantiateZipCode( "80331" ) )
    };

    public final static ClientType[] mockClients;

    static {
        mockClients = new ClientType[CLIENT_NAME.length];
        for ( int i = 0; i < CLIENT_NAME.length; i++ ) {
            mockClients[i] = new MockClient(
                    CLIENT_NAME[i], CLIENT_EMAIL[i], CLIENT_ADDRESS[i] );
        }
        // TODO - remove and replace by current _norepo, in M3
        assertThrows( ShopException.class, () -> {
            new MockClient( "No one",
                    FactoryMethodInvoker.instantiateEmail( null ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );
        assertThrows( ShopException.class, () -> {
            new MockClient( "No one",
                    FactoryMethodInvoker.instantiateEmail( "Max..Gideon.Hammer@example.com" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!" );
        assertThrows( ShopException.class, () -> {
            new MockClient( "No one",
                    FactoryMethodInvoker.instantiateEmail( "test@example.42" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );
        assertThrows( ShopException.class, () -> {
            new MockClient( "No one",
                    FactoryMethodInvoker.instantiateEmail( "test@" ),
                    CLIENT_ADDRESS[0] ); }, "Check your email validation!"  );
    }


    public void registerAllClients() {
        log.info( "Registering clients.");
        for ( int i = 0; i < CLIENT_NAME.length; i++ ) {
            registerClient( CLIENT_NAME[i], CLIENT_EMAIL[i], CLIENT_ADDRESS[i] );
        }
    }

    public void registerClient( String name, EmailType email, HomeAddressType homeAddress ) {
        log.info( "Registering client with email " + email );
        clientRegistrationUseCases.register( name, email, homeAddress );
    }
}
