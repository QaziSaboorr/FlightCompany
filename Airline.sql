
-- Create the database
CREATE DATABASE IF NOT EXISTS airlinedb;
USE airlinedb;

-- Users Table
CREATE TABLE IF NOT EXISTS Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    UserName VARCHAR(255),
    UserType VARCHAR(50),
    Email VARCHAR(255),
    Password VARCHAR(255),
    Address VARCHAR(255),
    IsMember BOOLEAN DEFAULT FALSE, -- New column for membership status
    HasCompanyCreditCard BOOLEAN DEFAULT FALSE, -- New column for company credit card status
    HasRedeemedCompanionTicket BOOLEAN DEFAULT FALSE, -- New column for companion ticket redemption status
    INDEX(UserName),
    INDEX(Email)
);


-- Flights Table
CREATE TABLE IF NOT EXISTS Flights (
    FlightID INT PRIMARY KEY AUTO_INCREMENT,
    FlightNumber VARCHAR(50),
    Origin VARCHAR(255),
    Destination VARCHAR(255),
    AircraftID INT,
    INDEX (Destination),  -- Add this line to create an index on Destination
    FOREIGN KEY (AircraftID) REFERENCES Aircrafts(AircraftID)
);


-- Seats Table
CREATE TABLE IF NOT EXISTS Seats (
    SeatID INT PRIMARY KEY AUTO_INCREMENT,
    FlightID INT,
    SeatNumber VARCHAR(10),
    SeatType VARCHAR(20),
    SeatPrice DECIMAL(10, 2),
    INDEX (SeatID),
    INDEX (SeatType),
    INDEX (SeatNumber),
    FOREIGN KEY (FlightID) REFERENCES Flights(FlightID)
);


-- Payments Table
CREATE TABLE IF NOT EXISTS Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT,
    FlightID INT,
    PaymentAmount DECIMAL(10, 2),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (FlightID) REFERENCES Flights(FlightID)
);


-- Crews Table
CREATE TABLE IF NOT EXISTS Crews (
    CrewID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(255),
    FlightID INT,
    FOREIGN KEY (FlightID) REFERENCES Flights(FlightID)
);

-- Aircrafts Table
CREATE TABLE IF NOT EXISTS Aircrafts (
    AircraftID INT PRIMARY KEY AUTO_INCREMENT,
    AircraftNumber VARCHAR(50)
);

-- Destinations Table
CREATE TABLE IF NOT EXISTS Destinations (
    DestinationID INT PRIMARY KEY AUTO_INCREMENT,
    DestinationName VARCHAR(255)
);


-- Tickets Table
CREATE TABLE IF NOT EXISTS Tickets (
    TicketID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT,
    Email VARCHAR(255),
    UserName VARCHAR(255),
    FlightID INT,
    SeatID INT,
    SeatType VARCHAR(20),
    SeatNumber VARCHAR(10),
    Destination VARCHAR(255),
    IsCancelled BOOLEAN DEFAULT 0,
    InsuranceSelected BOOLEAN DEFAULT 0,
    PaymentAmount DECIMAL(10, 2),
    PaymentDate TIMESTAMP,
    EmailSent BOOLEAN DEFAULT 0,
    ReceiptSent BOOLEAN DEFAULT 0,
    CancellationDate TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (UserName) REFERENCES Users(UserName),
    FOREIGN KEY (Email) REFERENCES Users(Email),
    FOREIGN KEY (FlightID) REFERENCES Flights(FlightID),
    FOREIGN KEY (SeatID) REFERENCES Seats(SeatID),
    FOREIGN KEY (SeatType) REFERENCES Seats(SeatType),
    FOREIGN KEY (SeatNumber) REFERENCES Seats(SeatNumber),
    FOREIGN KEY (Destination) REFERENCES Flights(Destination)
);

