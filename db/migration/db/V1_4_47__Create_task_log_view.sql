CREATE VIEW task_log_view AS (
  SELECT
    a.*
  FROM
    iyun_base_tasksh a
  UNION ALL
  SELECT
    b.*
  FROM
    iyun_base_tasks b
)