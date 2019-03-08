package com.redhat.kafka.demo.streams;

public class SpeedInfo {

    private float speed;
    private CarInfo carInfo;

    public SpeedInfo(float speed, CarInfo carInfo) {
        this.speed = speed;
        this.carInfo = carInfo;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }
}
