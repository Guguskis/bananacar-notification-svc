/*
 * This file is generated by jOOQ.
 */
package lt.liutikas.bananacar_notification_svc.generated.jooq.tables;


import lt.liutikas.bananacar_notification_svc.generated.jooq.Keys;
import lt.liutikas.bananacar_notification_svc.generated.jooq.Public;
import lt.liutikas.bananacar_notification_svc.generated.jooq.tables.records.RideSubscriptionRecord;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class RideSubscription extends TableImpl<RideSubscriptionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.ride_subscription</code>
     */
    public static final RideSubscription RIDE_SUBSCRIPTION = new RideSubscription();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RideSubscriptionRecord> getRecordType() {
        return RideSubscriptionRecord.class;
    }

    /**
     * The column <code>public.ride_subscription.id</code>.
     */
    public final TableField<RideSubscriptionRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>public.ride_subscription.origin_city</code>.
     */
    public final TableField<RideSubscriptionRecord, String> ORIGIN_CITY = createField(DSL.name("origin_city"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.ride_subscription.destination_city</code>.
     */
    public final TableField<RideSubscriptionRecord, String> DESTINATION_CITY = createField(DSL.name("destination_city"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>public.ride_subscription.departs_on_earliest</code>.
     */
    public final TableField<RideSubscriptionRecord, LocalDateTime> DEPARTS_ON_EARLIEST = createField(DSL.name("departs_on_earliest"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.ride_subscription.departs_on_latest</code>.
     */
    public final TableField<RideSubscriptionRecord, LocalDateTime> DEPARTS_ON_LATEST = createField(DSL.name("departs_on_latest"), SQLDataType.LOCALDATETIME(6), this, "");

    private RideSubscription(Name alias, Table<RideSubscriptionRecord> aliased) {
        this(alias, aliased, null);
    }

    private RideSubscription(Name alias, Table<RideSubscriptionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.ride_subscription</code> table reference
     */
    public RideSubscription(String alias) {
        this(DSL.name(alias), RIDE_SUBSCRIPTION);
    }

    /**
     * Create an aliased <code>public.ride_subscription</code> table reference
     */
    public RideSubscription(Name alias) {
        this(alias, RIDE_SUBSCRIPTION);
    }

    /**
     * Create a <code>public.ride_subscription</code> table reference
     */
    public RideSubscription() {
        this(DSL.name("ride_subscription"), null);
    }

    public <O extends Record> RideSubscription(Table<O> child, ForeignKey<O, RideSubscriptionRecord> key) {
        super(child, key, RIDE_SUBSCRIPTION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<RideSubscriptionRecord> getPrimaryKey() {
        return Keys.RIDE_SUBSCRIPTION_PKEY;
    }

    @Override
    public RideSubscription as(String alias) {
        return new RideSubscription(DSL.name(alias), this);
    }

    @Override
    public RideSubscription as(Name alias) {
        return new RideSubscription(alias, this);
    }

    @Override
    public RideSubscription as(Table<?> alias) {
        return new RideSubscription(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RideSubscription rename(String name) {
        return new RideSubscription(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RideSubscription rename(Name name) {
        return new RideSubscription(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RideSubscription rename(Table<?> name) {
        return new RideSubscription(name.getQualifiedName(), null);
    }
}
