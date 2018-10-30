package com.slisenko.server.socket;
/*
  패킷의 구성
     - 헤더(머리) + 페이로드(내용/데이터) + 트레일러(꼬리)
        . 패킷 선두(헤더)에는, 패킷의 주소(송수신 주소) 등 주요 제어 정보들이 포함되는
                               것이 일반적임                  ☞ IP 헤더, MAC 헤더 등 참조
        . 패킷 후미(트레일러)에는, 패킷 에러 검출 등에 사용   ☞ FCS 등 참조
           .. 패킷 꼬리는 없는 경우도 많음
 */

public class BasicProtocol {
    private Header header = new Header();
    private Data data = new Data();
    //private final int crc = 0xfffe;
}

class Header{
    private byte[] command;
    private char[] inetAddr;
    private char[] macAddr;
    private int time; //UnixTime
    private int size;
}
class Data{
    private byte[] data;
}