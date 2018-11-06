package test;

import java.util.Arrays;
import java.util.List;

public class StaticVal {
    static final byte QUIT = -1;

    static final byte[] REQUEST = {0x30, 0x31};
    static final byte[] RESPONSE = {0x30, 0x32};

    //정밀도 0~6
    static final byte[][] PREC = {{0x30, 0x30}, {0x30, 0x31}, {0x30, 0x32},
            {0x30, 0x33}, {0x30, 0x34}, {0x30, 0x35}, {0x30, 0x36}};


    static final String REQ = "01";
    static final String RES = "02";

    //정밀도 0~6
    static final String[] PRECS = {"00", "01", "02", "03", "04", "05", "06"};

    static final String IP = "172.21.25.143";//"123.2.134.41";//"172.21.24.182";//
    static final int PORT = 11001;//35100;//

//    static final String IP = "123.2.134.41";//"172.21.24.182";//
//    static final int PORT = 11001;

    static final int BUFFERSIZE = 1024;
    static final int HEAD_MAX_SIZE = 14;
    static final int LENGTH_MAX_SIZE = 10;

    static final List<Character> op = Arrays.asList('*', '/', '+', '-', '(', ')');//42, 47, 43, 45, 40, 41); // *, /, +, -, (, )
}