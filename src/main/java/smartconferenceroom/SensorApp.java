package smartconferenceroom;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import smartconferenceroom.action.SetInfraredSensorStatusAction;
import smartconferenceroom.device.Device;
import smartconferenceroom.device.DeviceApp;
import smartconferenceroom.service.InfraredSensor;
import smartconferenceroom.view.SensorView;

import java.io.IOException;
import java.util.Map;

public class SensorApp extends DeviceApp {
    private Device currentDevice;
    private SensorView sensorView;
    private boolean currentStatus = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Pyroelectric Infrared Radial Sensor Control Panel");
        initializeDevices(1, "Sensor", "Sensor", "Using for detecting people", InfraredSensor.class);
        initializeRootLayout();
        setServiceIds("Sensor");
        setCurrentDevice(0);

        sensorView.populateInfraredSensorList(devices);

    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue status = values.get("Status");
        if (currentStatus != (boolean) status.getValue()) {
            setInfraredSensorState((boolean) status.getValue());
            currentStatus = (boolean) status.getValue();
            sensorView.updateInfraredSensorStatus((boolean) status.getValue());
        }

    }

    private void initializeRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SensorView.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout, 320, 150);
            primaryStage.setScene(scene);
            primaryStage.show();
            sensorView = loader.getController();
            sensorView.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = devices[index];
        Service service = getService(currentDevice.getDevice(), "Sensor");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }

    public void setInfraredSensorState(boolean state) {
        Service service = getService(currentDevice.getDevice(), "Sensor");
        if (service != null) {
            executeAction(upnpService, new SetInfraredSensorStatusAction(service, state));
        }
    }
}