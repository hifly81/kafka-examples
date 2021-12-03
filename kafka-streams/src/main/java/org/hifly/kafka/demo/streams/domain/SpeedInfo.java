package org.hifly.kafka.demo.streams.domain;

public class SpeedInfo {

    private float speed;
    private CarInfo carInfo;

    public SpeedInfo() {}

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((carInfo == null) ? 0 : carInfo.hashCode());
        result = prime * result + Float.floatToIntBits(speed);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpeedInfo other = (SpeedInfo) obj;
        if (carInfo == null) {
            if (other.carInfo != null)
                return false;
        } else if (!carInfo.equals(other.carInfo))
            return false;
        if (Float.floatToIntBits(speed) != Float.floatToIntBits(other.speed))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SpeedInfo [carInfo=" + carInfo + ", speed=" + speed + "]";
    }
}
