package com.flightbuddy.db;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMutableEntity is a Querydsl query type for MutableEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QMutableEntity extends EntityPathBase<MutableEntity> {

    private static final long serialVersionUID = 1340482814L;

    public static final QMutableEntity mutableEntity = new QMutableEntity("mutableEntity");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public QMutableEntity(String variable) {
        super(MutableEntity.class, forVariable(variable));
    }

    public QMutableEntity(Path<? extends MutableEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMutableEntity(PathMetadata metadata) {
        super(MutableEntity.class, metadata);
    }

}

