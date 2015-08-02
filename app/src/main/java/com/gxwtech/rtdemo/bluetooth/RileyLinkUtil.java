package com.gxwtech.rtdemo.bluetooth;

/**
 * Created by Geoff on 2-8-15.
 */
public class RileyLinkUtil {
    private static final String TAG = "RileyLinkUtil";

    /*
     CodeSymbols is an ordered list of translations
     6bits -> 4 bits, in order from 0x0 to 0xF
     The 6 bit codes are what is used on the RF side of the RileyLink
     to communicate with a Medtronic pump.
     The RileyLink translates the 6 bit codes into bytes when receiving,
     but we have to construct the 6 bit codes when sending.
     */
    public static final byte[] CodeSymbols = {
            0x15,
            0x31,
            0x32,
            0x23,
            0x34,
            0x25,
            0x26,
            0x16,
            0x1a,
            0x19,
            0x2a,
            0x0b,
            0x2c,
            0x0d,
            0x0e,
            0x1c
    };

    public static int computeNewSize(int inputSize){
        return (int)(Math.ceil((inputSize * 3.0) / 2.0));
    }

    public static byte[] composeRFStream(byte[] input) {
        /*
         0xa7 -> (0xa -> 0x2a == 101010) + (0x7 -> 0x16 == 010110) == 1010 1001 0110 = 0xa96
         0x12 -> (0x1 -> 0x31 == 110001) + (0x2 -> 0x32 == 110010) == 1100 0111 0010 = 0xc72
         so:
         {0xa7} -> {0xa9, 0x60}
         {0xa7, 0x12} -> {0xa9, 0x6c, 0x72}
         {0xa7, 0x12, 0xa7} -> {0xa9, 0x6c, 0x72, 0xa9, 0x60}
         */

        if (input == null || input.length == 0) {
            return null;
        }

        int outSize = computeNewSize(input.length);
        final byte[] rval = new byte[outSize];
        for (int i=0; i< input.length; i++) {
            int rfBytes = composeRFBytes(input[i]);
            int outIndex = ((i/2) * 3) + (i%2);
            // outIndex: 0->0, 1->1, 2->3, 3->4, 4->6, 5->7, 6->9, 7->10
            if ((i % 2)==0) {
                rval[outIndex] = (byte)(rfBytes >> 8);
                rval[outIndex+1] = (byte)(rfBytes & 0xF0);
            } else {
                rval[outIndex] = (byte)((rval[outIndex] & 0xF0) | ((rfBytes >> 12) & 0x0F));
                rval[outIndex+1] = (byte)(rfBytes >> 4);
            }
        }
        return rval;
    }


    // return a 12 bit binary number representing outgoing RF code for byte
    // 0xa7 -> (0xa -> 0x2a == 101010) + (0x7 -> 0x16 == 010110) == 1010 1001 0110 = 0xa96
    // zero extend low bits to achieve 2 bytes: 0xa960
    public static int composeRFBytes(byte b) {
        // bit translations are per nibble
        int lowNibble = b & 0x0F;
        byte lowCode = CodeSymbols[lowNibble];
        int highNibble = (b & 0xF0) >> 4;
        byte highCode = CodeSymbols[highNibble];

        byte highByte = (byte)(((highCode << 2) & 0xFC) | ((lowCode & 0x30) >> 4));
        byte lowByte = (byte)((lowCode & 0x0f) << 4);
        int rval = highByte;
        rval = rval << 8;
        rval = rval | (lowByte & 0xFF);
        return rval;
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length);
    }

    private final static char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static String toHexString(byte[] array, int offset, int length) {
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
            buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
        }

        return new String(buf);
    }

}
