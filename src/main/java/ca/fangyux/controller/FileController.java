package ca.fangyux.controller;

import ca.fangyux.Utils.R;
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
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Value("${ruiji.path}")
    private String uploadDir;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("接收文件..."+file.toString());

        //获取上传文件的后缀
        String originalFileName=file.getOriginalFilename();
        String suffix=originalFileName.substring(originalFileName.lastIndexOf("."));

        //使用UUID重新命名文件，避免文件重复
        String fileName= UUID.randomUUID().toString()+suffix;

        //因为上传的文件是临时文件，在请求结束后会自动删除。我们需要转存文件
        File dir=new File(uploadDir);

        if(!dir.exists()){
            dir.mkdirs();
        }

        try{
            file.transferTo((new File(uploadDir+fileName)));
        }catch (IOException e){
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流：从磁盘中读取文件
            File file=new File(uploadDir+name);
            FileInputStream fis=new FileInputStream(file);

            //输出流：通过输出流将文件写回浏览器
            ServletOutputStream os=response.getOutputStream();

            response.setContentType("image/"+file.getName().substring(file.getName().lastIndexOf(".")));

            int len=0;
            byte[] bytes=new byte[1024];

            while((len=fis.read(bytes))!=-1){
                os.write(bytes,0,len);
                os.flush();
            }

            os.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}