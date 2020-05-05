package com.xwf.common.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwf.common.http.product.DYProduct;
import com.xwf.common.http.product.Elastic_images;
import com.xwf.common.http.product.Visitor;
import com.xwf.common.utils.CommonUtils;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by weifengxu on 2018/4/10.
 */
public class DownMylike {
    static String dir = "/Volumes/自媒体/temp/douyin/xihuan/08-31-product/";
    static String dir2 = "/Volumes/自媒体/temp/douyin/xihuan/08-31-idle/";
    static int cc = 0;

    public static void main(String args[]) throws InterruptedException, IOException {
        cc = 0;
        JSONObject jo = null;
        String text = null;
        JSONArray ja = JSON.parseArray(FileUtils.readFileToString(new File(new DownMylike().getPath())));
        for (int n = 0; n < ja.size(); n++) {
            try {
                JSONObject obj = ja.getJSONObject(n);
                text = obj.getJSONObject("response").getJSONObject("body").getString("encoded");

                text = CommonUtils.decode(text, "utf-8");
                jo = JSON.parseObject(text);
                downloader(jo);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("JSON 错误！");
            }
        }
        webp2png();
    }


    public static void downloader(JSONObject jo) throws Exception {
        JSONArray jsonArray = null;
        jsonArray = jo.getJSONArray("aweme_list");

        int i = 0;

        if (jsonArray != null && jsonArray.size() > 0) {
            String desc = "";
            JSONObject jobject = null;
            JSONObject author = null;
            String fm = null;
            String cfm = null;
            String productDir = null;
            for (Object obj : jsonArray) {
                try {
                    cc++;
                    jobject = (JSONObject) obj;
                    desc = jobject.getString("desc");
                    author = jobject.getJSONObject("author");
                    desc = author.getString("uid") + "--" + author.getString("short_id") + "--" + desc;

                    JSONObject vUrl = jobject.getJSONObject("video").getJSONObject("play_addr");
                    if (vUrl != null) {

                        JSONArray videos = jobject.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
                        JSONArray covers = jobject.getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list");

                        //目录已经存在，则跳过
                        CommonUtils.mkDirectory(dir);
                        CommonUtils.mkDirectory(dir2);

                        cfm = dir + desc + ".webp";

                        productDir = dir + desc + "/";

                        /*
                        HttpUtils.downLoad(covers.getString(0), cfm);
                        HttpUtils.downLoad(videos.getString(0), fm);*/

                        System.out.println(desc);

                        /*simple_promotions*/
                        if (jobject.get("simple_promotions") != null) {
                            JSONArray products = JSONArray.parseArray(jobject.getString("simple_promotions"));
                            if (products.size() > 0) {
                                fm = dir + desc + ".mp4";
                                HttpUtils.downLoad(videos.getString(0), fm);
                                saveProduct(productDir, products, videos.getString(0));
                            }
                        } else {
                            fm = dir2 + desc + ".mp4";
                            HttpUtils.downLoad(videos.getString(0), fm);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 保存产品信息
     *
     * @param dir
     * @param products
     */
    private static void saveProduct(String dir, JSONArray products, String videoUrl) throws IOException {
        CommonUtils.mkDirectory(dir);
        JSONObject jo;
        for (Object obj : products) {
            jo = (JSONObject) obj;
            DYProduct p = JSONObject.parseObject(jo.toJSONString(), DYProduct.class);
            int i = 0;
            /*图片*/
            for (Elastic_images img : p.getElastic_images()) {
                i++;
                String pic = img.getUrl_list().get(0);
                HttpUtils.downLoad(pic, dir + i + ".jpg");
            }
            /*价格*/
            BigDecimal price = p.getPrice();
            String title = p.getTitle();
            int sales = p.getSales();
            Visitor visitor = p.getVisitor();
            List<String> labels = p.getLabel();
            String product_id = p.getProduct_id();
            StringBuffer sb = new StringBuffer();
            sb.append("price:" + price + "\n");
            sb.append("title:" + title + "\n");
            sb.append("sales:" + sales + "\n");
            sb.append("visitor:" + visitor.getCount() + "\n");
            sb.append("labels:" + (labels == null ? "" : labels.toString()) + "\n");
            sb.append("product_id:" + product_id + "\n");

            CommonUtils.writeString(sb.toString(), dir + "info.text");
            HttpUtils.downLoad(videoUrl, dir + "video.mp4");
            System.out.println(title);
        }
    }

    private static void webp2png() {

        System.out.println(System.getProperty("java.library.path"));

        File f = new File(dir);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File pic : files) {
                String name = pic.getName();
                if (name != null && name.endsWith(".webp")) {
                    try {
                        BufferedImage im = ImageIO.read(pic);
                        ImageIO.write(im, "png", new File(pic.getAbsolutePath().replace(".webp", ".png")));

                        pic.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private String getPath() {
        return "/Volumes/自媒体/temp/xihuanjson/Untitled.chlsj";
    }

}
