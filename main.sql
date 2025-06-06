DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS MusicBands CASCADE;

CREATE TABLE Users(
    UserId SERIAL PRIMARY KEY NOT NULL,
    Login VARCHAR(64) UNIQUE NOT NULL,
    Password VARCHAR(64) NOT NULL
);

CREATE TABLE MusicBands(
    Id SERIAL PRIMARY KEY NOT NULL,
    Name VARCHAR(64) NOT NULL,
    X DECIMAL NOT NULL CHECK ( X > -823 ),
    Y FLOAT NOT NULL CHECK ( Y <= 752 ),
    CreationDate TIMESTAMP DEFAULT NOW(),
    NumberOfParticipants BIGINT NOT NULL CHECK ( NumberOfParticipants > 0 ),
    Genre VARCHAR(64),
    PersonName VARCHAR(64) NOT NULL,
    PersonBirthday DATE NOT NULL,
    PersonHeight DECIMAL CHECK ( PersonHeight > 0 ),
    PersonWeight INT NOT NULL CHECK ( PersonWeight > 0 ),
    PersonHairColor VARCHAR(64),
    OwnerId INT NOT NULL REFERENCES Users(UserId),

    CONSTRAINT band_equiv UNIQUE(Name, X, Y, NumberOfParticipants, Genre,
    PersonName, PersonBirthday, PersonHeight, PersonWeight, PersonHairColor)
);