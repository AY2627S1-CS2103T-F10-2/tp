package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a student's telehandle.
 * Guarantees: immutable; contains 5 to 32 ASCII letters, digits, or underscores.
 * Equality ignores case; the original capitalisation is preserved for display and storage.
 */
public class Telehandle {

    public static final String MESSAGE_CONSTRAINTS =
            "Telehandles should contain 5 to 32 characters, using only letters (A-Z, a-z), digits, and underscores";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9_]{5,32}";

    public final String value;

    /**
     * Constructs a {@code Telehandle}.
     *
     * @param telehandle a valid telehandle
     */
    public Telehandle(String telehandle) {
        requireNonNull(telehandle);
        checkArgument(isValidTelehandle(telehandle), MESSAGE_CONSTRAINTS);
        value = telehandle;
    }

    /**
     * Returns true if the given string is a valid telehandle.
     *
     * @param test string to validate
     * @return true if {@code test} is valid
     */
    public static boolean isValidTelehandle(String test) {
        return test != null && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Telehandle otherTelehandle)) {
            return false;
        }

        return value.equalsIgnoreCase(otherTelehandle.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase(Locale.ROOT).hashCode();
    }
}
