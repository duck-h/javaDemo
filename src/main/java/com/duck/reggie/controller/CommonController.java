package com.duck.reggie.controller;


/*文件上傳/下載*/

import com.duck.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j

public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    //文件上傳
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        /*                              file是一個臨時文件，需要轉存到指定位置
         * 否則本次請求完成後臨時文件會刪除*/
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        //使用UUID重新產生文件名，避免文件名重複被覆蓋
        String uuidFilename = UUID.randomUUID().toString() + suffix;

        //創建目錄對象
        File dir = new File(basePath);
        //判斷當前目錄是否存在
        if (!dir.exists()) { //目錄不存在
            dir.mkdirs();
        }

        try {
            //臨時文件轉存到指定位置
            file.transferTo(new File(basePath + uuidFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(uuidFilename);
    }

    //文件下載
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        //輸入流，通過輸入流讀取文件內容
        try {
            FileInputStream fileInputStream =
                    new FileInputStream(new File(basePath + name));

            //輸出流，通過輸出流將文件寫回瀏覽器，在瀏覽器中顯示圖片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //關閉資源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
