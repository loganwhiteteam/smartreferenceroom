package smartconferenceroom.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;

public class SetLightStatusAction extends ActionInvocation {
    public SetLightStatusAction(Service service, boolean status) {
        super(service.getAction("SetStatus"));

        try {
            setInput("NewStatus", status);
        } catch (InvalidValueException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
