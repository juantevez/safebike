-- Tabla para países
CREATE TABLE country (
    country_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla de marcas con clave foránea a country
CREATE TABLE brand (
    brand_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    country_id INTEGER REFERENCES country(country_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Tabla de tipos de bicicleta
CREATE TABLE bike_type (
    bike_type_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tipos de marco
CREATE TABLE frame_type (
    frame_type_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-------- VER USER.SQL

-- Tabla principal de bicicletas (normalizada)
CREATE TABLE bike (
    bikes_id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    brand_id INTEGER NOT NULL,
    serial_number VARCHAR(255),
    bike_type_id INTEGER NOT NULL,
    frame_type_id INTEGER NOT NULL,
    purchase_date DATE,
    purchase_value DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Claves foráneas
    CONSTRAINT fk_bike_user
        FOREIGN KEY (user_id) REFERENCES "user"(user_id),
    CONSTRAINT fk_bike_brand
        FOREIGN KEY (brand_id) REFERENCES brand(brand_id),
    CONSTRAINT fk_bike_type
        FOREIGN KEY (bike_type_id) REFERENCES bike_type(bike_type_id),
    CONSTRAINT fk_bike_frame_type
        FOREIGN KEY (frame_type_id) REFERENCES frame_type(frame_type_id)
);

-- Índices para mejorar performance en consultas
CREATE INDEX idx_bike_user_id ON bike(user_id);
CREATE INDEX idx_bike_brand_id ON bike(brand_id);
CREATE INDEX idx_bike_type_id ON bike(bike_type_id);
CREATE INDEX idx_bike_frame_type_id ON bike(frame_type_id);
CREATE INDEX idx_bike_serial_number ON bike(serial_number);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_username ON "user"(username);

-- Datos de ejemplo para las tablas de referencia
INSERT INTO brand (name) VALUES
    ('Trek'),
    ('Giant'),
    ('Specialized'),
    ('Cannondale'),
    ('Bianchi');

INSERT INTO bike_type (name, description) VALUES
    ('Mountain', 'Bicicletas diseñadas para terrenos montañosos'),
    ('Road', 'Bicicletas de carretera para velocidad'),
    ('Hybrid', 'Combinación entre mountain y road'),
    ('BMX', 'Bicicletas para acrobacias y competencias'),
    ('Electric', 'Bicicletas con motor eléctrico');

INSERT INTO frame_type (name, description) VALUES
    ('Aluminum', 'Marco de aluminio, ligero y resistente'),
    ('Carbon Fiber', 'Marco de fibra de carbono, muy ligero'),
    ('Steel', 'Marco de acero, duradero y clásico'),
    ('Titanium', 'Marco de titanio, ligero y premium'),
    ('Alloy', 'Marco de aleación metálica');