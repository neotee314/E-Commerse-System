package thkoeln.archilab.ecommerce.usecases.domainprimitivetypes;

import thkoeln.archilab.ecommerce.ShopException;

public interface EmailType {
    /**
     * @return the email as a string
     */
    public String toString();


    /**
     * A special kind of "copy constructor": Returns a new email object with
     * the same identifyer (the part left of the "@" sign) as the current one, but
     * with a new domain part (right of the "@" sign).
     * @param domainString - the new domain for the copied email
     * @return the new email
     * @throws ShopException if ...
     *     - domainString is null
     *     - the new email would not be valid (see `of(...)` method)
     */
    public EmailType sameIdentifyerDifferentDomain( String domainString );

    /**
     * Another special kind of "copy constructor": Returns a new email object
     * with the same domain (the part right of the "@" sign) as the current one, but
     * with a new identifyer part (left of the "@" sign).
     * @param identifyerString - the new identifyer for the copied email
     * @return the new email
     * @throws ShopException if ...
     *     - identifyerString is null
     *     - the new email would not be valid (see `of(...)` method)
     */
    public EmailType sameDomainDifferentIdentifyer( String identifyerString );


    /**
     * Unfortunately, Java interfaces cannot contain static methods. However, we expect the
     * implementing class to provide a static factory method (simply named "of(...)"),
     * which creates an email, given as a string.
     * We specify this factory method here as a comment, using the Javadoc documentation style.
     *
     * @param emailAsString - the email as a string.
     *      We will use a much simplified validation method to check if the email is valid:
     *      - it must contain exactly one '@' character.
     *      - the part before the '@' and the part after the '@' must not be empty, contain of
     *        at least one of these characters (A..Z, a..z, or 0..9) and must not contain any whitespace characters
     *      - the parts before and after the '@' may contain one or several '.' as separators
     *      - two '.' characters must not be directly next to each other (so "test@this..example.com" is invalid)
     *      - the part after the '@' must end with ".de", ".at", ".ch", ".com", ".org"
     *        (for simplicity we do not allow any other domains)
     * @return a new EmailType object matching the given email
     * @throws ShopException if ...
     *      - emailAsString is null
     *      - emailAsString is not a valid email (see above)
     */
     // public static EmailType of( String emailAsString );
}
