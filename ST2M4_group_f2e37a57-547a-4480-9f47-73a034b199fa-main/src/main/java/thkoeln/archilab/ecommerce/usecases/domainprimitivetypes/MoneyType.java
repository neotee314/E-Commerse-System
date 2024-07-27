package thkoeln.archilab.ecommerce.usecases.domainprimitivetypes;

import thkoeln.archilab.ecommerce.ShopException;

public interface MoneyType {
    /**
     * @return the amount of money
     */
    public Float getAmount();


    /**
     * @return the currency of the money
     */
    public String getCurrency();


    /**
     * @param otherMoney
     * @return this + otherMoney, as a new object
     * @throws ShopException if ...
     *      - otherMoney is null
     *      - otherMoney.currency != this.currency
     */
    public MoneyType add( MoneyType otherMoney );


    /**
     * @param otherMoney
     * @return this - otherMoney, as a new object
     * @throws ShopException if ...
     *      - otherMoney is null
     *      - otherMoney.currency != this.currency
     *      - otherMoney > this
     */
    public MoneyType subtract( MoneyType otherMoney );


    /**
     * @param factor
     * @return this * factor, as a new object
     * @throws ShopException if ...
     *     - factor < 0
     */
    public MoneyType multiplyBy( int factor );

    /**
     * @param otherMoney
     * @return true, if this > otherMoney
     * @throws ShopException if ...
     *      - otherMoney is null
     *      - otherMoney.currency != this.currency
     */
    public boolean largerThan( MoneyType otherMoney );


    /**
     * Unfortunately, Java interfaces cannot contain static methods. However, we expect the
     * implementing class to provide a static factory method (simply named "of(...)"),
     * which creates a money object from an amount and a currency (as string).
     * We specify this factory method here as a comment, using the Javadoc documentation style.
     *
     * @param amount the amount of money (must be >= 0)
     * @param currency the currency of the money (allowed values: "EUR", "CHF")
     * @return a new Money object with the given amount and currency
     * @throws ShopException if ...
     *   - amount is null
     *   - amount < 0
     *   - currency is null
     *   - currency is not one of the allowed values
     */
     // public static MoneyType of( Float amount, String currency );
}
