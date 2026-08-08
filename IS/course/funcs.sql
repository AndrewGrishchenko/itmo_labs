BEGIN;

-- 1
-- Fill segments for route
CREATE OR REPLACE PROCEDURE proc_build_route_segments(
   p_route_id BIGINT,
   p_zone_ids BIGINT[]
)
LANGUAGE plpgsql
AS $$
DECLARE
   v_zone_id BIGINT;
   v_step_index INT := 1;
   v_status TEXT;
   v_source_zone BIGINT;
   v_target_zone BIGINT;
BEGIN
   SELECT r.status, rr.source_zone_id, rr.target_zone_id
   INTO v_status, v_source_zone, v_target_zone
   FROM routes r
   JOIN route_requests rr ON rr.id = r.route_request_id
   WHERE r.id = p_route_id;

   IF NOT FOUND THEN
       RAISE EXCEPTION 'Route with ID % not found', p_route_id;
   END IF;

   IF v_status <> 'DRAFT' THEN
       RAISE EXCEPTION 'Cannot build segments for route ID % because status is %, expected DRAFT',
           p_route_id, v_status;
   END IF;

   IF p_zone_ids IS NULL OR array_length(p_zone_ids,1) IS NULL THEN
       RAISE EXCEPTION 'Zone list cannot be empty for route ID %', p_route_id;
   END IF;

   IF p_zone_ids[1] <> v_source_zone THEN
       RAISE EXCEPTION 'First zone of route (% ) does not match source zone (%)',
           p_zone_ids[1], v_source_zone;
   END IF;

   IF p_zone_ids[array_length(p_zone_ids,1)] <> v_target_zone THEN
       RAISE EXCEPTION 'Last zone of route (% ) does not match target zone (%)',
           p_zone_ids[array_length(p_zone_ids,1)], v_target_zone;
   END IF;

   DELETE FROM segments WHERE route_id = p_route_id;

   FOREACH v_zone_id IN ARRAY p_zone_ids
   LOOP
       INSERT INTO segments(route_id, zone_id, step_index)
       VALUES (p_route_id, v_zone_id, v_step_index);

       v_step_index := v_step_index + 1;
   END LOOP;
END;
$$;


-- 2
-- Check Requirement fulfillment
CREATE OR REPLACE FUNCTION func_check_requirement_fulfilled(
    p_requirement_id BIGINT
)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
    v_user_id      BIGINT;
    v_start_date   DATE;
    v_until_date   DATE;
    v_condition    RECORD;
    v_exists       BOOLEAN;
BEGIN
    SELECT user_id, start_date, until_date
    INTO v_user_id, v_start_date, v_until_date
    FROM requirements
    WHERE id = p_requirement_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Requirement with ID % not found', p_requirement_id;
    END IF;

    FOR v_condition IN
        SELECT zone_id,
               ship_type,
               action_type
        FROM requirement_conditions
        WHERE requirement_id = p_requirement_id
    LOOP

        SELECT EXISTS (
            SELECT 1
            FROM routes r
            JOIN route_requests rr ON rr.id = r.route_request_id
            JOIN ships s ON s.id = rr.ship_id
            JOIN segments seg ON seg.route_id = r.id
            WHERE s.captain_id = v_user_id
              AND r.status = 'COMPLETED'
              AND r.date BETWEEN v_start_date AND v_until_date
              AND s.ship_type = v_condition.ship_type
              AND seg.zone_id = v_condition.zone_id
        )
        INTO v_exists;

        IF v_condition.action_type = 'VISIT' THEN
            IF NOT v_exists THEN
                RETURN FALSE;
            END IF;

        ELSIF v_condition.action_type = 'NOT_VISIT' THEN
            IF v_exists THEN
                RETURN FALSE;
            END IF;
        END IF;

    END LOOP;

    RETURN TRUE;
END;
$$;


-- 3
-- active routes summary
CREATE OR REPLACE FUNCTION func_get_active_routes_summary()
RETURNS TABLE (
   route_id BIGINT,
   ship_id BIGINT,
   captain_fullname VARCHAR,
   ship_type VARCHAR,
   goal VARCHAR,
   full_path TEXT
)
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
   RETURN QUERY
   SELECT
       r.id,
       s.id,
       u.fullname,
       s.ship_type,
       rr.goal,
       string_agg(z.name, ' -> ' ORDER BY sg.step_index) AS full_path
   FROM routes r
   JOIN route_requests rr ON r.route_request_id = rr.id
   JOIN ships s ON rr.ship_id = s.id
   JOIN users u ON s.captain_id = u.id
   LEFT JOIN segments sg ON sg.route_id = r.id
   LEFT JOIN zones z ON sg.zone_id = z.id
   WHERE r.status = 'IN_PROGRESS'
   GROUP BY r.id, s.id, u.fullname, s.ship_type, rr.goal;
END;
$$;

-- 4
-- ship history
CREATE OR REPLACE FUNCTION func_get_ship_history(
   p_ship_id BIGINT
)
RETURNS TABLE (
   route_id BIGINT,
   route_status VARCHAR,
   route_date DATE,
   goal VARCHAR,
   complaint_case_id BIGINT,
   case_description VARCHAR
)
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM ships WHERE id = p_ship_id) THEN
       RAISE EXCEPTION 'Ship with ID % not found', p_ship_id;
   END IF;

   RETURN QUERY
   SELECT
       r.id,
       r.status,
       r.date,
       rr.goal,
       cs.id,
       cs.description
   FROM ships s
   JOIN route_requests rr 
       ON s.id = rr.ship_id
   JOIN routes r 
       ON rr.id = r.route_request_id
   LEFT JOIN complaints cmpl 
       ON rr.id = cmpl.route_request_id
   LEFT JOIN cases cs 
       ON cmpl.case_id = cs.id
   WHERE s.id = p_ship_id
   ORDER BY r.date DESC;
END;
$$;

COMMIT;