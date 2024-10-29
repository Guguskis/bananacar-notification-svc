package lt.liutikas.bananacar_notification_svc.adapter.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BananacarLocation {

    private String city;
    private int ord;
}
