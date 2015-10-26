package com.gk.touchstone.entity;

public class WifiAP {
	private String ssid;
	private String psk;
	private String keymgmt;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPsk() {
		return psk;
	}

	public void setPsk(String psk) {
		this.psk = psk;
	}

	public String getKeymgmt() {
		return keymgmt;
	}

	public void setKeymgmt(String keymgmt) {
		this.keymgmt = keymgmt;
	}

}