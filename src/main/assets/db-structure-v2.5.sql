
CREATE TABLE "company"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "name" TEXT NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT TRUE,
    "kvk" text DEFAULT NULL,	
    "address" text DEFAULT NULL
);
ALTER TABLE
    "company" ADD PRIMARY KEY("id");

CREATE TABLE "user"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "email" TEXT NOT NULL,
    "first_name" TEXT NOT NULL,
    "last_name" TEXT NOT NULL,
    "last_name_prefix" TEXT,
    "type" VARCHAR(255) CHECK
        ("type" IN('STUDENT', 'COMPANY', 'ADMINISTRATOR')) NOT NULL,
        "password" TEXT NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT TRUE,
    "kvk" text DEFAULT NULL,
    "btw" text DEFAULT NULL,	
    "address" text DEFAULT NULL
);
ALTER TABLE
    "user" ADD PRIMARY KEY("id");
ALTER TABLE
    "user" ADD CONSTRAINT "user_email_unique" UNIQUE("email");
COMMENT
ON COLUMN
    "user"."type" IS 'STUDENT
COMPANY
ADMINISTRATOR';

CREATE TABLE "worked_week"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "contract_id" UUID NOT NULL,
    "year" BIGINT NOT NULL,
    "week" BIGINT NOT NULL,
    "note" TEXT NULL,
    "company_note" TEXT NULL,
    "status" TEXT NOT NULL DEFAULT 'NOT_CONFIRMED'::text
);
ALTER TABLE
    "worked_week" ADD CONSTRAINT "worked_week_id_unique" UNIQUE("id");
ALTER TABLE
    "worked_week" ADD PRIMARY KEY("contract_id", "year", "week");

CREATE TABLE "user_contract"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "contract_id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "hourly_wage" BIGINT NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT TRUE
);
ALTER TABLE
    "user_contract" ADD PRIMARY KEY("id");

CREATE TABLE "worked"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "worked_week_id" UUID NOT NULL,
    "day" SMALLINT NOT NULL,
    "minutes" BIGINT NOT NULL,
    "work" TEXT NOT NULL,
    "suggestion" BIGINT DEFAULT NULL
);
ALTER TABLE
    "worked" ADD PRIMARY KEY("id");

CREATE TABLE "company_user"(
    "user_id" UUID NOT NULL,
    "company_id" UUID NOT NULL
);
ALTER TABLE
    "company_user" ADD PRIMARY KEY("user_id", "company_id");

CREATE TABLE "contract"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "company_id" UUID NOT NULL,
    "role" TEXT NOT NULL,
    "description" TEXT NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE "notification"(
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "company_id" UUID NOT NULL,
    "worked_week_id" UUID, 
    "date" TEXT NOT NULL,
    "seen" BOOLEAN NOT NULL DEFAULT FALSE,
    "type" TEXT
);

ALTER TABLE
    "notification" ADD PRIMARY KEY("id");



ALTER TABLE
    "contract" ADD PRIMARY KEY("id");
ALTER TABLE
    "user_contract" ADD CONSTRAINT "user_contract_contract_id_foreign" FOREIGN KEY("contract_id") REFERENCES "contract"("id");
ALTER TABLE
    "worked_week" ADD CONSTRAINT "worked_week_contract_id_foreign" FOREIGN KEY("contract_id") REFERENCES "user_contract"("id");
ALTER TABLE
    "company_user" ADD CONSTRAINT "company_user_company_id_foreign" FOREIGN KEY("company_id") REFERENCES "company"("id");
ALTER TABLE
    "user_contract" ADD CONSTRAINT "user_contract_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("id");
ALTER TABLE
    "worked" ADD CONSTRAINT "worked_worked_week_id_foreign" FOREIGN KEY("worked_week_id") REFERENCES "worked_week"("id");
ALTER TABLE
    "company_user" ADD CONSTRAINT "company_user_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("id");
ALTER TABLE
    "contract" ADD CONSTRAINT "contract_company_id_foreign" FOREIGN KEY("company_id") REFERENCES "company"("id");
ALTER TABLE
    "notification" ADD CONSTRAINT "user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("id");
ALTER TABLE
    "notification" ADD CONSTRAINT "company_id_foreign" FOREIGN KEY("company_id") REFERENCES "company"("id");
ALTER TABLE
    "notification" ADD CONSTRAINT "week_id_foreign" FOREIGN KEY("worked_week_id") REFERENCES worked_week("id");


CREATE OR REPLACE FUNCTION disableContracts()
  RETURNS TRIGGER AS $BODY$

BEGIN
  IF NEW.active = FALSE THEN
    UPDATE contract
    SET active= FALSE
    WHERE company_id = NEW.id;
  END IF;
  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER disableContractsTrigger
AFTER UPDATE ON company
FOR EACH ROW
EXECUTE FUNCTION disableContracts();


CREATE OR REPLACE FUNCTION disableUserContractsFromUser()
  RETURNS TRIGGER AS $BODY$

BEGIN
  IF NEW.active = FALSE THEN
    UPDATE user_contract
    SET active= FALSE
    WHERE user_id = NEW.id;
  END IF;
  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER disableUserContractsFromUserTrigger
