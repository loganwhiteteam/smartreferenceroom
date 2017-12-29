package smartconferenceroom.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import smartconferenceroom.LightApp;
import smartconferenceroom.device.Device;

public class LightView {
    @FXML
    private Slider level;

    private ObservableList<String> lightDeviceIds = FXCollections.observableArrayList();
    private final ToggleGroup lightSensorGroup = new ToggleGroup();
    private LightApp app;

    @FXML
    private void initialize() {
    }

    public void setApp(LightApp app) {
        this.app = app;
    }

    public void populateLightList(Device[] devices) {
        for (Device device : devices) {
            lightDeviceIds.add(device.getId());
        }
    }

    public void updateLightStatus(double status) {
        level.setValue(status);
    }
}
