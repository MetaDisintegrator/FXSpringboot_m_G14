package org.fxtravel.services.train.degrade;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DegradeSwitch {
    private final boolean on;
    public DegradeSwitch(@Value("${app.degrade-all:false}") boolean on) {
        this.on = on;
    }
}
