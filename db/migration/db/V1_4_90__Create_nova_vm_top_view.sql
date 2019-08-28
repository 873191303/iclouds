DROP VIEW IF EXISTS vm_top;
CREATE VIEW vm_top AS (SELECT
	vm.uuid,
	vm.id,
	vm.hostname,
	vm.vcpus,
	vm.memory,
	vm.ramdisk,
	vm.owner,
  vm.projectid,
	(
		SELECT
			val.keyvalue
		FROM
			ipm_pfm_values val
		LEFT JOIN ipm_cas_item item ON val.item = item. ID
		WHERE
			val.uuid = vm.uuid
		AND item.item = 'cpuRate'
	) AS cpurate,
	(
		SELECT
			val.keyvalue
		FROM
			ipm_pfm_values val
		LEFT JOIN ipm_cas_item item ON val.item = item. ID
		WHERE
			val.uuid = vm.uuid
		AND item.item = 'memRate'
	) AS memrate,
	(
		SELECT
			val.collecttime
		FROM
			ipm_pfm_values val
		LEFT JOIN ipm_cas_item item ON val.item = item. ID
		WHERE
			val.uuid = vm.uuid
		AND item.item = 'memRate'
	) AS collecttime
FROM
	iyun_nova_vm vm);