package smartconferenceroom.service;

import org.fourthline.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Sensor"),
        serviceType = @UpnpServiceType(value = "Sensor", version = 1)
)

public class InfraredSensor {
    private final PropertyChangeSupport propertyChangeSupport;

    public InfraredSensor() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "Unknown")
    private String id;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setId(@UpnpInputArgument(name = "NewId") String newId) {
        id = newId;
    }

    @UpnpAction
    public void setStatus(@UpnpInputArgument(name = "NewStatus") boolean newStatus) {
        status = newStatus;
        getPropertyChangeSupport().firePropertyChange("Status", null, null);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return status;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultId"))
    public String getId() {
        return id;
    }
}
