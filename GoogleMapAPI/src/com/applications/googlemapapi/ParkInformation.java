package com.applications.googlemapapi;

public class ParkInformation {
	private Double longitude;
	private Double latitude;
	private Long parkingtime;
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Long getParkingtime() {
		return parkingtime;
	}
	public void setParkingtime(Long parkingtime) {
		this.parkingtime = parkingtime;
	}
	
	public String toString(){
		return String.format("%s , %s , %d", this.getLongitude(), this.getLatitude(),this.getParkingtime());
	}
}
