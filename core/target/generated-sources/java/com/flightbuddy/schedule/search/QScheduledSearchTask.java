package com.flightbuddy.schedule.search;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QScheduledSearchTask is a Querydsl query type for ScheduledSearchTask
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QScheduledSearchTask extends EntityPathBase<ScheduledSearchTask> {

    private static final long serialVersionUID = -336261082L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScheduledSearchTask scheduledSearchTask = new QScheduledSearchTask("scheduledSearchTask");

    public final com.flightbuddy.db.QMutableEntity _super = new com.flightbuddy.db.QMutableEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final DateTimePath<java.time.LocalDateTime> executionTime = createDateTime("executionTime", java.time.LocalDateTime.class);

    public final StringPath id = createString("id");

    public final QScheduledSearch scheduledSearch;

    public final EnumPath<ScheduledSearchTask.RequestService> service = createEnum("service", ScheduledSearchTask.RequestService.class);

    public final EnumPath<ScheduledSearchTask.ScheduledSearchState> state = createEnum("state", ScheduledSearchTask.ScheduledSearchState.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated = _super.updated;

    //inherited
    public final NumberPath<Integer> version = _super.version;

    public QScheduledSearchTask(String variable) {
        this(ScheduledSearchTask.class, forVariable(variable), INITS);
    }

    public QScheduledSearchTask(Path<? extends ScheduledSearchTask> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QScheduledSearchTask(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QScheduledSearchTask(PathMetadata metadata, PathInits inits) {
        this(ScheduledSearchTask.class, metadata, inits);
    }

    public QScheduledSearchTask(Class<? extends ScheduledSearchTask> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.scheduledSearch = inits.isInitialized("scheduledSearch") ? new QScheduledSearch(forProperty("scheduledSearch"), inits.get("scheduledSearch")) : null;
    }

}