-- Passengers Table
CREATE TABLE IF NOT EXISTS Passengers (
    PassengerID INT PRIMARY KEY AUTO_INCREMENT,
    TicketID INT,
    FlightID INT,
    PassengerName VARCHAR(255),
    FOREIGN KEY (TicketID) REFERENCES Tickets(TicketID),
    FOREIGN KEY (FlightID) REFERENCES Flights(FlightID)
);

-- -- Data Insert 

-- Insert fake data into the Aircrafts table
INSERT INTO Aircrafts (AircraftNumber) VALUES
    ('Boeing 737'),   
    ('Airbus A320'),  
    ('Boeing 747'),   
    ('Airbus A380'), 
    ('Boeing 777');   

-- Display the contents of the Aircrafts table
SELECT * FROM airlinedb.Aircrafts;

-- Insert fake data into the Crews table (total crews for each flight)
INSERT INTO Crews (Name, FlightID) VALUES
    ('Sky Explorers', 1),   
    ('Galactic Navigators', 2), 
    ('Dream Flyers', 3),     
    ('Skywalkers', 4),       
    ('Cloud Surfers', 5);    

-- Display the contents of the Crews table
SELECT * FROM airlinedb.crews;


-- Insert unique destinations from the Flights table
-- Check the contents of the Destinations table
INSERT INTO Destinations (DestinationName)
SELECT DISTINCT Destination
FROM (
    SELECT Destination FROM Flights
) AS AllDestinations;

-- Check the contents of the Destinations table
SELECT * FROM Destinations;
-- Inserting fake data into the Seats table for a plane with 4 seats per row for multiple flights
INSERT INTO Seats (FlightID, SeatNumber, SeatType, SeatPrice) VALUES
    -- Seats for Flight ID 1
    (1, 'A1', 'Business-Class', 200.00),
    (1, 'A2', 'Business-Class', 200.00),
    (1, 'A3', 'Business-Class', 200.00),
    (1, 'A4', 'Business-Class', 200.00),
    (1, 'B1', 'Business-Class', 200.00),
    (1, 'B2', 'Business-Class', 200.00),
    (1, 'B3', 'Business-Class', 100.00),
    (1, 'B4', 'Business-Class', 100.00),
    (1, 'C1', 'Regular', 100.00),
    (1, 'C2', 'Regular', 100.00),
    (1, 'C3', 'Regular', 100.00),
    (1, 'C4', 'Regular', 100.00),
    (1, 'D1', 'Regular', 100.00),
    (1, 'D2', 'Regular', 100.00),
    (1, 'D3', 'Regular', 100.00),
    (1, 'D4', 'Regular', 100.00),

    -- Seats for Flight ID 2
    (2, 'A1', 'Business-Class', 200.00),
    (2, 'A2', 'Business-Class', 200.00),
    (2, 'A3', 'Business-Class', 200.00),
    (2, 'A4', 'Business-Class', 200.00),
    (2, 'B1', 'Business-Class', 200.00),
    (2, 'B2', 'Business-Class', 200.00),
    (2, 'B3', 'Business-Class', 100.00),
    (2, 'B4', 'Business-Class', 100.00),
    (2, 'C1', 'Regular', 100.00),
    (2, 'C2', 'Regular', 100.00),
    (2, 'C3', 'Regular', 100.00),
    (2, 'C4', 'Regular', 100.00),
    (2, 'D1', 'Regular', 100.00),
    (2, 'D2', 'Regular', 100.00),
    (2, 'D3', 'Regular', 100.00),
    (2, 'D4', 'Regular', 100.00),

    -- Seats for Flight ID 3
    (3, 'A1', 'Business-Class', 200.00),
    (3, 'A2', 'Business-Class', 200.00),
    (3, 'A3', 'Business-Class', 200.00),
    (3, 'A4', 'Business-Class', 200.00),
    (3, 'B1', 'Business-Class', 200.00),
    (3, 'B2', 'Business-Class', 200.00),
    (3, 'B3', 'Business-Classr', 100.00),
    (3, 'B4', 'Business-Class', 100.00),
    (3, 'C1', 'Regular', 100.00),
    (3, 'C2', 'Regular', 100.00),
    (3, 'C3', 'Regular', 100.00),
    (3, 'C4', 'Regular', 100.00),
    (3, 'D1', 'Regular', 100.00),
    (3, 'D2', 'Regular', 100.00),
    (3, 'D3', 'Regular', 100.00),
    (3, 'D4', 'Regular', 100.00),

	-- Seats for Flight ID 4
	(4, 'A1', 'Business-Class', 200.00),
	(4, 'A2', 'Business-Class', 200.00),
	(4, 'A3', 'Business-Class', 200.00),
	(4, 'A4', 'Business-Class', 200.00),
	(4, 'B1', 'Business-Class', 200.00),
	(4, 'B2', 'Business-Class', 200.00),
	(4, 'B3', 'Business-Class', 100.00),
	(4, 'B4', 'Business-Class', 100.00),
	(4, 'C1', 'Regular', 100.00),
	(4, 'C2', 'Regular', 100.00),
	(4, 'C3', 'Regular', 100.00),
	(4, 'C4', 'Regular', 100.00),
	(4, 'D1', 'Regular', 100.00),
	(4, 'D2', 'Regular', 100.00),
	(4, 'D3', 'Regular', 100.00),
	(4, 'D4', 'Regular', 100.00),

	-- Seats for Flight ID 5
	(5, 'A1', 'Business-Class', 200.00),
	(5, 'A2', 'Business-Class', 200.00),
	(5, 'A3', 'Business-Class', 200.00),
	(5, 'A4', 'Business-Class', 200.00),
	(5, 'B1', 'Business-Class', 200.00),
	(5, 'B2', 'Business-Class', 200.00),
	(5, 'B3', 'Business-Class', 100.00),
	(5, 'B4', 'Business-Class', 100.00),
	(5, 'C1', 'Regular', 100.00),
	(5, 'C2', 'Regular', 100.00),
	(5, 'C3', 'Regular', 100.00),
	(5, 'C4', 'Regular', 100.00),
	(5, 'D1', 'Regular', 100.00),
	(5, 'D2', 'Regular', 100.00),
	(5, 'D3', 'Regular', 100.00),
	(5, 'D4', 'Regular', 100.00);




