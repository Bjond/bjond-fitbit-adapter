CREATE TABLE group_configuration
(
	id VARCHAR NOT NULL,
    group_id VARCHAR NOT NULL,
    slack_team VARCHAR,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX group_configuration_idx ON group_configuration (group_id);
