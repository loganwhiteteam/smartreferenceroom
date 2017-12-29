package smartconferenceroom.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import smartconferenceroom.FanApp;
import smartconferenceroom.device.Device;

public class FanView {
    @FXML
    private RadioButton onBut;
    @FXML
    private RadioButton offBut;

    private ObservableList<String> fanDeviceIds = FXCollections.observableArrayList();
    private final ToggleGroup fanSensorGroup = new ToggleGroup();
    private FanApp app;

    private static final String STATE_ON = "available";
    private static final String STATE_OFF = "unavailable";

    @FXML
    private void initialize() {

        // Radio Button
        onBut.setUserData(STATE_ON);
        offBut.setUserData(STATE_OFF);
        onBut.setToggleGroup(fanSensorGroup);
        offBut.setToggleGroup(fanSensorGroup);
        fanSensorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (fanSensorGroup.getSelectedToggle() != null) {
                boolean status = false;
                // Parse status value
                String statusStr = (String) fanSensorGroup.getSelectedToggle().getUserData();
                if (statusStr.equals(STATE_ON)) {
                    status = true;
                } else if (statusStr.equals(STATE_OFF)) {
                    status = false;
                }

                app.setFanState(status);
            }
        });
    }

    public void setApp(FanApp app) {
        this.app = app;
    }

    public void populateFanList(Device[] devices) {
        for (Device device : devices) {
            fanDeviceIds.add(device.getId());
        }
    }

    public void updateFanStatus(boolean status) {
        if (status) {
            onBut.fire();
        } else {
            offBut.fire();
        }
    }
}
