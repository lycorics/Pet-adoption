package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Trend;
import com.example.springboot.service.ITrendService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/trend")
public class TrendController {

    @Resource
    private ITrendService trendService;

    @PostMapping
    public Result save(@RequestBody Trend trend) {
        if (trend.getId() == null) {
            trend.setTime(DateUtil.now());
        }
        return Result.success(trendService.saveOrUpdate(trend));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(trendService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(trendService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(trendService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(trendService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Trend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Trend::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Trend::getName, keyword);
        }

        return Result.success(trendService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

