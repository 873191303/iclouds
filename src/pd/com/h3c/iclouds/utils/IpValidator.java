package com.h3c.iclouds.utils;

import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yKF7317 on 2016/11/28.
 */

/**
 * 校验ip
 */
public class IpValidator {
	
	/**
	 * string IP-> byte[]
	 *
	 * @param ipAddr
	 * @return byte[]
	 * 将ip转为字节
	 */
	public static byte[] ipToBytesByInet (String ipAddr) {
		try {
			return InetAddress.getByName(ipAddr).getAddress();
		} catch (Exception e) {
			throw new MessageException(ResultType.ip_format_error);
		}
	}
	
	/**
	 * byte[] -> int
	 *
	 * @param bytes
	 * @return int
	 * 将字节转为int
	 */
	public static int bytesToInt (byte[] bytes) {
		int addr = bytes[3] & 0xFF;
		addr |= ((bytes[2] << 8) & 0xFF00);
		addr |= ((bytes[1] << 16) & 0xFF0000);
		addr |= ((bytes[0] << 24) & 0xFF000000);
		return addr;
	}
	
	/**
	 * int -> String
	 *
	 * @param ipInt
	 * @return String
	 * 将int转为ip
	 */
	public static String intToIp (int ipInt) {
		return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')
				.append((ipInt >> 16) & 0xff).append('.').append(
						(ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))
				.toString();
	}
	
	/**
	 * string IP -> Int
	 *
	 * @param ipAddr
	 * @return int
	 * 将ip转为int
	 */
	public static int ipToInt (String ipAddr) {
		try {
			return bytesToInt(ipToBytesByInet(ipAddr));
		} catch (Exception e) {
			throw new MessageException(ResultType.ip_format_error);
		}
	}
	
	/**
	 * ip/mask -> ip
	 *
	 * @param ipAndMask
	 * @return String
	 * 根据cidr获取ip
	 */
	public static String getIp (String ipAndMask) {
		
		String[] ipArr = ipAndMask.split("/");
		if (ipArr.length != 2) {
			throw new MessageException(ResultType.ip_format_error);
		}
		return ipArr[0];
	}
	
	/**
	 * 检验cidr
	 *
	 * @param cidr
	 * @return
	 */
	public static boolean checkCidr (String cidr) {
		String[] ipArr = cidr.split("/");
		if (ipArr.length != 2) {
			return false;
		}
		String ip = ipArr[0];
		if (!checkIp(ip)) {
			return false;
		}
		int mask = Integer.parseInt(ipArr[1]);
		if (mask < 0 || mask > 32) {
			return false;
		}
		return true;
	}
	
	/**
	 * ip/mask -> mask
	 *
	 * @param ipAndMask
	 * @return String
	 * 根据cidr获取掩码位数
	 */
	public static String getMask (String ipAndMask) {
		
		String[] ipArr = ipAndMask.split("/");
		if (ipArr.length != 2) {
			throw new MessageException(ResultType.ip_format_error);
		}
		int netMask = Integer.valueOf(ipArr[1].trim());
		if (netMask < 0 || netMask > 32) {
			throw new MessageException(ResultType.ip_format_error);
		}
		return ipArr[1];
	}
	
	/**
	 * ip/mask -> sbunet
	 *
	 * @param ipAddr ipAddr
	 * @param mask   mask
	 * @return int[]
	 * 获取cidr的首末ip的int型数组
	 */
	public static int[] getIPIntScope (String ipAddr, String mask) {
		
		int ipInt;
		int netMaskInt = 0, ipcount = 0;
		try {
			ipInt = IpValidator.ipToInt(ipAddr);
			if (null == mask || "".equals(mask)) {
				return new int[] {ipInt, ipInt};
			}
			netMaskInt = IpValidator.ipToInt(mask);
			ipcount = IpValidator.ipToInt("255.255.255.255") - netMaskInt;
			int netIP = ipInt & netMaskInt;
			int hostScope = netIP + ipcount;
			return new int[] {netIP, hostScope};
		} catch (Exception e) {
			throw new MessageException(ResultType.ip_format_error);
		}
		
	}
	
