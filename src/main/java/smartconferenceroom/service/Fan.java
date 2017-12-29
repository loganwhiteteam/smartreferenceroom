package smartconferenceroom.service;

import org.fourthline.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
        serviceId = @UpnpServiceId("Fan"),
        serviceType = @UpnpServiceType(value = "Fan", version = 1)
)

public class Fan {
    private final PropertyChangeSupport propertyChangeSupport;

    public Fan() {
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
