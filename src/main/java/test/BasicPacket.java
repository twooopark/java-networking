package test;

import java.io.Serializable;

public class BasicPacket implements Serializable {
    private Header header;
    private byte[] data;

    BasicPacket(int cmd) {
        this.header = new Header(cmd);
    }

    BasicPacket(int cmd, int size, byte[] data) {
        this.header = new Header(cmd, size);
        this.data = new byte[size];
        this.data = data;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
//public class BasicPacket {
//    private Header header;
//    private Data data;
//
//    BasicPacket(int cmd, int size, byte[] data){
//        this.header = new Header(cmd, size);
//        this.data = new Data(data);
//    }
//}

class Header implements Serializable {

    private int cmd;
    private int size;
    //private int time; //UnixTime

    Header(int cmd) {
        this.cmd = cmd;
    }

    Header(int cmd, int size) {
        this.cmd = cmd;
        this.size = size;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
//
//class Data{
//    private byte[] data;
//    Data(byte[] data){
//        this.data = data;
//    }
//}