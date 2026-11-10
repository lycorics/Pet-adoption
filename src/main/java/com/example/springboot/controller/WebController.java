package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Account;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.service.IUserService;
import com.example.springboot.service.IAdminService;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web")
public class WebController {

    private static final String FILE_UPLOAD_PATH = System.getProperty("user.dir") + File.separator + "/files/";

    @Value("${ip}")
    String ip;

    @Value("${server.port}")
    String port;

    @Resource
    private IUserService userService;
    @Resource
    private IAdminService adminService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword()) || StrUtil.isBlank(account.getRole())) {
            return Result.error(Constants.CODE_400, "参数错误");
        }

        if (StrUtil.equals(account.getRole(),"ROLE_USER")) {
            account = userService.login(account);
        }
        if (StrUtil.equals(account.getRole(),"ROLE_ADMIN")) {
            account = adminService.login(account);
        }

        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword()) || StrUtil.isBlank(account.getRole())) {
            return Result.error(Constants.CODE_400, "参数错误");
        }

        if (StrUtil.equals(account.getRole(),"ROLE_USER")) {
            userService.register(account);
        }
        if (StrUtil.equals(account.getRole(),"ROLE_ADMIN")) {
            adminService.register(account);
        }

        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getPassword()) || StrUtil.isBlank(account.getNewPassword())) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        Account one = TokenUtils.getCurrentUser();
        account.setUsername(one.getUsername());

        if (StrUtil.equals(one.getRole(),"ROLE_USER")) {
            userService.updatePassword(account);
        }
        if (StrUtil.equals(one.getRole(),"ROLE_ADMIN")) {
            adminService.updatePassword(account);
        }

        return Result.success();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userInfo")
    public Result userInfo() {
        Account account = TokenUtils.getCurrentUser();

        if (StrUtil.equals(account.getRole(),"ROLE_USER")) {
            return Result.success(userService.getById(account.getId()));
        }
        if (StrUtil.equals(account.getRole(),"ROLE_ADMIN")) {
            return Result.success(adminService.getById(account.getId()));
        }

        return Result.error(Constants.CODE_605,"获取用户信息失败");
    }


    /**
     * 文件上传接口
     *
     * @param file 前端传递过来的文件
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);

        // 定义一个文件唯一的标识码
        String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;

        File uploadFile = new File(FILE_UPLOAD_PATH + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        // 文件保存到磁盘
        file.transferTo(uploadFile);
        // 拼接文件地址：优先使用当前请求的 host，保证从任意地址访问都能打开
        String host = StrUtil.isNotBlank(request.getServerName()) ? request.getServerName() : ip;
        int serverPort = request.getServerPort() > 0 ? request.getServerPort() : Integer.parseInt(port);
        String url = "http://" + host + ":" + serverPort + "/web/download/" + fileUUID;
        // 返回文件地址
        return url;
    }

    /**
     * 文件下载接口
     *  @param fileUUID 根据文件UUID找到特定文件
     */
    @GetMapping("/download/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(FILE_UPLOAD_PATH + fileUUID);
        // 文件不存在时返回 404，而不是 200 空内容（避免前端显示破损图标）
        if (!uploadFile.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 按扩展名返回正确的 MIME 类型，图片可直接在浏览器中显示
        String ext = FileUtil.extName(fileUUID);
        String contentType = switch (ext == null ? "" : ext.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "mp4" -> "video/mp4";
            default -> "application/octet-stream";
        };
        response.setContentType(contentType);
        ServletOutputStream os = response.getOutputStream();
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

}
