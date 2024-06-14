-- docker compose exec postgres bash
-- psql --dbname=fussballverein --username=fussballverein [--file=/sql/V1.0__Create.sql]

CREATE TABLE IF NOT EXISTS fussballverein (
    id            uuid PRIMARY KEY USING INDEX TABLESPACE fussballvereinspace,
    version       integer NOT NULL DEFAULT 0,
    name          varchar(40) NOT NULL,
    email         varchar(40) NOT NULL UNIQUE USING INDEX TABLESPACE fussballvereinspace,
    gruendungsdatum  date CHECK (gruendungsdatum <= current_date),
    plz           char(5) NOT NULL CHECK (plz ~ '\d{5}'),
    telefonnummer varchar(15) NOT NULL CHECK (telefonnummer ~ '0[1-9]{1,4} ?[0-9]{4,9}'),
    erzeugt       timestamp NOT NULL,
    aktualisiert  timestamp NOT NULL,
    trainer_id      uuid
) TABLESPACE fussballvereinspace;

CREATE INDEX IF NOT EXISTS fussballverein_name_idx ON fussballverein(name) TABLESPACE fussballvereinspace;

CREATE TABLE IF NOT EXISTS mannschaft (
    id        uuid PRIMARY KEY USING INDEX TABLESPACE fussballvereinspace,
    jugend    varchar(7) CHECK (jugend ~ 'Bambini|F|E|D|C|B|A') NOT NULL,
    anzahl_mitglieder integer not null check (anzahl_mitglieder >= 0),
    fussballverein_id  uuid NOT NULL REFERENCES fussballverein,
    idx       integer NOT NULL DEFAULT 0
) TABLESPACE fussballvereinspace;
CREATE INDEX IF NOT EXISTS mannschaft_fussballverein_id_idx ON mannschaft(fussballverein_id) TABLESPACE fussballvereinspace;
