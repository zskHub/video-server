package com.winnerdt.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author:zsk
 * @CreateTime:2019-05-23 13:53
 */
@Slf4j
public class DataBaseUtil {
    //本实例支持Linux环境和Windows环境
    public static void main(String[] args) throws Exception {
        //测试备份
//        String savePath = "C:\\Users\\user\\Desktop\\test\\test.sql";
//
//        String ip = "localhost";
//        String port = "3306";
//        String userName = "root";
//        String password = "admin";
//        String dataBaseName = "test";
//
//        boolean b1 = new DataBaseUtil().backupAll(ip,port,userName,password,dataBaseName,false, savePath);
//        if(b1){
//            log.info("备份成功");
//        }else {
//            log.info("备份失败");
//        }

        //测试还原
        String ip = "localhost";
        String port = "3306";
        String userName = "root";
        String password = "admin";
        String dataBaseName = "test";
        String savePath = "C:\\Users\\user\\Desktop\\test\\test.sql";
        boolean b2 = new DataBaseUtil().recover(ip,port,userName,password,dataBaseName,savePath);
        if(b2){
            log.info("还原成功");
        }else {
            log.info("还原失败");
        }
    }

    /**
     * 备份整个数据库,注意只能是linux系统才能正确压缩，window不要开启压缩
     *
     * @param ip  数据库ip地址
     * @param port 数据库端口号
     * @param userName 数据库用户名
     * @param password 数据库密码
     * @param dataBaseName 数据库名称
     * @param isZip 是否压缩，true：压缩，false：不压缩
     * @param savePath 备份路径
     * @return
     */
    public boolean backupAll(String ip,String port,String userName,String password,String dataBaseName,boolean isZip, String savePath) {


        //拼接命令
        StringBuffer commandStrBuf = new StringBuffer();
        String command = null;
        if(isZip){
            command = commandStrBuf.append("mysqldump -h")
                    .append(ip)
                    .append(" -P "+port)
                    .append(" -u"+userName)
                    .append(" -p"+password)
                    .append(" "+dataBaseName+" | gzip")
                    .toString();
        }else {
            command = commandStrBuf.append("mysqldump -h")
                    .append(ip)
                    .append(" -P "+port)
                    .append(" -u"+userName)
                    .append(" -p"+password)
                    .append(" "+dataBaseName)
                    .toString();
        }

        log.info(command);
        boolean flag;
        // 获得与当前应用程序关联的Runtime对象
        Runtime r = Runtime.getRuntime();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            // 在单独的进程中执行指定的字符串命令
            Process p = r.exec(command);
            // 获得连接到进程正常输出的输入流，该输入流从该Process对象表示的进程的标准输出中获取数据
            InputStream is = p.getInputStream();
            // InputStreamReader是从字节流到字符流的桥梁：它读取字节，并使用指定的charset将其解码为字符
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            //BufferedReader从字符输入流读取文本，缓冲字符，提供字符，数组和行的高效读取
            br = new BufferedReader(isr);
            String s;
            // 创建文件输出流
            FileOutputStream fos = new FileOutputStream(savePath);
            // OutputStreamWriter是从字符流到字节流的桥梁，它使用指定的charset将写入的字符编码为字节
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            // BufferedWriter将文本写入字符输出流，缓冲字符，以提供单个字符，数组和字符串的高效写入
            bw = new BufferedWriter(osw);

            while ((s = br.readLine()) != null) {
                bw.write(s + System.lineSeparator());
            }

            bw.flush();
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            //由于输入输出流使用的是装饰器模式，所以在关闭流时只需要调用外层装饰类的close()方法即可，
            //它会自动调用内层流的close()方法
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * mysql还原
     *
     * @param ip  数据库ip地址
     * @param port 数据库端口号
     * @param userName 数据库用户名
     * @param password 数据库密码
     * @param dataBaseName 数据库名称
     * @param savePath 备份路径
     * @return
     */
    public boolean recover(String ip,String port,String userName,String password,String dataBaseName,String savePath) {
        //拼接参数
        //拼接命令
        StringBuffer commandStrBuf = new StringBuffer();
        String command = null;
        command = commandStrBuf.append("mysql -h")
                .append(ip)
                .append(" -P "+port)
                .append(" -u"+userName)
                .append(" -p"+password)
                .append(" --default-character-set=utf8 ")
                .append(dataBaseName)
                .toString();


        log.info(command);


        boolean flag;
        Runtime r = Runtime.getRuntime();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            Process p = r.exec(command);
            OutputStream os = p.getOutputStream();
            FileInputStream fis = new FileInputStream(savePath);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            br = new BufferedReader(isr);
            String s;
            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
            bw = new BufferedWriter(osw);

            while ((s = br.readLine()) != null) {
                bw.write(s + System.lineSeparator());
            }
            bw.flush();
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

}
