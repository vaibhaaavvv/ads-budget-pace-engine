#!/bin/bash

# Budget Pace Engine - Data Initialization Script
# This script loads campaigns and ads into PostgreSQL database

DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="budget_pace"
DB_USER="postgres"
DB_PASSWORD="postgres"

echo "Initializing Budget Pace Engine Database..."

# Create database if not exists
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | grep -q 1 || psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME"

# Insert sample campaigns
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME <<EOF

-- Insert campaigns
INSERT INTO campaigns (campaign_id, daily_spend_limit, start_date, expiry_date) 
VALUES 
    ('campaign1', 100, '2025-01-10', '2025-01-18'),
    ('campaign2', 500, '2025-01-01', '2025-02-01'),
    ('campaign3', 1000, '2025-01-15', '2025-03-15')
ON CONFLICT (campaign_id) DO NOTHING;

-- Insert ads
INSERT INTO ads (ad_id, campaign_id) 
VALUES 
    ('ad1', 'campaign1'),
    ('ad2', 'campaign1'),
    ('ad3', 'campaign1'),
    ('ad4', 'campaign2'),
    ('ad5', 'campaign2'),
    ('ad6', 'campaign3')
ON CONFLICT (ad_id) DO NOTHING;

EOF

echo "Database initialization complete!"
echo "Campaigns and ads have been loaded into PostgreSQL."
echo "Start the Spring Boot application to load data into Redis cache."
