USE fitness_tracker;
INSERT INTO users (name,email,password_hash,role,weight_kg,height_cm) VALUES
('Avery Admin','admin@fit.com',CONCAT('sha256$fitdemo2026$', SHA2(CONCAT('fitdemo2026','Admin@123'),256)),'ADMIN',72,178),
('Jordan Lee','jordan@fit.com',CONCAT('sha256$fitdemo2026$', SHA2(CONCAT('fitdemo2026','MoveMore@123'),256)),'USER',68,172),
('Sam Rivera','sam@fit.com',CONCAT('sha256$fitdemo2026$', SHA2(CONCAT('fitdemo2026','MoveMore@123'),256)),'USER',81,181)
ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO workouts (user_id,type,duration_min,intensity,calories,workout_date,notes)
SELECT u.id, x.type, x.duration_min, x.intensity, x.calories, x.workout_date, x.notes
FROM users u JOIN (
  SELECT 'jordan@fit.com' email,'RUNNING' type,38 duration_min,'HIGH' intensity,412 calories,CURDATE() workout_date,'Tempo intervals' notes UNION ALL
  SELECT 'jordan@fit.com','CYCLING',52,'MEDIUM',386,DATE_SUB(CURDATE(),INTERVAL 1 DAY),'River path ride' UNION ALL
  SELECT 'jordan@fit.com','STRENGTH',45,'HIGH',298,DATE_SUB(CURDATE(),INTERVAL 3 DAY),'Lower body' UNION ALL
  SELECT 'sam@fit.com','YOGA',35,'LOW',118,DATE_SUB(CURDATE(),INTERVAL 1 DAY),'Recovery flow' UNION ALL
  SELECT 'sam@fit.com','RUNNING',30,'MEDIUM',278,DATE_SUB(CURDATE(),INTERVAL 2 DAY),'Easy miles'
) x ON u.email=x.email
WHERE NOT EXISTS (SELECT 1 FROM workouts w WHERE w.user_id=u.id AND w.workout_date=x.workout_date AND w.notes=x.notes);
INSERT INTO goals (user_id,goal_type,target_value,start_date,end_date,status)
SELECT id,'MONTHLY_CALORIES',3000,DATE_FORMAT(CURDATE(),'%Y-%m-01'),LAST_DAY(CURDATE()),'ACTIVE'
FROM users WHERE email='jordan@fit.com' AND NOT EXISTS (SELECT 1 FROM goals WHERE user_id=users.id AND goal_type='MONTHLY_CALORIES');
INSERT INTO challenges (title,description,start_date,end_date,target_value,status)
SELECT '14-Day Momentum','Stack consistent movement sessions for two weeks.',CURDATE(),DATE_ADD(CURDATE(),INTERVAL 14 DAY),10,'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM challenges WHERE title='14-Day Momentum');
INSERT INTO challenges (title,description,start_date,end_date,target_value,status)
SELECT 'October Calorie Circuit','Burn 4,000 active calories this month.',DATE_FORMAT(CURDATE(),'%Y-%m-01'),LAST_DAY(CURDATE()),4000,'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM challenges WHERE title='October Calorie Circuit');
INSERT INTO system_settings(setting_key,setting_value) VALUES ('site_name','PULSE / Athlete HUD'),('max_challenges','12'),('maintenance_mode','false')
ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value);