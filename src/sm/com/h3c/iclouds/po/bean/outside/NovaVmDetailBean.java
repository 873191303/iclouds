package com.h3c.iclouds.po.bean.outside;

import java.io.Serializable;
import java.util.List;

import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.po.Volume;

public class NovaVmDetailBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1048725176634245147L;

	private NovaVm novaVm;
	
	private List<Volume> volumes;
	
	private Azone azone;
	
	private String ipAddress;
	
	private String publicIp;
	
	private String privateIp;
	
	private Boolean externalNetworks;
	
	private String osPasswd;
	
	private String imageName;
	
	private String manageIp;

    private SpePort monitor;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getExternalNetworks() {
		return externalNetworks;
	}

	public void setExternalNetworks(Boolean externalNetworks) {
		this.externalNetworks = externalNetworks;
	}
	
	public String getImageName () {
		return imageName;
	}
	
	public void setImageName (String imageName) {
		this.imageName = imageName;
	}
	
	public NovaVm getNovaVm() {
		return novaVm;
	}

	public void setNovaVm(NovaVm novaVm) {
		this.novaVm = novaVm;
	}
	
	public String getOsPasswd() {
		return osPasswd;
	}

	public void setOsPasswd(String osPasswd) {
		this.osPasswd = osPasswd;
	}

	public List<Volume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}
	
	public String getPublicIp() {
		return publicIp;
	}

	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}

	public String getPrivateIp() {
		return privateIp;
	}

	public void setPrivateIp(String privateIp) {
		this.privateIp = privateIp;
	}

	public Azone getAzone() {
		return azone;
	}

	public void setAzone(Azone azone) {
		this.azone = azone;
	}
	
	public String getManageIp () {
		return manageIp;
	}
	
	public void setManageIp (String manageIp) {
		this.manageIp = manageIp;
	}

    public SpePort getMonitor() {
        return monitor;
    }

    public void setMonitor(SpePort monitor) {
        this.monitor = monitor;
    }
}
