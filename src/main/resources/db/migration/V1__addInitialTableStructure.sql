CREATE TABLE IF NOT EXISTS accommodation (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS county (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS municipality (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS address (
    id SERIAL PRIMARY KEY,
    ads_oid VARCHAR(50) NOT NULL,
    accommodation_id BIGINT REFERENCES accommodation(id),
    address VARCHAR(1000) NOT NULL,
    municipality_id INT REFERENCES municipality(id),
    county_id INT REFERENCES county(id),
    active BOOLEAN DEFAULT TRUE NOT NULL,
    centroid_x VARCHAR(20),
    centroid_y VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS address_accomodation_id_idx ON address (accommodation_id);