package com.flightbuddy.results;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFoundTrip is a Querydsl query type for FoundTrip
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFoundTrip extends EntityPathBase<FoundTrip> {

    private static final long serialVersionUID = -1424289942L;

    public static final QFoundTrip foundTrip = new QFoundTrip("foundTrip");

    public final com.flightbuddy.db.QImmutableEntity _super = new com.flightbuddy.db.QImmutableEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final ListPath<Flight, QFlight> flights = this.<Flight, QFlight>createList("flights", Flight.class, QFlight.class, PathInits.DIRECT2);

    public final StringPath id = createString("id");

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public QFoundTrip(String variable) {
        super(FoundTrip.class, forVariable(variable));
    }

    public QFoundTrip(Path<? extends FoundTrip> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFoundTrip(PathMetadata metadata) {
        super(FoundTrip.class, metadata);
    }

}

