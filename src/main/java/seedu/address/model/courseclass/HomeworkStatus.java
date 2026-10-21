package seedu.address.model.courseclass;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents the status of the homework assigned to an entire course class.
 */
public enum HomeworkStatus {
    /** Status values accepted by the homework command and persisted in class data. */
    NOT_SET("not-set"),
    PENDING("pending"),
    COMPLETED("completed");

    public static final String MESSAGE_CONSTRAINTS =
            "Homework status must be one of: not-set, pending, completed.";

    private final String serializedValue;

    HomeworkStatus(String serializedValue) {
        this.serializedValue = serializedValue;
    }

    /** Returns the stable value used when persisting this status. */
    public String getSerializedValue() {
        return serializedValue;
    }

    /**
     * Parses a persisted status value.
     *
     * <p>Enum-style values are accepted as well so that manually edited legacy data remains readable.</p>
     */
    public static Optional<HomeworkStatus> fromSerializedValue(String value) {
        if (value == null) {
            return Optional.empty();
        }

        String normalizedValue = value.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        return Arrays.stream(values())
                .filter(status -> status.serializedValue.equals(normalizedValue))
                .findFirst();
    }
}
