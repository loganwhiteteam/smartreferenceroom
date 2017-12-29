package smartconferenceroom;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import smartconferenceroom.action.SetLightStatusAction;
import smartconferenceroom.device.Device;
import smartconferenceroom.device.DeviceApp;
import smartconferenceroom.service.Light;
import smartconferenceroom.view.LightView;

import java.io.IOException;
import java.util.Map;

public class LightApp extends DeviceApp {
    private Device currentDevice;
    private LightView lightView;
    private boolean currentStatus = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Light");
        initializeDevices(1, "Light", "Light", "Using turn on the light", Light.class);
        initializeRootLayout();
        setServiceIds("Light");
        setCurrentDevice(0);
        lightView.populateLightList(devices);

    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue status = values.get("Status");
        if (currentStatus != (boolean) status.getValue()) {
            setLightState((boolean) status.getValue());
            currentStatus = (boolean) status.getValue();
            if (currentStatus == true) {
                lightView.updateLightStatus(80);
            } else {
                lightView.updateLightStatus(0);
            }
        }

    }

    private void initializeRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/LightView.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout, 320, 150);
            primaryStage.setScene(scene);
            primaryStage.show();
            lightView = loader.getController();
            lightView.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = devices[index];
        Service service = getService(currentDevice.getDevice(), "Light");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }

    public void setLightState(boolean state) {
        Service service = getService(currentDevice.getDevice(), "Light");
        if (service != null) {
            executeAction(upnpService, new SetLightStatusAction(service, state));
        }
    }
}