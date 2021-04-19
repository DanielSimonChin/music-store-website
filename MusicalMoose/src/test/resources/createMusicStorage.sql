DROP DATABASE IF EXISTS MUSICSTORAGE;
DROP USER IF EXISTS music@localhost;
DROP DATABASE IF EXISTS CSgb4w21;

CREATE DATABASE CSgb4w21;

USE CSgb4w21;

DROP USER IF EXISTS CSgb4w21@localhost;

CREATE USER CSgb4w21@'localhost' IDENTIFIED WITH mysql_native_password BY 'ssecomal' REQUIRE NONE;

CREATE USER CSgb4w21@'%' IDENTIFIED WITH mysql_native_password BY 'ssecomal' REQUIRE NONE;

GRANT ALL ON CSgb4w21.* TO CSgb4w21@'localhost';

GRANT ALL ON CSgb4w21.* TO CSgb4w21@'%';

FLUSH PRIVILEGES;
