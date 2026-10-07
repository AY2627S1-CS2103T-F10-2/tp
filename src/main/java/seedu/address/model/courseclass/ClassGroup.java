package seedu.address.model.courseclass;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a class group.
 * Guarantees: immutable; is valid as declared in {@link #isValidClassGroup(String)}.
 */
public class ClassGroup {

    public static final String MESSAGE_CONSTRAINTS =
            "Class groups should start with an uppercase letter, followed by two digits, and may end with -<digit>";
    public static final String VALIDATION_REGEX = "[A-Z]\\d{2}(?:-\\d)?";

    public final String value;

    /**
     * Constructs a {@code ClassGroup}.
     *
     * @param classGroup a valid class group
     */
    public ClassGroup(String classGroup) {
        requireNonNull(classGroup);
        checkArgument(isValidClassGroup(classGroup), MESSAGE_CONSTRAINTS);
        value = classGroup;
    }

    /**
     * Returns true if the given string is a valid class group.
     *
     * @param test string to validate
     * @return true if {@code test} is valid
     */
    public static boolean isValidClassGroup(String test) {
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

        if (!(other instanceof ClassGroup otherClassGroup)) {
            return false;
        }

        return value.equals(otherClassGroup.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