	/**
	 * 根据ip和掩码获取可用首ip
	 *
	 * @param ipAddr
	 * @param mask
	 * @return
	 */
	public static String getStartIp (String ipAddr, String mask) {
		int[] ipIntArr = IpValidator.getIPIntScope(ipAddr, mask);
		return IpValidator.intToIp(ipIntArr[0] + 1);
	}
	
	/**
	 * 根据ip和掩码获取可用末ip
	 *
	 * @param ipAddr
	 * @param mask
	 * @return
	 */
	public static String getEndIp (String ipAddr, String mask, boolean type) {
		int[] ipIntArr = IpValidator.getIPIntScope(ipAddr, mask);
		if (type) {
			return IpValidator.intToIp(ipIntArr[1] - 1);
		}
		return IpValidator.intToIp(ipIntArr[1]);
	}
	
	/**
	 * string mask len -> mask
	 * for example 24-> 255.255.255.0
	 *
	 * @param masklen
	 * @return String
	 * 根据掩码1的位数获取掩码
	 */
	public static String getMaskbyLen (String masklen) {
		int netMaskInt = 0, maskint = 0;
		netMaskInt = Integer.valueOf(masklen);
		if (32 == netMaskInt) {
			return "255.255.255.255";
		}
		maskint = ~(IpValidator.ipToInt("255.255.255.255") >>> (netMaskInt));
		
		return IpValidator.intToIp(maskint);
	}
	
