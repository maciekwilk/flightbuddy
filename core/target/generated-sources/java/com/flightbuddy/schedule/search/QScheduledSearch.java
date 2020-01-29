package com.flightbuddy.schedule.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QScheduledSearch is a Querydsl query type for ScheduledSearch
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QScheduledSearch extends EntityPathBase<ScheduledSearch> {

    private static final long serialVersionUID = -1782657279L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScheduledSearch scheduledSearch = new QScheduledSearch("scheduledSearch");

    public final com.flightbuddy.db.QMutableEntity _super = new com.flightbuddy.db.QMutableEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final ListPath<java.time.LocalDate, DatePath<java.time.LocalDate>> dates = this.<java.time.LocalDate, DatePath<java.time.LocalDate>>createList("dates", java.time.LocalDate.class, DatePath.class, PathInits.DIRECT2);

    public final StringPath from = createString("from");

    public final StringPath id = createString("id");

    public final NumberPath<Integer> maxPrice = createNumber("maxPrice", Integer.class);

    public final NumberPath<Integer> minPrice = createNumber("minPrice", Integer.class);

    public final QPassengers passengers;

    public final ListPath<ScheduledSearchTask, QScheduledSearchTask> scheduledSearchTasks = this.<ScheduledSearchTask, QScheduledSearchTask>createList("scheduledSearchTasks", ScheduledSearchTask.class, QScheduledSearchTask.class, PathInits.DIRECT2);

    public final StringPath to = createString("to");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated = _super.updated;

    public final com.flightbuddy.user.QUser user;

    //inherited
    public final NumberPath<Integer> version = _super.version;

    public final BooleanPath withReturn = createBoolean("withReturn");

    public QScheduledSearch(String variable) {
        this(ScheduledSearch.class, forVariable(variable), INITS);
    }

    public QScheduledSearch(Path<? extends ScheduledSearch> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QScheduledSearch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QScheduledSearch(PathMetadata metadata, PathInits inits) {
        this(ScheduledSearch.class, metadata, inits);
    }

    public QScheduledSearch(Class<? extends ScheduledSearch> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.passengers = inits.isInitialized("passengers") ? new QPassengers(forProperty("passengers"), inits.get("passengers")) : null;
        this.user = inits.isInitialized("user") ? new com.flightbuddy.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

