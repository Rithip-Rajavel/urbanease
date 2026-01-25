-- Insert sample service categories
INSERT INTO service_categories (name, description, icon_url, is_active, created_at, updated_at) VALUES
('Home Maintenance', 'Plumbing, electrical, and general home repairs', '/icons/home-maintenance.png', true, NOW(), NOW()),
('Domestic Help', 'Cooking, cleaning, and household assistance', '/icons/domestic-help.png', true, NOW(), NOW()),
('Cleaning Services', 'Professional cleaning and sanitation services', '/icons/cleaning.png', true, NOW(), NOW()),
('Gardening & Landscaping', 'Garden maintenance and landscaping services', '/icons/gardening.png', true, NOW(), NOW()),
('Pest Control', 'Pest extermination and prevention services', '/icons/pest-control.png', true, NOW(), NOW());

-- Insert sample services
INSERT INTO services (name, description, category_id, base_price, estimated_duration, is_active, created_at, updated_at) VALUES
('Plumbing Repair', 'Fix leaking pipes, install fixtures, general plumbing work', 1, 150.00, 120, true, NOW(), NOW()),
('Electrical Work', 'Install outlets, fix wiring, electrical repairs', 1, 200.00, 90, true, NOW(), NOW()),
('House Cleaning', 'Complete home cleaning service', 2, 100.00, 180, true, NOW(), NOW()),
('Cooking Service', 'Daily meal preparation and cooking', 2, 80.00, 120, true, NOW(), NOW()),
('Deep Cleaning', 'Thorough cleaning of all rooms and surfaces', 3, 250.00, 240, true, NOW(), NOW()),
('Garden Maintenance', 'Lawn mowing, weeding, plant care', 4, 120.00, 150, true, NOW(), NOW()),
('Pest Extermination', 'Complete pest control treatment', 5, 300.00, 60, true, NOW(), NOW());

-- Insert admin user
INSERT INTO users (username, password, email, role, is_active, is_available, created_at, updated_at) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@urbanease.com', 'ADMIN', true, false, NOW(), NOW());
