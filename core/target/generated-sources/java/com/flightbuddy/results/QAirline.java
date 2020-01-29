package com.flightbuddy.results;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAirline is a Querydsl query type for Airline
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAirline extends EntityPathBase<Airline> {

    private static final long serialVersionUID = -1065000543L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAirline airline = new QAirline("airline");

    public final com.flightbuddy.db.QImmutableEntity _super = new com.flightbuddy.db.QImmutableEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final QFlight flight;

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public QAirline(String variable) {
        this(Airline.class, forVariable(variable), INITS);
    }

    public QAirline(Path<? extends Airline> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAirline(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAirline(PathMetadata metadata, PathInits inits) {
        this(Airline.class, metadata, inits);
    }

    public QAirline(Class<? extends Airline> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.flight = inits.isInitialized("flight") ? new QFlight(forProperty("flight"), inits.get("flight")) : null;
    }

}

