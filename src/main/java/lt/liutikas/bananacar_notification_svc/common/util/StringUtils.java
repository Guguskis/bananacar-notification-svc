package lt.liutikas.bananacar_notification_svc.common.util;

import java.text.Normalizer;

public class StringUtils {

    public static String removeDiacriticalMarks(String input) {

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