SELECT * FROM airlinedb.seats;


-- Inserting additional fake data into the Flights table
INSERT INTO Flights (FlightNumber, Origin, Destination, AircraftID) VALUES
    ('AC222', 'Vancouver', 'Edmonton', 1),
    ('AC333', 'Calgary', 'Ottawa', 2),
    ('AC555', 'Ottawa', 'Winnipeg', 1),
    ('AC777', 'Quebec City', 'Halifax', 3),
    ('AC999', 'Winnipeg', 'Montreal', 2);


SELECT * FROM airlinedb.flights;

-- -- Inserting fake data into the Users table

INSERT INTO Users (UserName, UserType, Email, Password, Address, IsMember, HasCompanyCreditCard, HasRedeemedCompanionTicket) VALUES
	('John Doe', 'Registered', 'john.doe@example.com', 'regispass', '123 Main Street', TRUE, FALSE, FALSE),
	('Jane Smith', 'AirlineAgent', 'jane.smith@example.com', 'agentpass', NULL, NULL, NULL, NULL),
	('Admin123', 'SystemAdmin', 'admin@example.com', 'adminPass', NULL, NULL, NULL, NULL),
	('Flight Attendant', 'FlightAttendant', 'flyattend@example.com', 'flyattendpass', NULL, NULL, NULL, NULL),
	('Unregistered User', 'Unregistered', 'unregistered@example.com', NULL, '245 Fake Street', FALSE, FALSE, FALSE);


SELECT * FROM airlinedb.users;
SELECT * FROM airlinedb.tickets;
SELECT * FROM airlinedb.passengers;
SELECT * FROM airlinedb.crews;
