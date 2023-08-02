package test.com.ido.runplan.utils;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouzj on 2018/7/25.
 */

public class StringUtil {

    public static String format(String format, Object... args){
        try {
            return String.format(Locale.CHINA,format, args);
        }catch (Exception e){
            return "";
        }

    }
    /**
     * 是否为空
     * @param str 字符串
     * @return true 空 false 非空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }/**
     * 得到字符串中某个字符的个数
     *
     * @param str
     *            字符串
     * @param c
     *            字符
     * @return
     */
    public static final int getCharnum(String str, char c) {
        int num = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (c == chars[i]) {
                num++;
            }
        }
        return num;
    }



    /**
     * 按长度分割字符串
     *
     * @param content
     * @param len
     * @return
     */
    public static String[] split(String content, int len) {
        if (content == null || content.equals("")) {
            return null;
        }
        int len2 = content.length();
        if (len2 <= len) {
            return new String[] { content };
        } else {
            int i = len2 / len + 1;
            System.out.println("i:" + i);
            String[] strA = new String[i];
            int j = 0;
            int begin = 0;
            int end = 0;
            while (j < i) {
                begin = j * len;
                end = (j + 1) * len;
                if (end > len2)
                    end = len2;
                strA[j] = content.substring(begin, end);
                // System.out.println(strA[j]+"<br/>");
                j = j + 1;
            }
            return strA;
        }
    }

    public static boolean emailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    /**
     * 判断某字符串是否为null，如果长度大于256，则返回256长度的子字符串，反之返回原串
     *
     * @param str
     * @return
     */
    public static String checkStr(String str) {
        if (str == null) {
            return "";
        } else if (str.length() > 256) {
            return str.substring(256);
        } else {
            return str;
        }
    }

    /**
     * 验证是不是Int validate a int string
     *
     * @param str
     *            the Integer string.
     * @return true if the str is invalid otherwise false.
     */
    public static boolean validateInt(String str) {
        if (str == null || str.trim().equals(""))
            return false;

        char c;
        for (int i = 0, l = str.length(); i < l; i++) {
            c = str.charAt(i);
            if (!((c >= '0') && (c <= '9')))
                return false;
        }

        return true;
    }

    /**
     * 字节码转换成16进制字符串 内部调试使用
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }

    /**
     * 字节码转换成自定义字符串 内部调试使用
     *
     * @param b
     * @return
     */
    public static String byte2string(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            // if (n<b.length-1) hs=hs+":";
        }
        return hs.toUpperCase();
    }

    public static byte[] string2byte(String hs) {
        byte[] b = new byte[hs.length() / 2];
        for (int i = 0; i < hs.length(); i = i + 2) {
            String sub = hs.substring(i, i + 2);
            byte bb = new Integer(Integer.parseInt(sub, 16)).byteValue();
            b[i / 2] = bb;
        }
        return b;
    }


    // 验证英文字母或数据
    public static boolean isLetterOrDigit(String str) {
        Pattern p = null; // 正则表达式
        Matcher m = null; // 操作的字符串
        boolean value = true;
        try {
            p = Pattern.compile("[^0-9A-Za-z]");
            m = p.matcher(str);
            if (m.find()) {

                value = false;
            }
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * 验证是否是小写字符串
     */
    @SuppressWarnings("unused")
    private static boolean isLowerLetter(String str) {
        Pattern p = null; // 正则表达式
        Matcher m = null; // 操作的字符串
        boolean value = true;
        try {
            p = Pattern.compile("[^a-z]");
            m = p.matcher(str);
            if (m.find()) {
                value = false;
            }
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * 截取数字
     *
     * @param content
     * @return
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 截取非数字
     *
     * @param content
     * @return
     */
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String encode(String str, String code) {
        try {
            return URLEncoder.encode(str, code);
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return "";
        }
    }

    public static String decode(String str, String code) {
        try {
            return URLDecoder.decode(str, code);
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return "";
        }
    }

    public static String nvl(String param) {
        return param != null ? param.trim() : "";
    }

    public static int parseInt(String param, int d) {
        int i = d;
        try {
            i = Integer.parseInt(param);
        } catch (Exception e) {
        }
        return i;
    }

    public static int parseInt(String param) {
        return parseInt(param, 0);
    }

    public static long parseLong(String param) {
        long l = 0L;
        try {
            l = Long.parseLong(param);
        } catch (Exception e) {
        }
        return l;
    }

    public static double parseDouble(String param) {
        return parseDouble(param, 1.0);
    }

    public static double parseDouble(String param, double t) {
        double tmp = 0.0;
        try {
            tmp = Double.parseDouble(param.trim());
        } catch (Exception e) {
            tmp = t;
        }
        return tmp;
    }

    public static boolean parseBoolean(String param) {
        if (isEmpty(param))
            return false;
        switch (param.charAt(0)) {
            case 49: // '1'
            case 84: // 'T'
            case 89: // 'Y'
            case 116: // 't'
            case 121: // 'y'
                return true;

        }
        return false;
    }

    /**
     * public static String replace(String mainString, String oldString, String
     * newString) { if(mainString == null) return null; int i =
     * mainString.lastIndexOf(oldString); if(i < 0) return mainString;
     * StringBuffer mainSb = new StringBuffer(mainString); for(; i >= 0; i =
     * mainString.lastIndexOf(oldString, i - 1)) mainSb.replace(i, i +
     * oldString.length(), newString);
     *
     * return mainSb.toString(); }
     *
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final String[] split(String str, String delims) {
        StringTokenizer st = new StringTokenizer(str, delims);
        ArrayList list = new ArrayList();
        for (; st.hasMoreTokens(); list.add(st.nextToken()))
            ;
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String substring(String str, int length) {
        if (str == null)
            return null;

        if (str.length() > length)
            return (str.substring(0, length - 2) + "...");
        else
            return str;
    }

    public static boolean validateDouble(String str) throws RuntimeException {
        if (str == null)
            // throw new RuntimeException("null input");
            return false;
        char c;
        int k = 0;
        for (int i = 0, l = str.length(); i < l; i++) {
            c = str.charAt(i);
            if (!((c >= '0') && (c <= '9')))
                if (!(i == 0 && (c == '-' || c == '+')))
                    if (!(c == '.' && i < l - 1 && k < 1))
                        // throw new RuntimeException("invalid number");
                        return false;
                    else
                        k++;
        }
        return true;
    }

    public static String gbToIso(String s) throws UnsupportedEncodingException {
        return covertCode(s, "GB2312", "ISO8859-1");
    }

    public static String covertCode(String s, String code1, String code2)
            throws UnsupportedEncodingException {
        if (s == null)
            return null;
        else if (s.trim().equals(""))
            return "";
        else
            return new String(s.getBytes(code1), code2);
    }

    public static String transCode(String s0)
            throws UnsupportedEncodingException {
        if (s0 == null || s0.trim().equals(""))
            return null;
        else {
            s0 = s0.trim();
            return new String(s0.getBytes("GBK"), "ISO8859-1");
        }
    }

    /**
     * 编码转换
     *
     * @param s
     * @return
     */
    public static String GBToUTF8(String s) {
        try {
            StringBuffer out = new StringBuffer();
            byte[] bytes = s.getBytes("unicode");
            for (int i = 2; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                out.append(str);
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                for (int j = str1.length(); j < 2; j++) {
                    out.append("0");
                }

                out.append(str1);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static final String[] replaceAll(String[] obj, String oldString,
                                            String newString) {
        if (obj == null) {
            return null;
        }
        int length = obj.length;
        String[] returnStr = new String[length];
        String str = null;
        for (int i = 0; i < length; i++) {
            returnStr[i] = replaceAll(obj[i], oldString, newString);
        }
        return returnStr;
    }

    /**
     * 字符串全文替换
     *
     * @param s0
     * @param oldstr
     * @param newstr
     * @return
     */
    public static String replaceAll(String s0, String oldstr, String newstr) {
        if (s0 == null || s0.trim().equals(""))
            return null;
        StringBuffer sb = new StringBuffer(s0);
        int begin = 0;
        // int from = 0;
        begin = s0.indexOf(oldstr);
        while (begin > -1) {
            sb = sb.replace(begin, begin + oldstr.length(), newstr);
            s0 = sb.toString();
            begin = s0.indexOf(oldstr, begin + newstr.length());
        }
        return s0;
    }

    public static String toHtml(String str) {
        String html = str;
        if (str == null || str.length() == 0) {
            return str;
        }
        html = replaceAll(html, "&", "&amp;");
        html = replaceAll(html, "<", "&lt;");
        html = replaceAll(html, ">", "&gt;");
        html = replaceAll(html, "\r\n", "\n");
        html = replaceAll(html, "\n", "<br>\n");
        html = replaceAll(html, "\t", "         ");
        html = replaceAll(html, " ", "&nbsp;");
        return html;
    }

    /**
     * 字符串替换
     *
     * @param line
     * @param oldString
     * @param newString
     * @return
     */
    public static final String replace(String line, String oldString,
                                       String newString) {
        if (line == null) {
            return null;
        }
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = line.indexOf(oldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    public static final String replaceIgnoreCase(String line, String oldString,
                                                 String newString) {
        if (line == null) {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * 标签转义
     *
     * @param input
     * @return
     */
    public static final String escapeHTMLTags(String input) {
        // Check if the string is null or zero length -- if so, return
        // what was sent in.
        if (input == null || input.length() == 0) {
            return input;
        }
        // Use a StringBuffer in lieu of String concatenation -- it is
        // much more efficient this way.
        StringBuffer buf = new StringBuffer(input.length());
        char ch = ' ';
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '>') {
                buf.append("&gt;");
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Returns a random String of numbers and letters of the specified length.
     * The method uses the Random class that is built-in to Java which is
     * suitable for low to medium grade security uses. This means that the
     * output is only pseudo random, i.e., each number is mathematically
     * generated so is not truly random.
     * <p>
     *
     * For every character in the returned String, there is an equal chance that
     * it will be a letter or number. If a letter, there is an equal chance that
     * it will be lower or upper case.
     * <p>
     *
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length
     *            the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */

    private static Random randGen = null;
    private static Object initLock = new Object();
    private static char[] numbersAndLetters = null;

    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Init of pseudo random number generator.
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    // Also initialize the numbersAndLetters array
                    numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                            + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                            .toCharArray();
                }
            }
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    /**
     * 固定长度字符串截取
     *
     * @param content
     * @param i
     * @return
     */
    public static String getSubstring(String content, int i) {
        int varsize = 10;
        String strContent = content;
        if (strContent.length() < varsize + 1) {
            return strContent;
        } else {
            int max = (int) Math.ceil((double) strContent.length() / varsize);
            if (i < max - 1) {
                return strContent.substring(i * varsize, (i + 1) * varsize);
            } else {
                return strContent.substring(i * varsize);
            }
        }
    }

    public static String formatHTMLOutput(String s) {
        if (s == null)
            return null;

        if (s.trim().equals(""))
            return "";

        String formatStr;
        formatStr = replaceAll(s, " ", "&nbsp;");
        formatStr = replaceAll(formatStr, "\n", "<br />");

        return formatStr;
    }

    /*
     * 4舍5入 @param num 要调整的数 @param x 要保留的小数位
     */
    public static double myround(double num, int x) {
        BigDecimal b = new BigDecimal(num);
        return b.setScale(x, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * liuqs
     *
     * @param param
     * @param d
     * @return
     */
    public static int parseLongInt(Long param, int d) {
        int i = d;
        try {
            i = Integer.parseInt(String.valueOf(param));
        } catch (Exception e) {
        }
        return i;
    }

    public static int parseLongInt(Long param) {
        return parseLongInt(param, 0);
    }

    public static String returnString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * 修改敏感字符编码
     *
     * @param value
     * @return
     */
    public static String htmlEncode(String value) {
        String[][] re = { { "<", "&lt;" }, { ">", "&gt;" }, { "\"", "&quot;" },
                { "\\′", "&acute;" }, { "&", "&amp;" } };

        for (int i = 0; i < 4; i++) {
            value = value.replaceAll(re[i][0], re[i][1]);
        }
        return value;
    }

    /**
     * 防SQL注入
     *
     * @param str
     * @return
     */
    public static boolean sql_inj(String str) {
        String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
        String[] inj_stra = inj_str.split("|");
        for (int i = 0; i < inj_stra.length; i++) {
            if (str.indexOf(inj_stra[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将给定的字符串,去掉末尾的"."号及之后的部分返回
     * @param string 源字符串
     * @return 返回去掉最后一个"."号及之后的部分返回
     */
    public static String getSubStrByCutLastDotPartOff(String string) {
        if (string == null) {
            return "";
        }
        int indexOf = string.lastIndexOf(".");
        if (indexOf == -1) {
            return string.trim();
        } else {
            return string.substring(0, indexOf).trim();
        }
    }

    /**
     * 使用最小值和最大值,生成他们之间相连的字符串数组.数组范围为[min,max]
     * @param min 数组的最小值
     * @param max 数组的最大值
     * @return 返回[min,max]之间的连续字符串数组
     */
    public static String[] generateStringArray(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("参数有误,应当先传入最小值,然后传入最大值");
        }
        String format = "%0" + String.valueOf(max).length() + "d";
        String[] array = new String[max - min + 1];
        for (int i = min; i <= max; i++) {
            array[i - min] = String.format(Locale.getDefault(), format, i + min);
        }
        return array;
    }

    /**
     * 将一个Object转换为Integer
     * @param o 被转换的Object
     * @return 转换后的Integer
     */
    public static int objectToInt(Object o) {
        int result;
        try {
            result = Integer.parseInt((String) o);
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }

    /**
     * 设置无空白的文字
     * @param str
     * @return
     */
    public static String getStringNoEmpty(String str){
        if(!TextUtils.isEmpty(str)) {
            String strNoBlank = str.replaceAll(" " , "") ;
            return strNoBlank;
        }else {
            return str;
        }
    }

    /**
     *
     * 将换行符替换为""
     * @param str
     * @return
     */
    public static String getStringNoBlank(String str) {
        if(str!=null && !"".equals(str)) {
            Pattern p = Pattern.compile("\\t|\\r|\\n");
            Matcher m = p.matcher(str);
            String strNoBlank = m.replaceAll("");
            return strNoBlank;
        }else {
            return str;
        }
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {

        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 富文本显示不同颜色Text
     * @param source
     * @param target
     * @param color
     * @return
     */
    public static CharSequence richText(String source,String target,int color){
        if(TextUtils.isEmpty(source) || TextUtils.isEmpty(target)){
            return source;
        }
        if(source.contains(target)){
            int i = source.indexOf(target);
            SpannableStringBuilder builder = new SpannableStringBuilder(source);
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            builder.setSpan(span,i,i+target.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        } else {
            return source;
        }
    }

    public static String formatDecimal(float in){
        BigDecimal bigDecimal = new BigDecimal(in);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * 判断字符串中某个字符存在的个数
     * @param str1  完整字符串
     * @param str2  要统计匹配个数的字符
     * @return
     */
    public static int countStr(String str1, String str2) {
        int count=0;
        if (str1 == null || str1.indexOf(str2) == -1) {
            return 0;
        }
        while(str1.indexOf(str2)!=-1){
            count++;
            str1=str1.substring(str1.indexOf(str2)+str2.length());
        }
        return count;
    }

    public static int[] countStr2(String str1, String str2) {
        int count = countStr(str1,str2);
        if(count == 2){
            int index1 = str1.indexOf(str2);
            String newStr = str1.substring(index1+str2.length());
            int index2 = newStr.indexOf(str2) + index1+str2.length();
            return new int[]{index1,index2};
        }
        return null;
    }
}
