package lt.liutikas.bananacar_notification_svc.common.util;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class PrefixJooqGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {

        if (mode == Mode.POJO || mode == Mode.DAO || mode == Mode.RECORD) {
            return "Jooq" + super.getJavaClassName(definition, mode);
        }

        return super.getJavaClassName(definition, mode);
    }
}
