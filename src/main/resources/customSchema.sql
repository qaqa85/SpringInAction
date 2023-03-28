CREATE TABLE IF NOT EXISTS taco_order (
    id identity,
    delivery_name varchar(50) NOT NULL,
    delivery_street varchar(50) NOT NULL,
    delivery_city varchar(50) NOT NULL,
    delivery_state varchar(2) NOT NULL,
    delivery_zip varchar(10) NOT NULL,
    cc_number varchar(16) NOT NULL,
    cc_expiration varchar(5) NOT NULL,
    cc_cvv varchar(3) NOT NULL,
    placed_at timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS taco (
    id identity,
    name varchar(50) NOT NULL,
    taco_order bigint NOT NULL,
    taco_order_id bigint NOT NULL,
    created_at timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredient_ref (
    ingredient varchar(4) NOT NULL,
    taco varchar(25) NOT NULL,
    type varchar(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredient (
    id varchar(4) NOT NULL unique,
    name varchar(25) NOT NULL,
    type varchar(10) NOT NULL
);

ALTER TABLE taco ADD FOREIGN KEY (taco_order_id) REFERENCES taco_order(id);
ALTER TABLE ingredient_ref ADD FOREIGN KEY (ingredient) REFERENCES ingredient(id);