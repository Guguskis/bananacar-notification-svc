package lt.liutikas.bananacar_notification_svc.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggable {

    default Logger getLogger() {

        return LoggerFactory.getLogger(this.getClass());
    }
}
