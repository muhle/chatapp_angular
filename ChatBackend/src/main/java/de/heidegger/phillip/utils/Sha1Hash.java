package de.heidegger.phillip.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Goal of the class is to distingish hashes from normal String. This way,
 * we get Compile errors at all places were Strings are used as hashes.
 */
public class Sha1Hash {
    private static final Logger logger = Logger.getLogger(Sha1Hash.class.getName());
    private final String hash;

    public static Sha1Hash createFromHash(String hash) {
        if (hash == null) {
            throw new NullPointerException();
        }
        return new Sha1Hash(hash);
    }

    public static Sha1Hash createFromValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final String sha1;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(value.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.log(Level.WARNING, "Exception happens while computing sha1.", e);
            return null;
        }
        return new Sha1Hash(sha1);

    }

    public String getHash() {
        return hash;
    }

    // Never publish this constructor. Use static factory method.
    private Sha1Hash(String hash) {
        this.hash = hash;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sha1Hash sha1Hash = (Sha1Hash) o;

        return hash.equals(sha1Hash.hash);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }


}
