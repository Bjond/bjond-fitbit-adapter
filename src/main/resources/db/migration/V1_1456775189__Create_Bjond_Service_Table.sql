CREATE TABLE bjond_service
    (
        id VARCHAR NOT NULL,
        group_id VARCHAR NOT NULL,
        endpoint VARCHAR NOT NULL,

        PRIMARY KEY (id)
    );

CREATE INDEX bjond_service_idx  ON bjond_service (group_id);
