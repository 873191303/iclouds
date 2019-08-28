create view clusters_server2vm as
	SELECT
		ic.id,
		ic.hostid,
		ic.cpu,
		ic.memory,
		ic.storage,
		ic.custertid,
		clus.phostid
	FROM (
		SELECT
			serv.id,
			serv.hostid,
			serv.cpu,
			serv.memory,
			serv.storage,
			item.custertid
		FROM cmdb_cloud_server2vm serv, cmdb_server_cluster2items item
		WHERE item.id = serv.hostid
	) ic, cmdb_server_clusters clus
	WHERE clus.id = ic.custertid