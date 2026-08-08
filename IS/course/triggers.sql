BEGIN;

-- unique zone in route constraint
ALTER TABLE segments
ADD CONSTRAINT unique_zone_per_route
UNIQUE(route_id, zone_id);

-- segment connectivity check trigger
CREATE OR REPLACE FUNCTION check_segment_connectivity()
RETURNS TRIGGER AS $$
DECLARE
   previous_zone_id BIGINT;
BEGIN
   IF NEW.step_index < 1 THEN
       RAISE EXCEPTION 
           'Step index must be >= 1 for route ID %', NEW.route_id;
   END IF;

   IF NEW.step_index = 1 THEN
       RETURN NEW;
   END IF;

   SELECT zone_id INTO previous_zone_id
   FROM segments
   WHERE route_id = NEW.route_id
     AND step_index = NEW.step_index - 1;

   IF NOT FOUND THEN
       RAISE EXCEPTION 
           'Previous segment (step_index = %) not found for route ID %',
           NEW.step_index - 1, NEW.route_id;
   END IF;

   IF NOT EXISTS (
       SELECT 1
       FROM zone_connections
       WHERE (zone_id = previous_zone_id AND connected_zone_id = NEW.zone_id)
          OR (zone_id = NEW.zone_id AND connected_zone_id = previous_zone_id)
   ) THEN
       RAISE EXCEPTION 
           'Zones % and % are not directly connected',
           previous_zone_id, NEW.zone_id;
   END IF;

   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS before_segment_check_connectivity ON segments;
CREATE TRIGGER before_segment_check_connectivity
BEFORE INSERT OR UPDATE ON segments
FOR EACH ROW
EXECUTE FUNCTION check_segment_connectivity();

-- supply update after visit completed
CREATE OR REPLACE FUNCTION update_supplies_on_visit_completion()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
        INSERT INTO supplies (name, amount)
        SELECT 
            d.supply_type,
            SUM(d.amount)
        FROM deliveries d
        WHERE d.visit_request_id = NEW.id
        GROUP BY d.supply_type

        ON CONFLICT (name)
        DO UPDATE
        SET amount = supplies.amount + EXCLUDED.amount;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS after_visit_request_completion 
ON visit_requests;

CREATE TRIGGER after_visit_request_completion
AFTER UPDATE OF status ON visit_requests
FOR EACH ROW
WHEN (NEW.status = 'COMPLETED')
EXECUTE FUNCTION update_supplies_on_visit_completion();

-- prevent changing completed routes status
CREATE OR REPLACE FUNCTION prevent_historic_route_modification()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'COMPLETED' THEN
        RAISE EXCEPTION 
            'Cannot modify route (ID: %) because it is already COMPLETED',
            OLD.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS before_route_update_historic_check ON routes;

CREATE TRIGGER before_route_update_historic_check
BEFORE UPDATE ON routes
FOR EACH ROW
EXECUTE FUNCTION prevent_historic_route_modification();

-- check documents for route request
CREATE OR REPLACE FUNCTION check_documents_on_submit()
RETURNS TRIGGER AS $$
DECLARE
    required_docs TEXT[];
    provided_docs TEXT[];
    expired_docs TEXT[];
BEGIN
    IF NEW.status = 'SUBMITTED' AND OLD.status <> 'SUBMITTED' THEN
        required_docs := ARRAY['CAPTAIN_ID', 'SHIP_ID'];

        IF NEW.goal = 'DELIVERY' THEN
            required_docs := array_append(required_docs, 'CARGO_ID');
        END IF;

        SELECT array_agg(d.doc_type)
        INTO provided_docs
        FROM route_documents rd
        JOIN documents d ON rd.document_id = d.id
        WHERE rd.route_request_id = NEW.id;

        IF provided_docs IS NULL THEN
            RAISE EXCEPTION 
                'Cannot submit request ID %. No documents attached.',
                NEW.id;
        END IF;

        IF NOT (required_docs <@ provided_docs) THEN
            RAISE EXCEPTION 
                'Cannot submit request ID %. Missing required documents. Required: %, Provided: %',
                NEW.id, required_docs, provided_docs;
        END IF;

        SELECT array_agg(d.doc_type)
        INTO expired_docs
        FROM route_documents rd
        JOIN documents d ON rd.document_id = d.id
        WHERE rd.route_request_id = NEW.id
          AND d.valid_until < CURRENT_DATE;

        IF expired_docs IS NOT NULL THEN
            RAISE EXCEPTION 
                'Cannot submit request ID %. Expired documents: %',
                NEW.id, expired_docs;
        END IF;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS trigger_check_documents_on_submit ON route_requests;

CREATE TRIGGER trigger_check_documents_on_submit
BEFORE UPDATE ON route_requests
FOR EACH ROW
EXECUTE FUNCTION check_documents_on_submit();

COMMIT;