package com.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Application;
import com.example.springboot.entity.Pet;
import com.example.springboot.service.IApplicationService;
import com.example.springboot.service.IPetService;
import com.example.springboot.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    private IApplicationService applicationService;
    @Resource
    private IPetService petService;

    @PostMapping
    public Result save(@RequestBody Application application) {
        if (application.getId() == null) {
            application.setStatus("待审核");
            application.setUserId(TokenUtils.getCurrentUser().getId());
            Pet pet = petService.getById(application.getPetId());
            application.setCategory(pet.getCategory());
            application.setFromId(pet.getUserId());
            pet.setStatus("领养审核中");
            petService.updateById(pet);
        }
        return Result.success(applicationService.saveOrUpdate(application));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(applicationService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(applicationService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(applicationService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(applicationService.getById(id));
    }

    @GetMapping("/ok/{id}")
    public Result ok(@PathVariable Integer id) {
        Application application = applicationService.getById(id);
        application.setStatus("已通过");
        applicationService.updateById(application);
        Pet pet = petService.getById(application.getPetId());
        pet.setStatus("已领养");
        petService.saveOrUpdate(pet);
        return Result.success();
    }

    @GetMapping("/no/{id}")
    public Result no(@PathVariable Integer id) {
        Application application = applicationService.getById(id);
        application.setStatus("已拒绝");
        applicationService.updateById(application);
        Pet pet = petService.getById(application.getPetId());
        pet.setStatus("待领养");
        petService.saveOrUpdate(pet);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Application::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Application::getName, keyword);
        }

        return Result.success(applicationService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/my/page")
    public Result findMyPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "我发出的") String type) {
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Application::getId);
        if (StrUtil.equals("我收到的",type)) {
            queryWrapper.eq(Application::getFromId,TokenUtils.getCurrentUser().getId());
        }
        if (StrUtil.equals("我发出的",type)) {
            queryWrapper.eq(Application::getUserId,TokenUtils.getCurrentUser().getId());
        }
        return Result.success(applicationService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

