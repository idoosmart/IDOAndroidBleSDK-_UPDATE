package test.com.ido.runplan;

public class ConnectBluetoothDeviceH5Bean {


  /**
   * BluetoothDevice : {"deviceName":"ID206","deviceVersion":"111","mac":"EB:63:F9:22:AE:5E"}
   */

  private BluetoothDeviceBean BluetoothDevice;

  public BluetoothDeviceBean getBluetoothDevice() {
    return BluetoothDevice;
  }

  public void setBluetoothDevice(BluetoothDeviceBean BluetoothDevice) {
    this.BluetoothDevice = BluetoothDevice;
  }

  public static class BluetoothDeviceBean {
    /**
     * deviceName : ID206
     * deviceVersion : 111
     * mac : EB:63:F9:22:AE:5E
     */

    private String deviceName;
    private String deviceVersion;
    private String mac;

    public String getDeviceName() {
      return deviceName;
    }

    public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
    }

    public String getDeviceVersion() {
      return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
      this.deviceVersion = deviceVersion;
    }

    public String getMac() {
      return mac;
    }

    public void setMac(String mac) {
      this.mac = mac;
    }
  }
}
