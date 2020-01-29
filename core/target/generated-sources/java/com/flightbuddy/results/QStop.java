package com.flightbuddy.results;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStop is a Querydsl query type for Stop
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QStop extends EntityPathBase<Stop> {

    private static final long serialVersionUID = -210121345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStop stop = new QStop("stop");

    public final com.flightbuddy.db.QImmutableEntity _super = new com.flightbuddy.db.QImmutableEntity(this);

    public final DateTimePath<java.time.LocalDateTime> arrivalTime = createDateTime("arrivalTime", java.time.LocalDateTime.class);

    public final StringPath code = createString("code");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final DateTimePath<java.time.LocalDateTime> departureTime = createDateTime("departureTime", java.time.LocalDateTime.class);

    public final QFlight flight;

    public final StringPath id = createString("id");

    public QStop(String variable) {
        this(Stop.class, forVariable(variable), INITS);
    }

    public QStop(Path<? extends Stop> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStop(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStop(PathMetadata metadata, PathInits inits) {
        this(Stop.class, metadata, inits);
    }

    public QStop(Class<? extends Stop> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.flight = inits.isInitialized("flight") ? new QFlight(forProperty("flight"), inits.get("flight")) : null;
    }

}

