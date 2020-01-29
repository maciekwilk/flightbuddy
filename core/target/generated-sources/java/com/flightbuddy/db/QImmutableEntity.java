package com.flightbuddy.db;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QImmutableEntity is a Querydsl query type for ImmutableEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QImmutableEntity extends EntityPathBase<ImmutableEntity> {

    private static final long serialVersionUID = 1883787546L;

    public static final QImmutableEntity immutableEntity = new QImmutableEntity("immutableEntity");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public QImmutableEntity(String variable) {
        super(ImmutableEntity.class, forVariable(variable));
    }

    public QImmutableEntity(Path<? extends ImmutableEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QImmutableEntity(PathMetadata metadata) {
        super(ImmutableEntity.class, metadata);
    }

}

