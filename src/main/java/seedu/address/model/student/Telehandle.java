package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's telehandle.
 * Guarantees: immutable; contains 5 to 32 non-whitespace characters and does not start with {@code @}.
 */
public class Telehandle {

    public static final String MESSAGE_CONSTRAINTS =
            "Telehandles should contain 5 to 32 characters, contain no spaces, and should not start with @";
    public static final String VALIDATION_REGEX = "(?!@)[^\\s]{5,32}";

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

        return value.equals(otherTelehandle.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
