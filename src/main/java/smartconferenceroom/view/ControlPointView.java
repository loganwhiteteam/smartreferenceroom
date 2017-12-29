package smartconferenceroom.view;

import javafx.fxml.FXML;
import smartconferenceroom.ConferenceRoomControlPoint;

public class ControlPointView {
    private ConferenceRoomControlPoint app;

    @FXML
    private void onObjectRelease() {
        app.onObjectRelease();
    }

    @FXML
    private void onObjectDetected() {
        app.onObjectDetected();
    }

    public void setApp(ConferenceRoomControlPoint app) {
        this.app = app;
    }
}
