package pl.kargolek.walletservice.exception;

/**
 * @author Karol Kuta-Orlowicz
 */
public class NoSuchCryptoPriceException extends RuntimeException{
    public NoSuchCryptoPriceException(String cryptoName) {
        super(String.format("Unable to get price for crypto: %s", cryptoName));
    }
}
