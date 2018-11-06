package test;

import java.util.Arrays;
import java.util.List;

public class StaticVal {
    static final byte QUIT = -1;
    static final char REQUEST = 1;
    static final char RESPONSE = 2;

    static final byte PRINT = 5;
    static final byte CALC = 6;//127;

    static final String IP = "localhost";//"123.2.134.41";//"172.21.24.182";//
    static final int PORT = 11001;//35100;//

//    static final String IP = "123.2.134.41";//"172.21.24.182";//
//    static final int PORT = 11001;

    static final int BUFFERSIZE = 128;
    static final int HEAD_MAX_SIZE = 14;
    static final int LENGTH_MAX_SIZE = 10;

    static final List<Character> op = Arrays.asList('*', '/', '+', '-', '(', ')');//42, 47, 43, 45, 40, 41); // *, /, +, -, (, )
}