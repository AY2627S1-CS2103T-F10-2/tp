package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a timestamped note about a student enrolment.
 * Guarantees: immutable; text is valid as declared in {@link #isValidText(String)}.
 */
public class Note {

    public static final String MESSAGE_CONSTRAINTS = "Notes must contain at least one visible character";

    private final String text;
    private final LocalDateTime timestamp;

    /**
     * Constructs a {@code Note}.
     *
     * @param text note text
     * @param timestamp date and time at which the note was recorded
     */
    public Note(String text, LocalDateTime timestamp) {
        requireNonNull(text);
        requireNonNull(timestamp);
        checkArgument(isValidText(text), MESSAGE_CONSTRAINTS);
        this.text = text;
        this.timestamp = timestamp;
    }

    /** Returns the note text. */
    public String getText() {
        return text;
    }

    /** Returns the date and time at which the note was recorded. */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** Returns true if the given text contains at least one visible character. */
    public static boolean isValidText(String test) {
        return test != null && test.codePoints().anyMatch(Note::isVisibleCharacter);
    }

    /** Returns true if {@code codePoint} represents visible note content. */
    private static boolean isVisibleCharacter(int codePoint) {
        return !Character.isWhitespace(codePoint)
                && !Character.isSpaceChar(codePoint)
                && !Character.isISOControl(codePoint)
                && Character.getType(codePoint) != Character.FORMAT;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return text.equals(otherNote.text) && timestamp.equals(otherNote.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, timestamp);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("text", text)
                .add("timestamp", timestamp)
                .toString();
    }
}
