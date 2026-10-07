package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.ReadOnlyCourseClassBook;
import seedu.address.model.courseclass.CourseClass;

/**
 * An Immutable CourseClassBook that is serializable to JSON format.
 */
@JsonRootName(value = "courseclassbook")
class JsonSerializableCourseClassBook {

    public static final String MESSAGE_DUPLICATE_COURSE_CLASS =
            "Course classes list contains duplicate course class(es).";

    private final List<JsonAdaptedCourseClass> courseClasses;

    /**
     * Constructs a {@code JsonSerializableCourseClassBook} with the given courseClasses.
     */
    @JsonCreator
    public JsonSerializableCourseClassBook(@JsonProperty("courseClasses") List<JsonAdaptedCourseClass> courseClasses) {
        this.courseClasses = courseClasses == null ? null : new ArrayList<>(courseClasses);
    }

    /**
     * Converts a given {@code ReadOnlyCourseClassBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableCourseClassBook}.
     */
    public JsonSerializableCourseClassBook(ReadOnlyCourseClassBook source) {
        courseClasses = source.getCourseClassList().stream()
                .map(JsonAdaptedCourseClass::new).collect(Collectors.toList());
    }

    /**
     * Converts this address book into the model's {@code CourseClassBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public CourseClassBook toModelType() throws IllegalValueException {
        if (courseClasses == null) {
            throw new IllegalValueException("Course classes list is missing.");
        }
        CourseClassBook courseClassBook = new CourseClassBook();
        for (JsonAdaptedCourseClass jsonAdaptedPerson : courseClasses) {
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException("Course classes list must not contain null entries.");
            }
            CourseClass courseClass = jsonAdaptedPerson.toModelType();
            if (courseClassBook.hasCourseClass(courseClass)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_COURSE_CLASS);
            }
            courseClassBook.addCourseClass(courseClass);
        }
        return courseClassBook;
    }

}
