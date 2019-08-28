package com.h3c.iclouds.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.h3c.iclouds.auth.CacheSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Ssh2Utils {
	
	private boolean isAuthenticated = false;
	
	private Session session = null;
	
	private Connection conn = null;
	
	public Ssh2Utils(String hostIp, String userName, String password) {
		conn = new Connection(hostIp);
		try {
			conn.connect();
			this.isAuthenticated = conn.authenticateWithPassword(userName, password);
			if (isAuthenticated) {
				session = conn.openSession();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public boolean execScp(String source, String targetDir) {
        try {
            SCPClient scpClient = conn.createSCPClient();
            scpClient.put(source, targetDir);
            return true;
        } catch (IOException e) {
            LogUtils.exception(this.getClass(), e);
            e.printStackTrace();
        }
        return false;
    }

	public List<String> execCmd(String cmd) {
		LogUtils.info(Ssh2Utils.class, "Exec cmd ==========>" + cmd);
		List<String> list = null;
		if(!this.isAuthenticated) {
			return null;
		}
		InputStream stdout = null;
		BufferedReader stdoutReader = null;
		try {
			session = conn.openSession();
			session.execCommand(cmd);
			stdout = new StreamGobbler(session.getStdout());
			stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			while (true) {
				if(list == null) {
					list = new ArrayList<String>();
				}
				String line = stdoutReader.readLine();
				if (line == null)
					break;
				list.add(line);
				LogUtils.info(Ssh2Utils.class, "Exec cmd line ==========>>> " + line);
			}
		} catch (Exception e) {
			list = null;
			e.printStackTrace();
		} finally {
			if(stdoutReader != null) {
				try {
					stdoutReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(stdout != null) {
				try {
					stdout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(session != null) {
				try {
					session.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static Ssh2Utils create(String hostIp, String userName, String password) {
		return new Ssh2Utils(hostIp, userName, password);
	}
	
	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}
	
	public void close() {
		if(session != null) {
			try {
				this.session.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				this.conn.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Ssh2Utils createCasHostSSH(String hostIp) {
		if (null == hostIp) {
			hostIp = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.ip");
		}
		String password = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.password");
		String userName = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.username");
		Ssh2Utils ssh = Ssh2Utils.create(hostIp, userName, password);
		if(!ssh.isAuthenticated()) {
			return null;
		}
		return ssh;
	}

}
