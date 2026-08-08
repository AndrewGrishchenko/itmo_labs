CREATE UNIQUE INDEX one_active_complaint_per_video
ON complaints(video_id)
WHERE status IN ('PENDING_MODERATOR', 'ACCEPTED_BY_AUTO');