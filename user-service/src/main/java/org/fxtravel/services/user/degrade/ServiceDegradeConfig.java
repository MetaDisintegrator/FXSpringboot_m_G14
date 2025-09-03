package org.fxtravel.services.user.degrade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceDegradeConfig {

    public enum Mode { enabled, readonly, disabled;
        public static Mode of(String v){
            try { return Mode.valueOf((v == null ? "enabled" : v).trim().toLowerCase()); }
            catch(Exception e){ return enabled; }
        }
    }

    @Value("${SERVICE_MODE:enabled}")
    private String serviceMode;

    @Value("${DISABLE_RECOMMEND:false}")
    private boolean disableRecommend;

    public Mode mode() { return Mode.of(serviceMode); }

    public boolean disableRecommend() { return disableRecommend; }
}
