CREATE TABLE cliente (
	nombres VARCHAR(128) NOT NULL,
	primer_apellido VARCHAR(128) NOT NULL,
	segundo_apellido VARCHAR(128),
	direccion VARCHAR(512) NOT NULL,
	curp CHAR(18) PRIMARY KEY,
	fecha_nacimiento DATE NOT NULL
);

CREATE TABLE poliza (
	clave UUID PRIMARY KEY,
	tipo INT NOT NULL,
	monto NUMERIC(9,2) NOT NULL,
	descripcion TEXT NOT NULL,
	curp_cliente VARCHAR(18) REFERENCES cliente(curp) ON DELETE CASCADE NOT NULL
);

CREATE TABLE beneficiario_poliza (
	nombres VARCHAR(128) NOT NULL,
	primer_apellido VARCHAR(128) NOT NULL,
	segundo_apellido VARCHAR(128) NOT NULL DEFAULT '',
	fecha_nacimiento DATE NOT NULL,
	clave_poliza UUID REFERENCES poliza(clave) ON DELETE CASCADE NOT NULL,
	porcentaje INT NOT NULL
);
