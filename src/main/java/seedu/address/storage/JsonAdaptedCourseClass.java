package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import seedu.address.model.student.Telehandle;
import seedu.address.model.tag.Tag;

/** Jackson-friendly version of a course class, including its student book. */
class JsonAdaptedCourseClass {
    private final String name;
    private final String courseCode;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedStudent> enrolledStudents = new ArrayList<>();
    private final String homeworkStatus;
    private final Map<String, String> studentHomeworkStatuses = new HashMap<>();

    /** Creates an adapted class from its JSON fields. */
    @JsonCreator
    public JsonAdaptedCourseClass(@JsonProperty("name") String name,
            @JsonProperty("courseCode") String courseCode, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("enrolledStudents") List<JsonAdaptedStudent> enrolledStudents,
            @JsonProperty("homeworkStatus") String homeworkStatus,
            @JsonProperty("studentHomeworkStatuses") Map<String, String> studentHomeworkStatuses) {
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
        if (studentHomeworkStatuses != null) {
            this.studentHomeworkStatuses.putAll(studentHomeworkStatuses);
        }
    }

    /** Creates an adapted class with class-wide homework status but no individual statuses. */
    public JsonAdaptedCourseClass(String name, String courseCode, List<JsonAdaptedTag> tags,
            List<JsonAdaptedStudent> enrolledStudents, String homeworkStatus) {
        this(name, courseCode, tags, enrolledStudents, homeworkStatus, null);
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
        this(name, courseCode, tags, enrolledStudents, null, null);
    }

    /** Copies a course class for serialization. */
    public JsonAdaptedCourseClass(CourseClass source) {
        name = source.getName().fullName;
        courseCode = source.getCourseCode().value;
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
        enrolledStudents.addAll(source.getStudentList().stream()
                .map(JsonAdaptedStudent::new).collect(Collectors.toList()));
        homeworkStatus = source.getHomeworkStatus().getSerializedValue();
        source.getStudentHomeworkStatuses().forEach((telehandle, status) ->
                studentHomeworkStatuses.put(telehandle.value, status.getSerializedValue()));
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
        Map<Telehandle, HomeworkStatus> modelStudentHomeworkStatuses = new HashMap<>();
        for (Map.Entry<String, String> entry : studentHomeworkStatuses.entrySet()) {
            Telehandle telehandle;
            try {
                telehandle = new Telehandle(entry.getKey());
            } catch (IllegalArgumentException exception) {
                throw new IllegalValueException("Invalid student telehandle for homework status: " + entry.getKey());
            }
            if (modelStudents.stream().noneMatch(student -> student.getTelehandle().equals(telehandle))) {
                throw new IllegalValueException("Homework status refers to a student not enrolled in the class: "
                        + entry.getKey());
            }
            HomeworkStatus status = HomeworkStatus.fromSerializedValue(entry.getValue())
                    .orElseThrow(() -> new IllegalValueException("Invalid student homework status: "
                            + entry.getValue()));
            modelStudentHomeworkStatuses.put(telehandle, status);
        }
        return new CourseClass(new Name(name), new CourseCode(courseCode), modelTags,
                modelStudents, modelHomeworkStatus, modelStudentHomeworkStatuses);
    }
}
