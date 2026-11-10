package com.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Health;
import com.example.springboot.service.IHealthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Resource
    private IHealthService healthService;

    @PostMapping
    public Result save(@RequestBody Health health) {
        return Result.success(healthService.saveOrUpdate(health));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(healthService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(healthService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(healthService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(healthService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Health> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Health::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Health::getName, keyword);
        }

        return Result.success(healthService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

