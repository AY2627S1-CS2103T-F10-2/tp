package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.CourseClassBook;

/**
 * Represents the course-class data and its persisted undo snapshots.
 */
public class CourseClassBookStorageData {
    private final CourseClassBook courseClassBook;
    private final List<CourseClassBook> undoHistory;

    /** Creates storage data and defensively copies all course-class books. */
    public CourseClassBookStorageData(CourseClassBook courseClassBook, List<CourseClassBook> undoHistory) {
        requireNonNull(courseClassBook);
        requireNonNull(undoHistory);
        this.courseClassBook = new CourseClassBook(courseClassBook);
        this.undoHistory = undoHistory.stream()
                .map(CourseClassBook::new)
                .toList();
    }

    /** Returns a defensive copy of the current course-class book. */
    public CourseClassBook getCourseClassBook() {
        return new CourseClassBook(courseClassBook);
    }

    /** Returns defensive copies of the persisted undo snapshots. */
    public List<CourseClassBook> getUndoHistory() {
        return undoHistory.stream()
                .map(CourseClassBook::new)
                .toList();
    }
}
