package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's name.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}.
 */
public class StudentName {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain letters, spaces, accents, apostrophes, and hyphens, and should not be blank";

    /**
     * A name starts and ends with a letter, and may contain letters, combining marks, apostrophes, hyphens,
     * and single spaces between name parts.
     */
    public static final String VALIDATION_REGEX =
            "[\\p{L}](?:[\\p{L}\\p{M}'-]*[\\p{L}\\p{M}])?(?: [\\p{L}](?:[\\p{L}\\p{M}'-]*[\\p{L}\\p{M}])?)*";

    public final String fullName;

    /**
     * Constructs a {@code StudentName}.
     *
     * @param name a valid student name
     */
    public StudentName(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if the given string is a valid student name.
     *
     * @param test string to validate
     * @return true if {@code test} is valid
     */
    public static boolean isValidName(String test) {
        return test != null && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudentName otherStudentName)) {
            return false;
        }

        return fullName.equals(otherStudentName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
