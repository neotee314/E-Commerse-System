package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.Getter;
import lombok.NoArgsConstructor;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Money implements MoneyType {

    private float amount;
    private String currency;

    private static final String[] ALLOWED_CURRENCIES = {"EUR", "CHF"};

    private Money(Float amount, String currency)  {
        if (amount == null || amount < 0) {
            throw new ShopException("Amount must be non-null and >= 0");
        }
        if (currency == null || !isValidCurrency(currency)) {
            throw new ShopException("Currency must be one of the allowed values: EUR, CHF");
        }
        this.amount = amount;
        this.currency = currency;
    }

    private boolean isValidCurrency(String currency) {
        for (String allowedCurrency : ALLOWED_CURRENCIES) {
            if (allowedCurrency.equals(currency)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Float getAmount() {
        return amount;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public MoneyType add(MoneyType otherMoney)  {
        if (otherMoney == null) {
            throw new ShopException("other money cannot be null");
        }
        if (!this.currency.equals(otherMoney.getCurrency())) {
            throw new ShopException("Currency mismatch");
        }
        return new Money(this.amount + otherMoney.getAmount(), this.currency);
    }

    @Override
    public MoneyType subtract(MoneyType otherMoney) throws ShopException {
        if (otherMoney == null) {
            throw new ShopException("Other money cannot be null");
        }
        if (!this.currency.equals(otherMoney.getCurrency())) {
            throw new ShopException("Currency mismatch");
        }
        if (this.amount < otherMoney.getAmount()) {
            throw new ShopException("Cannot subtract more than the current amount");
        }
        return new Money(this.amount - otherMoney.getAmount(), this.currency);
    }

    @Override
    public MoneyType multiplyBy(int factor) throws ShopException {
        if (factor < 0) {
            throw new ShopException("Factor must be >= 0");
        }
        return new Money(this.amount * factor, this.currency);
    }

    @Override
    public boolean largerThan(MoneyType otherMoney) throws ShopException {
        if (otherMoney == null) {
            throw new ShopException("Other money cannot be null");
        }
        if (!this.currency.equals(otherMoney.getCurrency())) {
            throw new ShopException("Currency mismatch");
        }
        return this.amount > otherMoney.getAmount();
    }

    public static MoneyType of(Float amount, String currency) throws ShopException {
        return new Money(amount, currency);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || object.getClass() != getClass()) {
            return false;
        }
        Money moneyTypeImpType = (Money) object;
        return moneyTypeImpType.currency.equals(this.currency) && moneyTypeImpType.getAmount().equals(this.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
