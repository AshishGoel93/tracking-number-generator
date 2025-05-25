  CREATE TABLE IF NOT EXISTS audit_log (
      id VARCHAR(50) PRIMARY KEY,
      tracking_number VARCHAR(50),
      origin_country_id VARCHAR(10),
      destination_country_id VARCHAR(10),
      customer_id VARCHAR(100),
      customer_slug VARCHAR(100),
      customer_name VARCHAR(100),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );