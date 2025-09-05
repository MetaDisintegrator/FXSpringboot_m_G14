// hotel-service/src/main/java/org/fxtravel/services/hotel/degrade/DegradeSwitch.java
package org.fxtravel.services.hotel.degrade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DegradeSwitch {
    private final boolean on;
    public DegradeSwitch(@Value("${app.degrade-all:false}") boolean on) { this.on = on; }
    public boolean isOn() { return on; }
}
