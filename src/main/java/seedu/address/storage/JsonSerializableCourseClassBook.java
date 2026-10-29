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
    private final List<JsonSerializableCourseClassBook> undoHistory;

    /**
     * Constructs a {@code JsonSerializableCourseClassBook} with the given courseClasses.
     */
    @JsonCreator
    public JsonSerializableCourseClassBook(@JsonProperty("courseClasses") List<JsonAdaptedCourseClass> courseClasses,
            @JsonProperty("undoHistory") List<JsonSerializableCourseClassBook> undoHistory) {
        this.courseClasses = courseClasses == null ? null : new ArrayList<>(courseClasses);
        this.undoHistory = undoHistory == null ? new ArrayList<>() : new ArrayList<>(undoHistory);
    }

    /** Constructs a serializable course-class book without undo history. */
    public JsonSerializableCourseClassBook(List<JsonAdaptedCourseClass> courseClasses) {
        this(courseClasses, null);
    }

    /**
     * Converts a given {@code ReadOnlyCourseClassBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableCourseClassBook}.
     */
    public JsonSerializableCourseClassBook(ReadOnlyCourseClassBook source) {
        this(source, List.of());
    }

    /** Constructs a serializable course-class book with undo history. */
    public JsonSerializableCourseClassBook(ReadOnlyCourseClassBook source,
            List<? extends ReadOnlyCourseClassBook> undoHistory) {
        courseClasses = source.getCourseClassList().stream()
                .map(JsonAdaptedCourseClass::new).collect(Collectors.toList());
        this.undoHistory = undoHistory.stream()
                .map(historyEntry -> new JsonSerializableCourseClassBook(historyEntry))
                .collect(Collectors.toList());
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

    /** Converts the persisted undo history to model objects. */
    public List<CourseClassBook> toUndoHistory() throws IllegalValueException {
        List<CourseClassBook> history = new ArrayList<>();
        for (JsonSerializableCourseClassBook historyEntry : undoHistory) {
            if (historyEntry == null) {
                throw new IllegalValueException("Undo history must not contain null entries.");
            }
            history.add(historyEntry.toModelType());
        }
        return history;
    }

}
