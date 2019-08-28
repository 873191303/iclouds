/**

时间：	    2017-08-08

说明： 	  新增删除监控服务器相关数据函数

前置版本：	ICloudsV5.2.pdm

当前版本：	ICloudsV5.2.pdm
*/

CREATE OR REPLACE FUNCTION delehost(hid BIGINT)
  RETURNS VARCHAR AS $BODY$
 BEGIN

  /* 创建记录触发器id的临时表 */
  CREATE LOCAL TEMPORARY TABLE IF NOT EXISTS trigger_id (triggerid BIGINT);
	INSERT INTO trigger_id (triggerid)
		SELECT DISTINCT fun.triggerid FROM ipm_pft_functions fun
		WHERE EXISTS(SELECT 1 FROM ipm_pft_items item WHERE item.itemid = fun.itemid AND item.hostid = hid);

  /* 创建记录动作id的临时表 */
	CREATE LOCAL TEMPORARY TABLE IF NOT EXISTS action_id (actionid BIGINT);
	INSERT INTO action_id (actionid)
		SELECT DISTINCT act.actionid FROM ipm_pft_actions act
		WHERE EXISTS(SELECT 1 FROM trigger_id ti WHERE ti.triggerid = act.triggerid);

  /* 创建记录操作id的临时表 */
	CREATE LOCAL TEMPORARY TABLE IF NOT EXISTS operation_id (operationid BIGINT);
	INSERT INTO operation_id (operationid)
		SELECT DISTINCT ope.operationid FROM ipm_pft_operations ope
		WHERE EXISTS(SELECT 1 FROM action_id ai WHERE ai.actionid = ope.actionid);

	DELETE FROM ipm_pft_opcommand opc WHERE EXISTS(SELECT 1 FROM operation_id oi WHERE oi.operationid = opc.operationid);

	DELETE FROM ipm_pft_opmessage opm WHERE EXISTS(SELECT 1 FROM operation_id oi WHERE oi.operationid = opm.operationid);

	DELETE FROM ipm_pft_opmessage2grp opm2g WHERE EXISTS(SELECT 1 FROM operation_id oi WHERE oi.operationid = opm2g.operationid);

	DELETE FROM ipm_pft_opmessage2usr opm2u WHERE EXISTS(SELECT 1 FROM operation_id oi WHERE oi.operationid = opm2u.operationid);

	DELETE FROM ipm_pft_operations ope WHERE EXISTS(SELECT 1 FROM action_id ai WHERE ai.actionid = ope.actionid);

	DELETE FROM ipm_pft_conditions con WHERE EXISTS (SELECT 1 FROM action_id ai WHERE ai.actionid=con.actionid);

	DELETE FROM ipm_pft_actions act WHERE EXISTS (SELECT 1 FROM trigger_id ti WHERE ti.triggerid = act.triggerid);

	DELETE FROM ipm_pft_functions fun WHERE EXISTS(SELECT 1 FROM ipm_pft_items item WHERE item.itemid = fun.itemid AND item.hostid = hid);

	DELETE FROM ipm_pft_triggers tri WHERE EXISTS (SELECT 1 FROM trigger_id ti WHERE ti.triggerid = tri.triggerid);

	DELETE FROM ipm_pft_item2applications i2a WHERE EXISTS(SELECT 1 FROM ipm_pft_items item WHERE item.itemid = i2a.itemid AND item.hostid = hid);

	DELETE FROM ipm_pft_item2condition i2c WHERE EXISTS(SELECT 1 FROM ipm_pft_items item WHERE item.itemid = i2c.itemid AND item.hostid = hid);

	DELETE FROM ipm_pft_httptest2item h2i WHERE EXISTS (SELECT 1 FROM ipm_pft_httptest test WHERE test.httptestid = h2i.httptestid AND test.hostid = hid);

	DELETE FROM ipm_pft_httpstepitem s2i WHERE EXISTS(SELECT 1 FROM ipm_pft_httpstep step WHERE step.httpstepid = s2i.httpstepid AND EXISTS (SELECT 1 FROM ipm_pft_httptest http WHERE http.httptestid = step.httptestid AND http.hostid = hid));

	DELETE FROM ipm_pft_items item WHERE item.hostid = hid;

	DELETE FROM ipm_pft_selfmonitor self WHERE "self".hostid = hid;

	DELETE FROM ipm_pft_httpstep step WHERE EXISTS (SELECT 1 FROM ipm_pft_httptest http WHERE http.httptestid = step.httptestid AND http.hostid = hid);

	DELETE FROM ipm_pft_httptest http WHERE http.hostid = hid;

	DELETE FROM ipm_pft_applications app WHERE app.hostid = hid;

	DELETE FROM ipm_pft_interface inter WHERE inter.hostid = hid;

	DELETE FROM ipm_pft_host2template h2t WHERE h2t.hostid = hid;

	DELETE FROM ipm_pft_host2group h2g WHERE h2g.hostid = hid;

	DELETE FROM ipm_pft_hosts host WHERE "host".hostid = hid;

	RETURN 'success';
END;
 $BODY$
  LANGUAGE plpgsql;