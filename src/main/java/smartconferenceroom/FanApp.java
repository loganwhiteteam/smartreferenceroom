package smartconferenceroom;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import smartconferenceroom.action.SetFanStatusAction;
import smartconferenceroom.device.Device;
import smartconferenceroom.device.DeviceApp;
import smartconferenceroom.service.Fan;
import smartconferenceroom.view.FanView;

import java.io.IOException;
import java.util.Map;

public class FanApp extends DeviceApp {
    private Device currentDevice;
    private FanView fanView;
    private boolean currentStatus = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        primaryStage.setTitle("Fan");
        initializeDevices(1, "Fan", "Fan", "Using turn on the fan", Fan.class);
        initializeRootLayout();
        setServiceIds("Fan");
        setCurrentDevice(0);
        fanView.populateFanList(devices);

    }

    @Override
    public void onPropertyChangeCallbackReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        StateVariableValue status = values.get("Status");
        if (currentStatus != (boolean) status.getValue()) {
            setFanState((boolean) status.getValue());
            currentStatus = (boolean) status.getValue();
            fanView.updateFanStatus((boolean) status.getValue());
        }

    }

    private void initializeRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FanView.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout, 320, 150);
            primaryStage.setScene(scene);
            primaryStage.show();
            fanView = loader.getController();
            fanView.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentDevice(int index) {
        currentDevice = devices[index];
        Service service = getService(currentDevice.getDevice(), "Fan");

        if (service != null) {
            initializePropertyChangeCallback(upnpService, service);
        }
    }

    public void setFanState(boolean state) {
        Service service = getService(currentDevice.getDevice(), "Fan");
        if (service != null) {
            executeAction(upnpService, new SetFanStatusAction(service, state));
        }
    }
}