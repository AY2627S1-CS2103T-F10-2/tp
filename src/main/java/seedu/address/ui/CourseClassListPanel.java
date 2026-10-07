package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.courseclass.CourseClass;

/**
 * Panel containing the list of course classes.
 */
public class CourseClassListPanel extends UiPart<Region> {
    private static final String FXML = "CourseClassListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(CourseClassListPanel.class);

    @FXML
    private ListView<CourseClass> courseClassListView;

    /**
     * Creates a {@code CourseClassListPanel} with the given {@code ObservableList}.
     */
    public CourseClassListPanel(ObservableList<CourseClass> courseClassList) {
        super(FXML);
        courseClassListView.setItems(courseClassList);
        courseClassListView.setCellFactory(listView -> new CourseClassListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code CourseClass} using a {@code CourseClassCard}.
     */
    class CourseClassListViewCell extends ListCell<CourseClass> {
        @Override
        protected void updateItem(CourseClass courseClass, boolean empty) {
            super.updateItem(courseClass, empty);

            if (empty || courseClass == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new CourseClassCard(courseClass, getIndex() + 1).getRoot());
            }
        }
    }

}
