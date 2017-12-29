package smartconferenceroom.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import smartconferenceroom.SensorApp;
import smartconferenceroom.device.Device;

public class SensorView {
    @FXML
    private RadioButton detectedBut;
    @FXML
    private RadioButton undetectedBut;

    private ObservableList<String> infraredSensorDeviceIds = FXCollections.observableArrayList();
    private final ToggleGroup infraredSensorGroup = new ToggleGroup();
    private SensorApp app;

    private static final String STATE_DETECTED = "available";
    private static final String STATE_UNDETECTED = "unavailable";

    @FXML
    private void initialize() {

        // Radio Button
        detectedBut.setUserData(STATE_DETECTED);
        undetectedBut.setUserData(STATE_UNDETECTED);
        detectedBut.setToggleGroup(infraredSensorGroup);
        undetectedBut.setToggleGroup(infraredSensorGroup);
        infraredSensorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (infraredSensorGroup.getSelectedToggle() != null) {
                boolean status = false;
                // Parse status value
                String statusStr = (String) infraredSensorGroup.getSelectedToggle().getUserData();
                if (statusStr.equals(STATE_DETECTED)) {
                    status = true;
                } else if (statusStr.equals(STATE_UNDETECTED)) {
                    status = false;
                }

                app.setInfraredSensorState(status);
            }
        });
    }

    public void setApp(SensorApp app) {
        this.app = app;
    }

    public void populateInfraredSensorList(Device[] devices) {
        for (Device device : devices) {
            infraredSensorDeviceIds.add(device.getId());
        }
    }

    public void updateInfraredSensorStatus(boolean status) {
        if (status) {
            detectedBut.fire();
        } else {
            undetectedBut.fire();
        }
    }
}
