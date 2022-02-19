package test.com.ido.font.upgrade;


import java.io.Serializable;

/**
 * {
 *    "dle_length" : 0,
 *    "phy_speed" : 0,
 *    "rx_mtu" : 0,
 *    "status" : 0,
 *    "tx_mtu" : 0
 * }
 */
public class MtuInfo implements Serializable {
    public int dle_length;
    public int phy_speed;
    public int rx_mtu;
    public int status;
    public int tx_mtu;

}
