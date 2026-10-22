package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.HomeworkStatus;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Student;
import seedu.address.model.tag.Tag;

/** Jackson-friendly version of a course class, including its student book. */
class JsonAdaptedCourseClass {
    private final String name;
    private final String courseCode;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedStudent> enrolledStudents = new ArrayList<>();
    private final String homeworkStatus;

    /** Creates an adapted class from its JSON fields. */
    @JsonCreator
    public JsonAdaptedCourseClass(@JsonProperty("name") String name,
            @JsonProperty("courseCode") String courseCode, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("enrolledStudents") List<JsonAdaptedStudent> enrolledStudents,
            @JsonProperty("homeworkStatus") String homeworkStatus) {
        this.name = name;
        this.courseCode = courseCode;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (enrolledStudents != null) {
            this.enrolledStudents.addAll(enrolledStudents);
        }
        this.homeworkStatus = homeworkStatus == null
                ? HomeworkStatus.NOT_SET.getSerializedValue() : homeworkStatus;
    }

    /**
     * Creates an adapted class without homework status data.
     *
     * @param name class name
     * @param courseCode course code
     * @param tags class tags
     * @param enrolledStudents enrolled student data
     */
    public JsonAdaptedCourseClass(String name, String courseCode, List<JsonAdaptedTag> tags,
            List<JsonAdaptedStudent> enrolledStudents) {
        this(name, courseCode, tags, enrolledStudents, null);
    }

    /** Copies a course class for serialization. */
    public JsonAdaptedCourseClass(CourseClass source) {
        name = source.getName().fullName;
        courseCode = source.getCourseCode().value;
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
        enrolledStudents.addAll(source.getStudentList().stream()
                .map(JsonAdaptedStudent::new).collect(Collectors.toList()));
        homeworkStatus = source.getHomeworkStatus().getSerializedValue();
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
        List<Student> modelStudents = new ArrayList<>();
        for (JsonAdaptedStudent jsonAdaptedStudent : enrolledStudents) {
            if (jsonAdaptedStudent == null) {
                throw new IllegalValueException("Enrolled students list must not contain null entries.");
            }
            Student student = jsonAdaptedStudent.toModelType();
            if (modelStudents.stream().anyMatch(student::isSameStudent)) {
                throw new IllegalValueException("Enrolled students list contains duplicate student(s).");
            }
            modelStudents.add(student);
        }
        HomeworkStatus modelHomeworkStatus = HomeworkStatus.fromSerializedValue(homeworkStatus)
                .orElseThrow(() -> new IllegalValueException("Invalid homework status: " + homeworkStatus));
        return new CourseClass(new Name(name), new CourseCode(courseCode), modelTags,
                modelStudents, modelHomeworkStatus);
    }
}
