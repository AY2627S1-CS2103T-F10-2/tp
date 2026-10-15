package seedu.address.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Note;

/** Jackson-friendly version of {@link Note}. */
class JsonAdaptedNote {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Note's %s field is missing!";
    public static final String INVALID_TIMESTAMP_MESSAGE = "Note timestamp is not a valid date and time.";

    private final String text;
    private final String timestamp;

    /** Creates an adapted note from its JSON fields. */
    @JsonCreator
    public JsonAdaptedNote(@JsonProperty("text") String text,
            @JsonProperty("timestamp") String timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    /** Converts a given {@code Note} into this class for Jackson use. */
    public JsonAdaptedNote(Note source) {
        text = source.getText();
        timestamp = source.getTimestamp().toString();
    }

    /**
     * Converts this Jackson-friendly object into a {@code Note}.
     *
     * @return the validated note
     * @throws IllegalValueException if a field is missing or invalid
     */
    public Note toModelType() throws IllegalValueException {
        if (text == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "text"));
        }
        if (!Note.isValidText(text)) {
            throw new IllegalValueException(Note.MESSAGE_CONSTRAINTS);
        }

        if (timestamp == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "timestamp"));
        }

        try {
            return new Note(text, LocalDateTime.parse(timestamp));
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(INVALID_TIMESTAMP_MESSAGE, e);
        }
    }
}