	/**
	 * 检查ip是否在规定范围内
	 *
	 * @param ip
	 * @param startIp
	 * @param endIp
	 * @return
	 */
	public static boolean checkIpScope (String ip, String startIp, String endIp) {
		boolean result = false;
		int ipInt = IpValidator.ipToInt(ip);
		int startIpInt = IpValidator.ipToInt(startIp);
		int endIpInt = IpValidator.ipToInt(endIp);
		if (ipInt >= startIpInt && ipInt <= endIpInt) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 检查首ip是否小于末ip
	 *
	 * @param startIp
	 * @param endIp
	 * @return
	 */
	public static boolean checkIpScope (String startIp, String endIp) {
		boolean result = false;
		int startIpInt = IpValidator.ipToInt(startIp);
		int endIpInt = IpValidator.ipToInt(endIp);
		if (startIpInt <= endIpInt) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 判断ip段是否有重叠部分
	 *
	 * @param ipList
	 * @return
	 */
	public static boolean checkIpPoolRepeat (List<Map<String, String>> ipList) {
		boolean repeat = true;//默认没有重叠
		for (int i = 0; i < ipList.size() - 1; i++) {
			Map<String, String> ipPoolI = ipList.get(i);
			int startIpIntI = ipToInt(ipPoolI.get("start"));
			int endIpIntI = ipToInt(ipPoolI.get("end"));
			for (int j = i + 1; j < ipList.size(); j++) {
				Map<String, String> ipPoolJ = ipList.get(j);
				int startIpIntJ = ipToInt(ipPoolJ.get("start"));
				int endIpIntJ = ipToInt(ipPoolJ.get("end"));
				if (endIpIntJ >= startIpIntI && startIpIntJ <= endIpIntI) {
					repeat = false;
					break;
				}
			}
		}
		return repeat;
	}
	
	/**
	 * 判断ipList里面的ip段是否在dest的ip段范围内
	 *
	 * @param ipList
	 * @return
	 */
	public static boolean checkIpPoolRepeat (List<Map<String, String>> ipList, List<Map<String, String>> dest) {
		boolean repeat = true;//默认没有重叠
		for (int i = 0; i < ipList.size(); i++) {
			if (!checkIpIsContain(ipList.get(i), dest)) {
				return false;
			}
		}
		return repeat;
	}
	
	private static boolean checkIpIsContain (Map<String, String> map, List<Map<String, String>> dest) {
		int startIpIntI = ipToInt(map.get("start"));
		int endIpIntI = ipToInt(map.get("end"));
		for (Map<String, String> map1 : dest) {
			int destStartIp = ipToInt(map1.get("start"));
			int destEndIp = ipToInt(map1.get("end"));
			if (startIpIntI >= destStartIp && endIpIntI <= destEndIp) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查ip段集合是否有重叠部分,首ip是否小于末ip,首末ip必须在cidr的首末ip内
	 *
	 * @param ipList
	 * @return
	 */
	public static ResultType checkIpPool (String cidrStartIp, String cidrEndIp, List<Map<String, String>> ipList) {
		int minStartIntIp = 0, maxEndIntIp = 0;//初始化一个最小的开始ip和最大的结束ip
		int i = 0;
		for (Map<String, String> map : ipList) {
			String startIp = map.get("start");
			String endIp = map.get("end");
			
			int startIntIp = ipToInt(startIp);
			int endIntIp = ipToInt(endIp);
			if (i == 0) {//第一次时初始化最小开始ip和最大结束ip
				minStartIntIp = startIntIp;
				maxEndIntIp = endIntIp;
			}
			if (startIntIp < minStartIntIp) {//找出最小的开始ip
				minStartIntIp = startIntIp;
			}
			if (endIntIp > maxEndIntIp) {//找出最大的开始ip
				maxEndIntIp = endIntIp;
			}
			if (!checkIpScope(startIp, endIp)) {//判断开始ip是否小于结束ip
				return ResultType.startIP_greater_than_range;
			}
			i++;
		}
		if (!checkIpPoolRepeat(ipList)) {//判断是否有重叠
			return ResultType.ipPool_contain_repeat;
		}
		if (minStartIntIp < ipToInt(cidrStartIp) || maxEndIntIp > ipToInt(cidrEndIp)) {//判断是否在cidr范围内
			return ResultType.ipPool_notIn_range;
		}
		return ResultType.success;
	}
	
	/**
	 * 检查ip是否填写正确
	 * 若全部正确将cidr、ip段数据计算结果封装返回
	 * @param gatewayIp
	 * @param ipPools
	 * @param cidr
	 * @return
	 */
	public static Map<String, Object> checkIp (String gatewayIp, String ipPools, String cidr, boolean type) {
		Map<String, Object> resultMap = new HashMap<>();
		if (!checkCidr(cidr)) {//检查cidr
			resultMap.put("error", ResultType.cidr_format_error);
			return resultMap;
		}
		String cidrStartIp = IpValidator.getStartIpByCidr(cidr);//首ip
		String cidrEndIp = IpValidator.getEndIpByCidr(cidr, type);//末ip
		if (type) {
			if (StrUtils.checkParam(gatewayIp)) {//检查网关ip
				boolean check = IpValidator.checkIp(gatewayIp);
				if (!check) {//检查网关ip格式是否正确
					resultMap.put("error", ResultType.ip_format_error);
					return resultMap;
				}
				if (!IpValidator.checkIpScope(gatewayIp, cidrStartIp, cidrEndIp)) {//检查网关ip是否在cidr规定的范围内
					resultMap.put("error", ResultType.gatewayIp_notIn_range);
					return resultMap;
				}
			} else {
				gatewayIp = cidrStartIp;
			}
		}
		List<Map<String, String>> ipPoolList = new ArrayList<>();
		if (StrUtils.checkParam(ipPools)) {//获取用户自定义的ip段
			String[] ipArray = ipPools.split(";");
			for (int i = 0; i < ipArray.length; i++) {
				Map<String, String> ipMap = new HashMap<>();
				String[] ip = ipArray[i].split("-");
				if (ip.length != 2) {//当长度不为2时则格式错误（正确格式为10.10.0.1-10.10.0.8）
					resultMap.put("error", ResultType.ipPool_format_error);
					return resultMap;
				}
				String startIp = ip[0];
				String endIp = ip[1];
				if (!checkIp(startIp) || !checkIp(endIp)) {//检查网关ip格式是否正确
					resultMap.put("error", ResultType.ipPool_format_error);
					return resultMap;
				}
				if (IpValidator.checkIpScope(gatewayIp, startIp, endIp)) {//检查网关ip是否在ippool的范围内
					resultMap.put("error", ResultType.ipPool_contain_gatewayIp);
					return resultMap;
				}
				ipMap.put("start", startIp);
				ipMap.put("end", endIp);
				ipPoolList.add(ipMap);//将用户定义的ip段封装成一个集合
			}
			ResultType rs = IpValidator.checkIpPool(cidrStartIp, cidrEndIp, ipPoolList);
			//检查用户定义的ip段是否正确（不能包含网关ip，首ip必须小于等于末ip，不能包含重叠部分,首末ip必须在cidr的首末ip内）
			if (!ResultType.success.equals(rs)) {
				resultMap.put("error", rs);
				return resultMap;
			}
		} else {//用户没有指定ip段时
			if (StrUtils.checkParam(gatewayIp) && !gatewayIp.equals(cidrStartIp)) {//用户指定网关ip不为首ip时,将网段分割成两个ip段
				int gatewayIpInt = ipToInt(gatewayIp);
				Map<String, String> ipMap = new HashMap<>();
				ipMap.put("start", cidrStartIp);
				ipMap.put("end", intToIp(gatewayIpInt - 1));
				ipPoolList.add(ipMap);//将用户定义的ip段封装成一个集合
				Map<String, String> ipMap2 = new HashMap<>();
				ipMap2.put("start", intToIp(gatewayIpInt + 1));
				ipMap2.put("end", cidrEndIp);
				ipPoolList.add(ipMap2);//将用户定义的ip段封装成一个集合
			} else {
				Map<String, String> ipMap = new HashMap<>();
				if (type) {
					ipMap.put("start", intToIp(ipToInt(cidrStartIp) + 1));
				} else {
					ipMap.put("start", cidrStartIp);
				}
				ipMap.put("end", cidrEndIp);
				ipPoolList.add(ipMap);//将用户定义的ip段封装成一个集合
			}
		}
		resultMap.put("ipPool", ipPoolList);
		resultMap.put("cidrStartIp", cidrStartIp);//放入结果集中
		resultMap.put("cidrEndIp", cidrEndIp);
		return resultMap;//验证全部通过，则返回封装好的计算结果
	}
	
	/**
	 * 根据cidr获取首ip
	 *
	 * @param cidr
	 * @return
	 */
	public static String getStartIpByCidr (String cidr) {
		String ipAddress = IpValidator.getIp(cidr);
		String mask = IpValidator.getMask(cidr);
		String maskIp = IpValidator.getMaskbyLen(mask);
		String cidrStartIp = IpValidator.getStartIp(ipAddress, maskIp);//首ip
		return cidrStartIp;
	}
	
	/**
	 * 根据cidr获取末ip
	 *
	 * @param cidr
	 * @return
	 */
	public static String getEndIpByCidr (String cidr, boolean type) {
		String ipAddress = IpValidator.getIp(cidr);
		String mask = IpValidator.getMask(cidr);
		String maskIp = IpValidator.getMaskbyLen(mask);
		String cidrEndIp = IpValidator.getEndIp(ipAddress, maskIp, type);//末ip
		return cidrEndIp;
	}
	
	/**
	 * 检验参数是否是ip或cidr
	 *
	 * @param ip
	 */
	public static boolean checkIpOrCidr (String... ip) {
		boolean result = true;
		if (null == ip) {
			return result;
		}
		for (String s : ip) {
			String[] ipArr = s.split("/");
			if (ipArr.length == 1) {
				boolean checkip = checkIp(s);
				if (!checkip) {
					result = false;
				}
			} else {
				if (!checkCidr(s)) {
					result = false;
				}
			}
		}
		return result;
	}
	
	/**
	 * 检查ip格式
	 *
	 * @param ipAddress
	 * @return
	 */
	public static boolean checkIp (String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
		
	}
	
}
