package test;

import java.io.Serializable;

public class CalcPacket {
    private CalcHeader header;
    private StringBuffer data;

    public CalcPacket(CalcHeader header) {
        this.header = header;
        data = new StringBuffer();
        System.out.println("Constructor: "+this.toString());
    }

    public CalcHeader getHeader() {
        return header;
    }

    public void setHeader(CalcHeader header) {
        this.header = header;
    }

    public StringBuffer getData() {
        return data;
    }

    public void setData(StringBuffer data) {
        this.data = data;
    }

    public void append(Object data) {
        this.data.append(data);
    }

    @Override
    public String toString() {
        return "type:"+this.header.getType()+", prec:"+this.header.getPrecision()+", leng:"+this.header.getLength();
    }
}

class CalcHeader{
    private int type;
    private int precision;
    private long length;

    public CalcHeader(int type, int precision, long length) {
        this.type = type;
        this.precision = precision;
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}