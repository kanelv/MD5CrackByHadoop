package hadoop;

/**
 * @author Cahgnexx
 * @email cuonglvrepvn@gmail.com
 */
// Lớp triển khai hash MD5
public class MD5Hash
{
	// Khởi tạo 4 từ 32 bit A, B, C, D với những hằng số cố định
    private static final int   INIT_A     = 0x67452301;
    private static final int   INIT_B     = (int) 0xEFCDAB89L;
    private static final int   INIT_C     = (int) 0x98BADCFEL;
    private static final int   INIT_D     = 0x10325476;
    
    // Xác định số dịch chuyển mỗi vòng
    private static final int[] SHIFT_AMTS = { 7, 12, 17, 22, 5, 9, 14, 20, 4,
            11, 16, 23, 6, 10, 15, 21};
    
    // Sử dụng hàm sin để tạo hàng số ki
    private static final int[] TABLE_T    = new int[64];
    static
    {
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }
 
    public static byte[] hashMD5(byte[] message)
    {
    	// Tiền xử lý
        int messageLenBytes = message.length;
        int numBlocks = ((messageLenBytes + 8) >>> 6) + 1;
        // totalLenBytes
        int totalLen = numBlocks << 6;        
        byte[] paddingBytes = new byte[totalLen - messageLenBytes];
        // Nối 1000 0000 vào bản tin gốc
        paddingBytes[0] = (byte) 0x80;      
        long messageLenBits = (long) messageLenBytes << 3;
        // Do nối chiều dài tính theo bit của bản tin gốc là một số nguyên 64-bit little-endian vào bản tin
        for (int i = 0; i < 8; i++)
        {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits >>>= 8;
        }
        
        // Khởi tạo biến a,b,c,d 4 từ 32bit của 128bit MD5
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;
        int[] buffer = new int[16];
        
        //Xử lý mẩu tin trong đoạn 512-bit tiếp theo
        for (int i = 0; i < numBlocks; i++)
        {
            int index = i << 6;
            for (int j = 0; j < 64; j++, index++)
                buffer[j >>> 2] = ((int) ((index < messageLenBytes) ? message[index]
                        : paddingBytes[index - messageLenBytes]) << 24)
                        | (buffer[j >>> 2] >>> 8);
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;
            
            // Vòng lặp chính
            for (int j = 0; j < 64; j++)
            {
                int div16 = j >>> 4;
                int f = 0;
                int bufferIndex = j;
                switch (div16)
                {
                    case 0:
                        f = (b & c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }
                int temp = b
                        + Integer.rotateLeft(a + f + buffer[bufferIndex]
                                + TABLE_T[j],
                                SHIFT_AMTS[(div16 << 2) | (j & 3)]);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            
            // Thêm bảng băm của đoạn vào kết quả
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }
        
        // Ghép nối kết quả cuối cùng
        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++)
            {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
    }
 
    public static String toHexString(byte[] b)
    {
//    	tạo ra một string Builder rỗng với chiều dài khởi tạo là 16
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < b.length; i++)
        {
        	// Chuyển từng byte trong b ra thành từng cặp mã Hex tương ứng
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }
 /*
    public static void main(String[] args)
    {
    	long startTime = System.currentTimeMillis();
    	    	
    	System.out.println(toHexString(hashMD5("123456".getBytes())));
    	
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);               
    }
    */
}