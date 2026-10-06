package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.model.tag.Tag;

/** Jackson-friendly version of a course class, including its student book. */
class JsonAdaptedCourseClass {
    private final String name;
    private final String courseCode;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final JsonSerializableAddressBook students;

    /** Creates an adapted class from its JSON fields. */
    @JsonCreator
    public JsonAdaptedCourseClass(@JsonProperty("name") String name,
            @JsonProperty("courseCode") String courseCode, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("students") JsonSerializableAddressBook students) {
        this.name = name;
        this.courseCode = courseCode;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.students = students;
    }

    /** Copies a course class for serialization. */
    public JsonAdaptedCourseClass(CourseClass source) {
        name = source.getName().fullName;
        courseCode = source.getCourseCode().value;
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
        students = new JsonSerializableAddressBook(source.getStudents());
    }

    /**
     * Converts the stored fields to a course class.
     * @throws IllegalValueException if a field is missing or invalid.
     */
    public CourseClass toModelType() throws IllegalValueException {
        if (name == null || !Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (courseCode == null || !CourseCode.isValidCourseCode(courseCode)) {
            throw new IllegalValueException(CourseCode.MESSAGE_CONSTRAINTS);
        }
        Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException("Course class tags must not be null.");
            }
            modelTags.add(tag.toModelType());
        }
        AddressBook studentBook = students == null ? new AddressBook() : students.toModelType();
        return new CourseClass(new Name(name), new CourseCode(courseCode), modelTags, studentBook);
    }
}
