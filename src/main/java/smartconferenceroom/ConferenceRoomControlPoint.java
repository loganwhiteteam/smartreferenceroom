package smartconferenceroom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import smartconferenceroom.action.SetFanStatusAction;
import smartconferenceroom.action.SetInfraredSensorStatusAction;
import smartconferenceroom.view.ControlPointView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConferenceRoomControlPoint extends Application {
    private HashMap<String, RemoteDevice> controlledDevices;
    private final UpnpService upnpService = new UpnpServiceImpl();
    private Stage primaryStage;
    private ControlPointView controlPointView;
//    private SensorApp sensorApp = new SensorApp();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Smart Conference Room - People action");

        try {
            controlledDevices = new HashMap<>();
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );
            initializeRootLayout();
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

    private void initializeRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ControlPointView.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout, 320, 240);
            primaryStage.setScene(scene);
            primaryStage.show();
            controlPointView = loader.getController();
            controlPointView.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePropertyChangeCallback(UpnpService upnpService, Service service) {
        SubscriptionCallback callback = new SubscriptionCallback(service, 600) {

            @Override
            public void established(GENASubscription sub) {
            }

            @Override
            protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
                System.err.println(defaultMsg);
            }

            @Override
            public void ended(GENASubscription sub, CancelReason reason, UpnpResponse response) {
            }

            @Override
            public void eventReceived(GENASubscription sub) {
                try {
                    Map<String, StateVariableValue> values = sub.getCurrentValues();
                    StateVariableValue idVar = values.get("Id");
                    if (idVar != null) {
                        String id = (String) idVar.getValue();
                        switch (id) {
                            case "Sensor0":
                                boolean status = (boolean) values.get("Status").getValue();
                                Service fanService = controlledDevices.get("Fan0").findService(new UDAServiceId("Fan"));
                                if (fanService != null) {
                                    executeAction(upnpService, new SetFanStatusAction(fanService, status));
                                }
                                Service lightService = controlledDevices.get("Light0").findService(new UDAServiceId("Light"));
                                if (lightService != null) {
                                    executeAction(upnpService, new SetFanStatusAction(lightService, status));
                                }
                                break;

                            default:
                                break;
                        }
                    }
                } catch (Exception ignored) {

                }

            }

            @Override
            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
//                System.out.println("Missed events: " + numberOfMissedEvents);
            }

            @Override
            protected void invalidMessage(RemoteGENASubscription sub, UnsupportedDataException ex) {
            }
        };

        upnpService.getControlPoint().execute(callback);
    }

    private RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {
            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                String deviceId = device.getDetails().getFriendlyName();
                controlledDevices.put(deviceId, device);
                Service infraredSensorService = device.findService(new UDAServiceId("Sensor"));
                if (infraredSensorService != null) {
                    initializePropertyChangeCallback(upnpService, infraredSensorService);
                }
//                System.out.println("Device discovered: " + deviceId);
            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                String deviceId = device.getDetails().getFriendlyName();
                controlledDevices.remove(deviceId);
//                System.out.println("Device disappeared: " + deviceId);
            }
        };
    }

    private void executeAction(UpnpService upnpService, ActionInvocation action) {
        upnpService.getControlPoint().execute(
                new ActionCallback(action) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        assert invocation.getOutput().length == 0;
//                        System.out.println("Successfully called action " + invocation.getClass().getSimpleName());
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        System.err.println(defaultMsg);
                    }
                }
        );
    }

    public void onObjectDetected() {
        Service service = controlledDevices.get("Sensor0").findService(new UDAServiceId("Sensor"));
        if (service != null) {
            executeAction(upnpService, new SetInfraredSensorStatusAction(service, true));
        }
    }

    public void onObjectRelease() {
        Service service = controlledDevices.get("Sensor0").findService(new UDAServiceId("Sensor"));
        if (service != null) {
            executeAction(upnpService, new SetInfraredSensorStatusAction(service, false));
        }
    }
}
