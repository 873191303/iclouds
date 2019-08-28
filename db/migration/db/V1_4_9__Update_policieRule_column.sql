
/**

时间：	    2017年2月16日

说明： 	 修改防火墙规则位置字段为int型

*/

ALTER TABLE public.iyun_policie_rule ALTER COLUMN position TYPE INT USING position::INT;