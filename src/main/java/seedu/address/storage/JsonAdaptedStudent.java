package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

/** Jackson-friendly version of {@link Student}. */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String telehandle;

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     *
     * @param name student's name
     * @param telehandle student's telehandle
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name,
            @JsonProperty("telehandle") String telehandle) {
        this.name = name;
        this.telehandle = telehandle;
    }

    /** Converts a given {@code Student} into this class for Jackson use. */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        telehandle = source.getTelehandle().value;
    }

    /**
     * Converts this Jackson-friendly object into a {@code Student}.
     *
     * @return the validated student
     * @throws IllegalValueException if a field is missing or invalid
     */
    public Student toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    StudentName.class.getSimpleName()));
        }
        if (!StudentName.isValidName(name)) {
            throw new IllegalValueException(StudentName.MESSAGE_CONSTRAINTS);
        }

        if (telehandle == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    Telehandle.class.getSimpleName()));
        }
        if (!Telehandle.isValidTelehandle(telehandle)) {
            throw new IllegalValueException(Telehandle.MESSAGE_CONSTRAINTS);
        }

        return new Student(new StudentName(name), new Telehandle(telehandle));
    }
}