AFTER UPDATE ON "user"
FOR EACH ROW
EXECUTE FUNCTION disableUserContractsFromUser();


CREATE OR REPLACE FUNCTION disableUserContractsFromContract()
  RETURNS TRIGGER AS $BODY$

BEGIN
  IF NEW.active = FALSE THEN
    UPDATE user_contract
    SET active= FALSE
    WHERE contract_id = NEW.id;
  END IF;
  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER disableUserContractsFromContractTrigger
AFTER UPDATE ON contract
FOR EACH ROW
EXECUTE FUNCTION disableUserContractsFromContract();


CREATE FUNCTION get_user_id_from_worked_week("value" UUID) 
RETURNS UUID
language plpgsql as $$
DECLARE 
result uuid;
BEGIN
    
    
    SELECT uc.user_id INTO result
    FROM worked_week ww, user_contract uc
    WHERE ww.contract_id = uc.id and ww.contract_id = "value"
    LIMIT 1;
    
    RETURN result;
END;
$$;

CREATE FUNCTION get_company_id_from_worked_week("value" UUID) 
RETURNS UUID
language plpgsql as $$
DECLARE 
result uuid;
BEGIN
    
    
    SELECT c.company_id INTO result
    FROM worked_week ww, user_contract uc, contract c
    WHERE ww.contract_id = uc.id and ww.contract_id = "value" and uc.contract_id=c.id
    LIMIT 1;
    
    RETURN result;
END;
$$;

CREATE FUNCTION get_company_id_from_user_contract("value" UUID) 
RETURNS UUID
language plpgsql as $$
DECLARE 
result uuid;
BEGIN
    
    
    SELECT c.company_id INTO result
    FROM  user_contract uc, contract c
    WHERE uc.contract_id = "value" and uc.contract_id=c.id
    LIMIT 1;
    
    RETURN result;
END;
$$;


CREATE FUNCTION notification_missed_hours()
RETURNS TRIGGER AS $BODY$
DECLARE
  current_week int = date_part('week', current_date);
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
  company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF OLD.week < current_week and OLD.status = 'NOT_CONFIRMED' THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'HOURS');
  END IF;

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_missed_hours
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION notification_missed_hours();


CREATE FUNCTION notification_approved_week()
RETURNS TRIGGER AS $BODY$
DECLARE
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF NEW.status = 'APPROVED' AND OLD.status = 'CONFIRMED' THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'APPROVED');
  END IF;

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_approved_week
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION notification_approved_week();


CREATE FUNCTION notification_rejected_week()
RETURNS TRIGGER AS $BODY$
DECLARE
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
  company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF NEW.status = 'SUGGESTED' AND OLD.status = 'CONFIRMED' THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'SUGGESTION');
  END IF;

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_rejected_week
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION notification_rejected_week();



CREATE FUNCTION notification_accepted_suggested_week()
RETURNS TRIGGER AS $BODY$
DECLARE
  company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF NEW.status = 'APPROVED' and OLD.status = 'SUGGESTED' THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'SUGGESTION ACCEPTED');
  END IF;

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_accepted_suggested_week
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION notification_accepted_suggested_week();



CREATE FUNCTION notification_rejected_suggested_week()
RETURNS TRIGGER AS $BODY$
DECLARE
  company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF NEW.status = 'SUGGESTION_DENIED'  THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'SUGGESTION REJECTED');
  END IF;
  
RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;

CREATE FUNCTION notification_student_company_conflict()
RETURNS TRIGGER AS $BODY$
DECLARE
  company_id UUID = get_company_id_from_worked_week(OLD.contract_id);
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
BEGIN
  

  IF NEW.status = 'SUGGESTION_DENIED'  THEN
    
    INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, OLD.id, CURRENT_DATE, FALSE, 'CONFLICT');
  END IF;

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_student_company_conflict
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION notification_student_company_conflict();


CREATE FUNCTION notification_student_company_link()
RETURNS TRIGGER AS $BODY$
DECLARE
  company_id UUID = get_company_id_from_user_contract(NEW.contract_id);
  user_id UUID =NEW.user_id;
BEGIN
    
    INSERT INTO notification (id, user_id, company_id, date, seen, type)
    VALUES (gen_random_uuid(), user_id, company_id, CURRENT_DATE, FALSE, 'LINK');
  

  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER notify_student_company_link
AFTER INSERT ON user_contract
FOR EACH ROW
EXECUTE FUNCTION notification_student_company_link();



CREATE FUNCTION delete_notification()
RETURNS TRIGGER AS $BODY$
DECLARE
  company UUID = get_company_id_from_worked_week(NEW.contract_id);
  user_id UUID = get_user_id_from_worked_week(OLD.contract_id);
BEGIN
    
    IF NEW.status = 'CONFIRMED' and (OLD.status = 'APPROVED' OR OLD.status = 'SUGGESTED') THEN
    
    DELETE FROM notification 
    WHERE worked_week_id = OLD.id;
  END IF;


  RETURN NEW;
END;
$BODY$ LANGUAGE plpgsql;


CREATE TRIGGER delete_not
AFTER INSERT OR UPDATE OR DELETE ON worked_week
FOR EACH ROW
EXECUTE FUNCTION delete_notification();
