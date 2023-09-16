package hadoop;

import java.io.IOException;

/**
 * @author Cahgnexx
 *
 */
//Lớp thám mã MD5
public class PasswordCrackerUtil {
	private static final String PASSWORD_CHARS 	= "0123456789";    // cac ky hieu password NUMBER(0~9)
    private static final int 	PASSWORD_LEN 	= 6;
    public 	static final long 	TOTAL_PASSWORD_RANGE_SIZE = (long) Math.pow(PASSWORD_CHARS.length(), PASSWORD_LEN);
    
    public static String encrypt(String password){
    	return MD5Hash.toHexString(MD5Hash.hashMD5(password.getBytes()));
    }
    
    public static String findPasswordInRange(long rangeBegin, long rangeEnd, String encryptedPassword, TerminationChecker checker)
            throws IOException {

            char    passwdFirstChar = encryptedPassword.charAt(0);
            int[]   arrayKey        = new int[PASSWORD_LEN];
            String  passwd = null;

            // Tính toán trên chuỗi đầu tiên
            long longKey = rangeBegin;
            transformDecToBase36(longKey, arrayKey);
     
            for (; longKey < rangeEnd && !checker.isTerminated(); longKey++) {
                String rawKey = transformIntoStr(arrayKey);
                String md5Key = encrypt(rawKey);

                // Tránh việc so sánh đầy đủ bằng so sánh ký tự đầu tiên rồi mới so sánh đầy đủ
                if (md5Key.charAt(0) == passwdFirstChar) {
                    if (encryptedPassword.equals(md5Key)) {
                        passwd = rawKey;
                        break;
                    }
                }
                getNextCandidate(arrayKey);
            }

            return passwd;
        }
    // Chuyen 1 so ban dau thanh 1 mang cac phan tu co so 36
    private static void transformDecToBase36(long numInDec, int[] numArrayInBase36) {
        long    quotient        = numInDec; 
        int     passwdlength    = numArrayInBase36.length - 1;

        for (int i = passwdlength; quotient > 0l; i--) {
            int reminder = (int) (quotient % 10l);
            quotient /= 10l;
            numArrayInBase36[i] = reminder;
        }
    }
    
    private static void getNextCandidate(int[] candidateChars) {
        int i = candidateChars.length - 1;

        while(i >= 0) {
            candidateChars[i] += 1;

            if (candidateChars[i] >= 10) {
                candidateChars[i] = 0;
                i--;

            } else {
                break;
            }
        }
    }
// Chuyen mang cac phan tu co so 36 thanh cac ky tu tuong ung trong chuoi password kha thi
    private static String transformIntoStr(int[] candidateChars) {
        char[] password = new char[candidateChars.length];
        for (int i = 0; i < password.length; i++) {
            password[i] = PASSWORD_CHARS.charAt(candidateChars[i]);
        }
        return new String(password);
    }
}

