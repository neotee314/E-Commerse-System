package thkoeln.archilab.ecommerce.usecases.domainprimitivetypes;

import thkoeln.archilab.ecommerce.ShopException;

public interface ZipCodeType {
    /**
     * @return the zip code as a string
     */
    public String toString();

    /**
     * You will need some idea of "imprecise difference" between two zip codes for properly
     * implementing storage units in your shopping platform. This method calculates such a
     * difference. You can decide for yourself what values you return, unless you comply
     * with the following rules.
     * - The return value is 0 if both zip codes are the same
     * - If not:
     *      - The difference is > 0 if both zip codes differ in the last digit, like 5673x
     *        and 5673y (with x != y). However, the exact numbers for x and y don't matter.
     *        So, 56733 and 56734 have the same difference as 56733 and 56739.
     *      - The difference grows if more digits (counted from the right side) differ.
     *        So, 5abcd and 5rstu have a larger difference than 53bcd and 53stu (if abcd
     *        and rstu are not the same). Again, the precise numbers don't matter. Therefore,
     *        53876 and 54876 have the same difference as 53876 and 57261.
     *      - However, the difference between 5abcd and 6rstu must be smaller than the one
     *        between 5abcd and 7rstu, and this difference in turn must be smaller than the
     *        one between 5abcd and 9rstu.
     *      - This last condition reflects the fact the first digits of a zip code marks a region
     *        that is (usually) next to the region for an adjacent number. I.e. the "4" region
     *        is next to the "5" number. Same applies to "0" and "9", they are also next to each
     *        other.
     * @param otherZipCode
     * @return the calculated difference
     * @throws ShopException if otherZipCode is null
     */
    public int difference( ZipCodeType otherZipCode );


    /**
     * Unfortunately, Java interfaces cannot contain static methods. However, we expect the
     * implementing class to provide a static factory method (simply named "of(...)"),
     * which creates an zip code, given as a string.
     * We specify this factory method here as a comment, using the Javadoc documentation style.
     *
     * @param zipCodeAsString - the zip code as a string.
     *      We will use a much simplified validation method to check if the zip code is valid:
     *      - It must contain exactly 5 digits.
     *      - The last 4 digits must not be 0000 (i.e. 20000 is not a valid zip code, but 20001 is valid)
     * @return a new zip code object matching the given string
     * @throws ShopException if ...
     *      - zipCodeAsString is null
     *      - zipCodeAsString is not a valid zip code (see above)
     */
     // public static ZipCodeType of( String zipCodeAsString );
}
