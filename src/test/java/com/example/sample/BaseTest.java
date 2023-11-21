package com.example.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Slf4j
public class BaseTest {

    @Test
    public void timeComplexity() {
        int n = 100000;
        long count = 0;
        long start = System.currentTimeMillis();
        // O(n^2)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                count++;
            }
        }
        log.info(">>>>> COUNT: [{}], COST: [{}]MS", count, System.currentTimeMillis() - start);

        count = 0;
        start = System.currentTimeMillis();
        // O(n)
        for (int i = 0; i < n; i++) {
            count++;
        }
        log.info(">>>>> COUNT: [{}], COST: [{}]MS", count, System.currentTimeMillis() - start);

        count = 0;
        start = System.currentTimeMillis();
        // O(logn)
        for (int i = 1; i < n; i *= 2) {
            count++;
        }
        log.info(">>>>> COUNT: [{}], COST: [{}]MS", count, System.currentTimeMillis() - start);
    }

    // public static void main(String[] args) {
    //     Scanner sc = new Scanner(System.in);
    //     String line = sc.nextLine();
    //     sc.close();
    //
    //     if (line.indexOf("joker") >= 0 || line.indexOf("JOKER") >= 0) {
    //         System.out.println("ERROR");
    //         return;
    //     }
    //
    //     int[] array = new int[4];
    //     for (int i = 0; i < 4; ++i) {
    //         String num = String.valueOf(line.charAt(i * 2));
    //         if ("J".equals(num)) {
    //             array[i] = 11;
    //         } else if ("Q".equals(num)) {
    //             array[i] = 12;
    //         } else if ("K".equals(num)) {
    //             array[i] = 13;
    //         } else if ("A".equals(num)) {
    //             array[i] = 1;
    //         } else {
    //             array[i] = Integer.parseInt(num);
    //         }
    //     }
    //     List<Integer> included = new ArrayList<>();
    //     String[] result = new String[7];
    //     findResult(result, 24, array, included);
    //     if (result[0].equals("NONE")) {
    //         System.out.println("NONE");
    //         return;
    //     }
    //
    //     for (int i = 0; i < result.length; ++i) {
    //         System.out.print(result[i]);
    //         if (i == result.length - 1) {
    //             System.out.println();
    //         }
    //     }
    // }
    //
    // private static void findResult(String[] result, int target, int[] array,
    //                                List<Integer> included) {
    //     int resultLen = result.length;
    //     for (int i = 0; i < array.length; i++) {
    //         if (included.contains((Integer) i)) {
    //             continue;
    //         }
    //         included.add((Integer) i);
    //
    //         result[resultLen - 1 - i] = String.valueOf(array[i]);
    //         result[resultLen - 2 - i] = "+";
    //         findResult(result, target - array[i], array, included);
    //         if (!"NONE".equals(result[0])) {
    //             return;
    //         }
    //
    //         result[resultLen - 2 - i] = "-";
    //         findResult(result, target + array[i], array, included);
    //         if (!"NONE".equals(result[0])) {
    //             return;
    //         }
    //
    //         result[resultLen - 2 - i] = "/";
    //         findResult(result, target * array[i], array, included);
    //         if (!"NONE".equals(result[0])) {
    //             return;
    //         }
    //
    //         if (target % array[i] != 0) {
    //             continue;
    //         }
    //         result[resultLen - 2 - i] = "*";
    //         findResult(result, target / array[i], array, included);
    //         if (!"NONE".equals(result[0])) {
    //             return;
    //         }
    //         included.remove(Integer.valueOf(i));
    //     }
    //     result[0] = "NONE";
    // }

    private static boolean solveSudokuHelper(char[][] board) {
        //「一个for循环遍历棋盘的行，一个for循环遍历棋盘的列，
        // 一行一列确定下来之后，递归遍历这个位置放9个数字的可能性！」
        for (int i = 0; i < 9; i++) { // 遍历行
            for (int j = 0; j < 9; j++) { // 遍历列
                if (board[i][j] != '0') { // 跳过原始数字
                    continue;
                }
                for (char k = '1'; k <= '9'; k++) { // (i, j) 这个位置放k是否合适
                    if (isValidSudoku(i, j, k, board)) {
                        board[i][j] = k;
                        if (solveSudokuHelper(board)) { // 如果找到合适一组立刻返回
                            return true;
                        }
                        board[i][j] = '0';
                    }
                }
                // 9个数都试完了，都不行，那么就返回false
                return false;
                // 因为如果一行一列确定下来了，这里尝试了9个数都不行，说明这个棋盘找不到解决数独问题的解！
                // 那么会直接返回， 「这也就是为什么没有终止条件也不会永远填不满棋盘而无限递归下去！」
            }
        }
        // 遍历完没有返回false，说明找到了合适棋盘位置了
        return true;
    }

    /**
     * 判断棋盘是否合法有如下三个维度:
     * 同行是否重复
     * 同列是否重复
     * 9宫格里是否重复
     */
    private static boolean isValidSudoku(int row, int col, char val, char[][] board) {
        // 同行是否重复
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == val) {
                return false;
            }
        }
        // 同列是否重复
        for (int j = 0; j < 9; j++) {
            if (board[j][col] == val) {
                return false;
            }
        }
        // 9宫格里是否重复
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == val) {
                    return false;
                }
            }
        }
        return true;
    }

    // public static void main(String[] args) {
    //     Scanner sc = new Scanner(System.in);
    //     List<String> list = new ArrayList<>();
    //     while (!sc.hasNext("#")) {
    //         list.add(sc.nextLine());
    //     }
    //
    //     for (String s : list) {
    //         String[] array = s.split("\\/");
    //         int a = Integer.parseInt(array[0]);
    //         int b = Integer.parseInt(array[1]);
    //         StringBuffer buffer = new StringBuffer();
    //         findEgypt(a, b, buffer);
    //         System.out.println(buffer.substring(0, buffer.length() - 1));
    //     }
    // }

    private static void findEgypt(int a, int b, StringBuffer buffer) {
        if (b % a == 0) {
            buffer.append("1/").append(b / a).append("+");
            return;
        }

        int c = b / a;
        buffer.append("1/").append(c + 1).append("+");
        findEgypt(a * (c + 1) - b, b * (c + 1), buffer);
    }

    // public static void main(String[] args) {
    //     Scanner sc = new Scanner(System.in);
    //     double d = sc.nextDouble();
    //
    //     double high = d;
    //     double low = 0;
    //     double result = d / 2;
    //     while (true) {
    //         double r = result * result * result;
    //         if (r > d) {
    //             high = result;
    //             double mid = (high + low) / 2;
    //             if (high - low < 0.1) {
    //                 result = mid;
    //                 break;
    //             }
    //             result = mid;
    //
    //         } else if (r < d) {
    //             low = result;
    //             double mid = (high + low) / 2;
    //             if (high - low < 0.1) {
    //                 result = mid;
    //                 break;
    //             }
    //             result = mid;
    //         } else {
    //             break;
    //         }
    //
    //     }
    //     System.out.println(String.format("%.1f", Double.valueOf( String.format("%.2f", result))));
    // }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     while (scanner.hasNext()){
    //         String[] ss = scanner.nextLine().split(" ");
    //         Integer a = Integer.parseInt(ss[0]);
    //         String x = ss[ss.length-2];
    //         Integer k = Integer.parseInt(ss[ss.length-1]);
    //         List<String> list = new ArrayList<>();
    //
    //         for (int i = 1; i <=a ; i++) {
    //             if (isBrother(x, ss[i])){
    //                 list.add(ss[i]);
    //             }
    //         }
    //         int size = list.size();
    //         System.out.println(size);
    //         if (size>=k){
    //             Collections.sort(list);
    //             System.out.println(list.get(k-1));
    //         }
    //         for (String  s : list) {
    //             System.out.println(s);
    //         }
    //
    //     }
    // }

    public static boolean isBrother(String x, String y) {
        if (x.length() != y.length() || y.equals(x)) {
            return false;
        }
        char[] s = x.toCharArray();
        char[] j = y.toCharArray();
        Arrays.sort(s);
        Arrays.sort(j);
        return new String(s).equals(new String(j));
    }

    // public static void main(String[] args) throws IOException{
    //     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    //     String s;
    //     while((s = br.readLine()) != null){
    //         char[] ch = s.toCharArray();
    //         char[] chars = new char[ch.length];
    //         int flag = 65, j=0;
    //         while(flag<=90){
    //             for(int i=0; i<ch.length; i++){
    //                 if((ch[i]>=65&&ch[i]<=90) || (ch[i]>=97&&ch[i]<=122)){
    //                     if(ch[i]==flag || ch[i]== flag+32){
    //                         chars[j] = ch[i];
    //                         j++;
    //                     }
    //                 }
    //             }
    //             flag++;
    //         }
    //
    //         j=0;
    //         for(int i=0; i<ch.length; i++){
    //             if((ch[i]>=65&&ch[i]<=90) || (ch[i]>=97&&ch[i]<=122)){
    //                 ch[i] = chars[j];
    //                 j++;
    //             }
    //         }
    //         System.out.println(String.valueOf(ch));
    //     }
    // }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     int count = scanner.nextInt();
    //     scanner.nextLine();
    //     String line = scanner.nextLine();
    //     scanner.close();
    //
    //     int cur = 1;
    //     int curTop = 1;
    //     int length = line.length();
    //     for (int i = 0; i < length; ++i) {
    //         char c = line.charAt(i);
    //         if (c == 'U') {
    //             if (cur == curTop) {
    //                 if (cur == 1) {
    //                     cur = count;
    //                 } else {
    //                     cur--;
    //                 }
    //                 if (count > 4) {
    //                     curTop = cur;
    //                 }
    //             } else {
    //                 if (cur == 1) {
    //                     cur = count;
    //                 } else {
    //                     cur--;
    //                 }
    //             }
    //         } else if (c == 'D') {
    //             if (cur == count) {
    //                 cur = 1;
    //             } else {
    //                 cur++;
    //             }
    //             if ((cur - curTop < 0 ? cur - curTop + count :  cur - curTop) == 4  && count > 4) {
    //                 if (curTop == count) {
    //                     curTop = 1;
    //                 } else {
    //                     curTop++;
    //                 }
    //             }
    //         }
    //     }
    //
    //     int min = 4 > count ? count : 4;
    //     int max = 4 > count ? 4 : count;
    //     for (int i = 0; i < min; ++i) {
    //         System.out.print(curTop + i > max ?  curTop + i - max : curTop + i);
    //         if (i != min - 1) {
    //             System.out.print(" ");
    //         }
    //     }
    //     System.out.println();
    //     System.out.println(cur);
    // }


    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     String line = scanner.nextLine();
    //     scanner.close();
    //     boolean lastCharIsLetter = false;
    //     StringBuffer buffer = null;
    //     List<String> list = new ArrayList<>();
    //     for (int i = 0; i < line.length(); ++i) {
    //         char c = line.charAt(i);
    //         if (c >= 'A' && c <= 'Z') {
    //             if (buffer == null) {
    //                 buffer = new StringBuffer();
    //             }
    //             lastCharIsLetter = true;
    //             buffer.append(c);
    //         } else if (c >= 'a' && c <= 'z') {
    //             if (buffer == null) {
    //                 buffer = new StringBuffer();
    //             }
    //             lastCharIsLetter = true;
    //             buffer.append(c);
    //         } else {
    //             // 1.第一个或连续的第 n 个字符为非字母
    //             // 2.单词结束后的第一个非字母
    //             // 3.单词结束后的第 n 个非字母(n > 1)
    //             if (lastCharIsLetter) {
    //                 list.add(buffer.toString());
    //                 buffer = new StringBuffer();
    //             }
    //             lastCharIsLetter = false;
    //         }
    //
    //     }
    //
    //     if (buffer != null && buffer.length() > 0) {
    //         list.add(buffer.toString());
    //     }
    //     for (int i = list.size() - 1; i >= 0; --i) {
    //         System.out.print(list.get(i));
    //         if (i == 0) {
    //             System.out.println();
    //         } else {
    //             System.out.print(" ");
    //         }
    //     }
    // }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     String line = scanner.nextLine();
    //     scanner.close();
    //
    //     // 1.将输入字符串按空格切割后合并
    //     String[] array = line.split(" ");
    //     line = array[0] + array[1];
    //
    //     // 2.分别对合并后的字串的奇数位和偶数位排序
    //     int length = line.length() / 2;
    //     char[] even = new char[line.length() % 2 == 0 ? length : length + 1];
    //     char[] odd = new char[length];
    //     for (int i = 0; i < line.length(); ++i) {
    //         if (i % 2 == 0) {
    //             even[i / 2] = line.charAt(i);
    //         } else {
    //             odd[i / 2] = line.charAt(i);
    //         }
    //     }
    //     Arrays.sort(even);
    //     Arrays.sort(odd);
    //
    //     // 3.对字符进行转换，直接将转换的源和目标放入数组，源值即为数组索引
    //     char[] map = new char['f' + 1];
    //     map['0'] = '0';
    //     map['1'] = '8';
    //     map['2'] = '4';
    //     map['3'] = 'C';
    //     map['4'] = '2';
    //     map['5'] = 'A';
    //     map['6'] = '9';
    //     map['7'] = 'E';
    //     map['8'] = '1';
    //     map['9'] = '9';
    //     map['A'] = '5';
    //     map['B'] = 'D';
    //     map['C'] = '3';
    //     map['D'] = 'B';
    //     map['E'] = '7';
    //     map['F'] = 'F';
    //     map['a'] = '5';
    //     map['b'] = 'D';
    //     map['c'] = '3';
    //     map['d'] = 'B';
    //     map['e'] = '7';
    //     map['f'] = 'F';
    //
    //     StringBuffer buffer = new StringBuffer();
    //     length = map.length;
    //     for(int i = 0; i < even.length; ++i) {
    //         if (even[i] < length && map[even[i]] != '\0') {
    //             buffer.append(map[even[i]]);
    //         } else {
    //             buffer.append(even[i]);
    //         }
    //         if ((i != even.length - 1 || even.length == odd.length)) {
    //             if (odd[i] < length && map[odd[i]] != '\0') {
    //                 buffer.append(map[odd[i]]);
    //             } else {
    //                 buffer.append(odd[i]);
    //             }
    //         }
    //     }
    //     System.out.println(buffer);
    // }


    // A类地址从1.0.0.0到126.255.255.255;
    // B类地址从128.0.0.0到191.255.255.255;
    // C类地址从192.0.0.0到223.255.255.255;
    // D类地址从224.0.0.0到239.255.255.255；
    // E类地址从240.0.0.0到255.255.255.255
    // 从10.0.0.0到10.255.255.255
    // 从172.16.0.0到172.31.255.255
    // 从192.168.0.0到192.168.255.255
    public static void main(String[] args) throws IOException {
        List<Long> masks = new ArrayList<>();
        masks.add(transferIP2Long("255.255.255.0"));
        masks.add(transferIP2Long("255.255.0.0"));
        masks.add(transferIP2Long("255.0.0.0"));
        long minIgnore1 = transferIP2Long("0.0.0.0");
        long maxIgnore1 = transferIP2Long("0.255.255.255");
        long minIgnore2 = transferIP2Long("127.0.0.0");
        long maxIgnore2 = transferIP2Long("127.255.255.255");
        long minA = transferIP2Long("1.0.0.0");
        long maxA = transferIP2Long("126.255.255.255");
        long minB = transferIP2Long("128.0.0.0");
        long maxB = transferIP2Long("191.255.255.255");
        long minC = transferIP2Long("192.0.0.0");
        long maxC = transferIP2Long("223.255.255.255");
        long minD = transferIP2Long("224.0.0.0");
        long maxD = transferIP2Long("239.255.255.255");
        long minE = transferIP2Long("240.0.0.0");
        long maxE = transferIP2Long("255.255.255.255");
        long minP1 = transferIP2Long("10.0.0.0");                        // > minA
        long maxP1 = transferIP2Long("10.255.255.255");         // < maxA
        long minP2 = transferIP2Long("172.16.0.0");                   // > minB
        long maxP2 = transferIP2Long("172.31.255.255");         // < maxB
        long minP3 = transferIP2Long("192.168.0.0");                // > minC
        long maxP3 = transferIP2Long("192.168.255.255");      // < maxC

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int[] result = new int[7];
        List<String> aList = new ArrayList<>();
        List<String> bList = new ArrayList<>();
        List<String> cList = new ArrayList<>();
        List<String> dList = new ArrayList<>();
        List<String> eList = new ArrayList<>();
        List<String> iList = new ArrayList<>();
        List<String> nList = new ArrayList<>();
        List<String> pList = new ArrayList<>();
        String ipAndMask;
        while ((ipAndMask = br.readLine()) != null && !"".equals(ipAndMask)) {
            String[] array = ipAndMask.split("~");
            long longOfIp = transferIP2Long(array[0]);
            long longOfMask = transferIP2Long(array[1]);

            if (longOfIp >= minIgnore1 && longOfIp <= maxIgnore1 || longOfIp >= minIgnore2 && longOfIp <= maxIgnore2) {
                iList.add(ipAndMask);
                continue;
            }

            if (!masks.contains(longOfMask)) {
                result[5]++;
                nList.add(ipAndMask);
                continue;
            }
            if (longOfIp == -1) {
                result[5]++;
                nList.add(ipAndMask);
            } else if (longOfIp >= minP1 && longOfIp <= maxP1 || longOfIp >= minP2 && longOfIp <= maxP2 || longOfIp >= minP3 && longOfIp <= maxP3) {
                result[6]++;
                pList.add(ipAndMask);
            } else if (longOfIp >= minA && longOfIp <= maxA) {
                result[0]++;
                aList.add(ipAndMask);
            } else if (longOfIp >= minB && longOfIp <= maxB) {
                result[1]++;
                bList.add(ipAndMask);
            } else if (longOfIp >= minC && longOfIp <= maxC) {
                result[2]++;
                cList.add(ipAndMask);
            } else if (longOfIp >= minD && longOfIp <= maxD) {
                result[3]++;
                dList.add(ipAndMask);
            } else if (longOfIp >= minE && longOfIp <= maxE) {
                result[4]++;
                eList.add(ipAndMask);
            }
        }
        br.close();
        for (int i : result) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println(aList);
        System.out.println(bList);
        System.out.println(cList);
        System.out.println(dList);
        System.out.println(eList);
        System.out.println(iList);
        System.out.println(nList);
        System.out.println(pList);
    }

    private static long transferIP2Long(String ip) {
        String regex255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";
        String regex = regex255 + "(\\." + regex255 + "){3}";
        if (!Pattern.compile(regex).matcher(ip).matches()) {
            return -1;
        }

        String[] array = ip.split("\\.");
        long longOfIp = (Long.parseLong(array[0]) << 24) + (Long.parseLong(array[1]) << 16) + (Long.parseLong(array[2]) << 8) + (Long.parseLong(array[3]));
        // log.info(">>>>> longOfIp: [{}]", longOfIp);
        return longOfIp;
    }

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);
    //     int size = scanner.nextInt();
    //     int[] array = new int[size];
    //     for (int i = 0; i < size; ++i) {
    //         array[i] = scanner.nextInt();
    //     }
    //     quickSort(array);
    //     for (int j : array) {
    //         System.out.println(j);
    //     }
    // }

    private int sumBySynchronized;
    private AtomicInteger atomicSum = new AtomicInteger();
    private volatile int volatileSum;
    private synchronized void addToSum(int n) {
        sumBySynchronized += n;
    }

    @Test
    public void add1To100Using10Threads() {
        for (int i = 1; i <= 100; i = i + 10) {
            MyThread myThread = new MyThread(i);
            myThread.start();
        }

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
        log.info(">>>>> SUM_BY_SYNCHRONIZED: [{}]", sumBySynchronized);
        log.info(">>>>> ATOMIC_SUM: [{}]", atomicSum);
        log.info(">>>>> VOLATILE_SUM: [{}]", volatileSum);

        int result = 0;
        for (int i = 1; i <= 100; ++i) {
            result += i;
        }
        log.info(">>>>> CORRECT_RESULT: [{}]", result);
    }

    class MyThread extends Thread {
        private int start;

        public MyThread(int start) {
            this.start = start;
        }

        @Override
        public void run() {
            int sum = 0;
            for (int i = 0; i < 10; ++i) {
                sum += start++;
            }
            // log.info(">>>>> THREAD_NAME: [{}], SUM: [{}]", Thread.currentThread().getName(), sum);

            addToSum(sum);                              // method1
            atomicSum.addAndGet(sum);           // method2

            // 这里有问题, 编译器都提示 += 不是原子操作, 所以即使 v 是 volatile, 某线程修改了 v, 会保证其他线程读到的 v 一定是修改过后的 v,
            // 但是 v += sum 是读后再加 sum, 有可能读到修改后的 v 后, 又被其他线程抢到 CPU 时间片, 且又修改了 v, 那么当前线程计算时的 v 跟主内存中的 v 不一致
            // 本例中将线程数设置到足够大才可以使问题复现
            volatileSum += sum;                             // method3
        }
    }

    private static volatile int number = 0;
    private static void increase() {
        number++;
    }

    @Test
    public void volatileNotSuitable() {
        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int i1 = 0; i1 < 1000; i1++) {
                    increase();
                }
            });
            threads[i].start();
        }

        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
        log.info(">>>>> NUMBER: [{}]", number);
    }

    @Test
    public void ensureChildThreadCouldExecuteComplete() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    log.info(">>>> sleep 3 s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        // method1
        // thread.join();

        // method2
        while(Thread.activeCount() > 1) {
            Thread.yield();
        }

        // method3
        // Thread.currentThread().join();
    }

}
