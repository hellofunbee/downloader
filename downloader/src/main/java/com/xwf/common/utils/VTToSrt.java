package com.xwf.common.utils;

import subtitleFile.FormatSRT;
import subtitleFile.IOClass;
import subtitleFile.TimedTextObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifengxu on 2018/9/6.
 */
public class VTToSrt {
    static String path = CommonUtils.getPathByKey("srt_path");
    static String temp = CommonUtils.getPathByKey("srt_path") + "temp.srt";

    public static void main(String[] args) {

        List<File> files = CommonUtils.getMp4FileList(path, new ArrayList<File>(), "vtt");

        String line = null;
        for (File file : files) {
            // file = new File(file.getParent() + "/Obama Foundation Summit _ Morning Session - Business & Society-N9n7_xpxdpM.en.vtt");

            String o_srt = file.getAbsolutePath().replace(".vtt", ".srt");
            boolean start = false;
            try {
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));

                List<String> result = new ArrayList<String>();
                int i = 1;
                while ((line = br.readLine()) != null) {
                    if (!start && !line.startsWith("0"))
                        continue;


                    if (line.startsWith("0") && line.indexOf("-->") != -1) {

                        line = line.substring(0, 29);

                        if (!start)
                            result.add(String.valueOf(i) + "\n" + line);
                        else
                            result.add("\n" + String.valueOf(i) + "\n" + line);
                        i++;

                        start = true;
                    } else {

                        line = line.replaceAll("<(.*?)>", " ").replaceAll("\n", " ").replaceAll(" +", " ");
                        if (line.length() <= 0 || line.equals(" ")) {
                            continue;
                        }
                        result.add(line);

                    }
                }

                CommonUtils.writeAsLine(temp, result);

                //To test the correct implementation of the SRT parser and writer.
                FormatSRT ttff = new FormatSRT();
                InputStream is = new FileInputStream(temp);
                TimedTextObject tto = ttff.parseFile(file.getName().replace(".vtt", ".srt"), is);
                IOClass.writeFileTxt(o_srt, tto.toSRT());


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(line);
            }


        }
    }


}
