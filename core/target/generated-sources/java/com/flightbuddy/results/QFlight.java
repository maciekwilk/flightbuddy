package com.flightbuddy.results;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFlight is a Querydsl query type for Flight
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFlight extends EntityPathBase<Flight> {

    private static final long serialVersionUID = -442900819L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFlight flight = new QFlight("flight");

    public final com.flightbuddy.db.QImmutableEntity _super = new com.flightbuddy.db.QImmutableEntity(this);

    public final ListPath<Airline, QAirline> airlines = this.<Airline, QAirline>createList("airlines", Airline.class, QAirline.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final DateTimePath<java.time.LocalDateTime> date = createDateTime("date", java.time.LocalDateTime.class);

    public final NumberPath<Integer> duration = createNumber("duration", Integer.class);

    public final QFoundTrip foundTrip;

    public final StringPath id = createString("id");

    public final ListPath<Stop, QStop> stops = this.<Stop, QStop>createList("stops", Stop.class, QStop.class, PathInits.DIRECT2);

    public QFlight(String variable) {
        this(Flight.class, forVariable(variable), INITS);
    }

    public QFlight(Path<? extends Flight> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFlight(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFlight(PathMetadata metadata, PathInits inits) {
        this(Flight.class, metadata, inits);
    }

    public QFlight(Class<? extends Flight> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.foundTrip = inits.isInitialized("foundTrip") ? new QFoundTrip(forProperty("foundTrip")) : null;
    }

}

