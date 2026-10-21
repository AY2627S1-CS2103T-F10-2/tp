package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyCourseClassBook;

/**
 * A class to access CourseClassBook data stored as a JSON file on the hard disk.
 */
public class JsonCourseClassBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonCourseClassBookStorage.class);

    private Path filePath;

    public JsonCourseClassBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getCourseClassBookFilePath() {
        return filePath;
    }

    /**
     * Returns CourseClassBook data as a {@link ReadOnlyCourseClassBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyCourseClassBook> readCourseClassBook() throws DataLoadingException {
        return readCourseClassBookData().map(CourseClassBookStorageData::getCourseClassBook);
    }

    /**
     * Similar to {@link #readCourseClassBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyCourseClassBook> readCourseClassBook(Path filePath) throws DataLoadingException {
        return readCourseClassBookData(filePath).map(CourseClassBookStorageData::getCourseClassBook);
    }

    /** Reads the course-class book together with its persisted undo history. */
    public Optional<CourseClassBookStorageData> readCourseClassBookData() throws DataLoadingException {
        return readCourseClassBookData(filePath);
    }

    /** Reads course-class data and undo history from the given path. */
    public Optional<CourseClassBookStorageData> readCourseClassBookData(Path filePath)
            throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableCourseClassBook> jsonCourseClassBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableCourseClassBook.class);
        if (!jsonCourseClassBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(new CourseClassBookStorageData(
                    jsonCourseClassBook.get().toModelType(),
                    jsonCourseClassBook.get().toUndoHistory()));
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyCourseClassBook} to the storage.
     * @param courseClassBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveCourseClassBook(ReadOnlyCourseClassBook courseClassBook) throws IOException {
        saveCourseClassBookData(courseClassBook, List.of());
    }

    /**
     * Similar to {@link #saveCourseClassBook(ReadOnlyCourseClassBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveCourseClassBook(ReadOnlyCourseClassBook courseClassBook, Path filePath) throws IOException {
        saveCourseClassBookData(courseClassBook, List.of(), filePath);
    }

    /** Saves the course-class book together with its undo history. */
    public void saveCourseClassBookData(ReadOnlyCourseClassBook courseClassBook,
            List<? extends ReadOnlyCourseClassBook> undoHistory) throws IOException {
        saveCourseClassBookData(courseClassBook, undoHistory, filePath);
    }

    /** Saves course-class data and undo history to the given path. */
    public void saveCourseClassBookData(ReadOnlyCourseClassBook courseClassBook,
            List<? extends ReadOnlyCourseClassBook> undoHistory, Path filePath) throws IOException {
        requireNonNull(courseClassBook);
        requireNonNull(undoHistory);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path temporaryFile = Files.createTempFile(absoluteFilePath.getParent(), "courseclassbook", ".tmp");
        try {
            JsonUtil.saveJsonFile(new JsonSerializableCourseClassBook(courseClassBook, undoHistory), temporaryFile);
            try {
                Files.move(temporaryFile, absoluteFilePath,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, absoluteFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

}
