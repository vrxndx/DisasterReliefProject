-- ============================================================
-- Disaster Relief Management System — MySQL Schema
-- Run this script once: mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS disasterrelief;
USE disasterrelief;

-- 1. Incidents
CREATE TABLE IF NOT EXISTS incidents (
    incident_id INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    date        DATE         NOT NULL,
    location    VARCHAR(200) NOT NULL,
    description TEXT,
    severity_level VARCHAR(20) DEFAULT 'Medium'
);

-- 2. Victims
CREATE TABLE IF NOT EXISTS victims (
    victim_id    INT AUTO_INCREMENT PRIMARY KEY,
    incident_id  INT,
    name         VARCHAR(100) NOT NULL,
    status       VARCHAR(30)  DEFAULT 'Affected',
    contact_info VARCHAR(100),
    address      VARCHAR(200),
    FOREIGN KEY (incident_id) REFERENCES incidents(incident_id) ON DELETE SET NULL
);

-- 3. Relief Requests
CREATE TABLE IF NOT EXISTS relief_requests (
    req_id            INT AUTO_INCREMENT PRIMARY KEY,
    victim_id         INT NOT NULL,
    date_requested    DATETIME DEFAULT CURRENT_TIMESTAMP,
    status            VARCHAR(30) DEFAULT 'Pending',
    needs_description TEXT NOT NULL,
    priority          VARCHAR(20) DEFAULT 'Normal',
    FOREIGN KEY (victim_id) REFERENCES victims(victim_id) ON DELETE CASCADE
);

-- 4. Centers
CREATE TABLE IF NOT EXISTS centers (
    center_id    INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    location     VARCHAR(200) NOT NULL,
    capacity     INT,
    contact_info VARCHAR(100)
);

-- 5. Teams
CREATE TABLE IF NOT EXISTS teams (
    team_id        INT AUTO_INCREMENT PRIMARY KEY,
    center_id      INT,
    team_name      VARCHAR(100) NOT NULL,
    specialization VARCHAR(50),
    status         VARCHAR(30) DEFAULT 'Available',
    FOREIGN KEY (center_id) REFERENCES centers(center_id) ON DELETE SET NULL
);

-- 6. Resources
CREATE TABLE IF NOT EXISTS resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    center_id   INT,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(50),
    quantity    INT DEFAULT 0,
    unit        VARCHAR(30),
    FOREIGN KEY (center_id) REFERENCES centers(center_id) ON DELETE SET NULL
);

-- 7. Assignments (maps teams → relief requests)
CREATE TABLE IF NOT EXISTS assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    team_id       INT NOT NULL,
    req_id        INT,
    date_assigned DATETIME DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(30) DEFAULT 'Active',
    FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE,
    FOREIGN KEY (req_id)  REFERENCES relief_requests(req_id) ON DELETE SET NULL
);

-- ============================================================
-- Sample Data
-- ============================================================

INSERT INTO incidents (title, type, date, location, description, severity_level) VALUES
('Kerala Floods 2024',   'Flood',      '2024-08-15', 'Wayanad, Kerala',       'Severe flooding and landslides', 'High'),
('Chennai Cyclone',      'Cyclone',    '2024-12-03', 'Chennai, Tamil Nadu',   'Cyclone Michaung landfall',      'High'),
('Delhi Fire Outbreak',  'Fire',       '2025-01-20', 'Mundka, New Delhi',     'Industrial area fire',           'Medium'),
('Assam Earthquake',     'Earthquake', '2025-06-10', 'Guwahati, Assam',       'Magnitude 5.2 tremor',           'Low');

INSERT INTO centers (name, location, capacity, contact_info) VALUES
('Central Relief Hub',    'New Delhi',   500, '011-2345678'),
('Southern Relief Center','Chennai',     300, '044-9876543'),
('Eastern Relief Base',   'Guwahati',    200, '0361-1234567');

INSERT INTO victims (incident_id, name, status, contact_info, address) VALUES
(1, 'Arjun Menon',    'Injured',  '9876543210', 'Wayanad, Kerala'),
(1, 'Priya Nair',     'Affected', '9876543211', 'Wayanad, Kerala'),
(2, 'Ravi Kumar',     'Safe',     '9876543212', 'Chennai, TN'),
(3, 'Sunita Sharma',  'Affected', '9876543213', 'Mundka, Delhi');

INSERT INTO relief_requests (victim_id, needs_description, status, priority) VALUES
(1, 'Medical aid and shelter',     'In Progress', 'Urgent'),
(2, 'Food, water, and clothing',   'Pending',     'Normal'),
(4, 'Temporary shelter',           'Pending',     'Normal');

INSERT INTO teams (center_id, team_name, specialization, status) VALUES
(1, 'Alpha Rescue',     'Rescue',    'Deployed'),
(1, 'Bravo Medical',    'Medical',   'Available'),
(2, 'Delta Logistics',  'Logistics', 'Available'),
(3, 'Echo Rescue',      'Rescue',    'Available');

INSERT INTO resources (center_id, name, type, quantity, unit) VALUES
(1, 'Water Bottles',  'Supplies',  5000, 'bottles'),
(1, 'First Aid Kits', 'Medical',    200, 'kits'),
(2, 'Blankets',       'Supplies',  1500, 'pieces'),
(2, 'Tarpaulins',     'Equipment',  300, 'sheets'),
(3, 'Rice Bags',      'Food',       800, 'bags');

INSERT INTO assignments (team_id, req_id, status) VALUES
(1, 1, 'Active');
