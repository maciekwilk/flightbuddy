package com.flightbuddy.schedule.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPassengers is a Querydsl query type for Passengers
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPassengers extends EntityPathBase<Passengers> {

    private static final long serialVersionUID = -217952947L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPassengers passengers = new QPassengers("passengers");

    public final com.flightbuddy.db.QMutableEntity _super = new com.flightbuddy.db.QMutableEntity(this);

    public final NumberPath<Integer> adultCount = createNumber("adultCount", Integer.class);

    public final NumberPath<Integer> childCount = createNumber("childCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final StringPath id = createString("id");

    public final NumberPath<Integer> infantInLapCount = createNumber("infantInLapCount", Integer.class);

    public final NumberPath<Integer> infantInSeatCount = createNumber("infantInSeatCount", Integer.class);

    public final QScheduledSearch scheduledSearch;

    public final NumberPath<Integer> seniorCount = createNumber("seniorCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated = _super.updated;

    //inherited
    public final NumberPath<Integer> version = _super.version;

    public QPassengers(String variable) {
        this(Passengers.class, forVariable(variable), INITS);
    }

    public QPassengers(Path<? extends Passengers> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPassengers(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPassengers(PathMetadata metadata, PathInits inits) {
        this(Passengers.class, metadata, inits);
    }

    public QPassengers(Class<? extends Passengers> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.scheduledSearch = inits.isInitialized("scheduledSearch") ? new QScheduledSearch(forProperty("scheduledSearch"), inits.get("scheduledSearch")) : null;
    }

}

