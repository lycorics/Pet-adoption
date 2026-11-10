package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Rescue;
import com.example.springboot.service.IRescueService;
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
@RequestMapping("/rescue")
public class RescueController {

    @Resource
    private IRescueService rescueService;

    @PostMapping
    public Result save(@RequestBody Rescue rescue) {
        if (rescue.getId() == null){
            rescue.setTime(DateUtil.now());
            rescue.setUserId(TokenUtils.getCurrentUser().getId());
            rescue.setStatus("待援助");
        }
        return Result.success(rescueService.saveOrUpdate(rescue));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(rescueService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(rescueService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(rescueService.list());
    }

    @GetMapping("/ok/{id}")
    public Result ok(@PathVariable Integer id) {
        Rescue rescue = rescueService.getById(id);
        rescue.setStatus("已援助");
        rescueService.updateById(rescue);
        return Result.success();
    }

    @GetMapping("/no/{id}")
    public Result no(@PathVariable Integer id) {
        Rescue rescue = rescueService.getById(id);
        rescue.setStatus("已取消");
        rescueService.updateById(rescue);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(rescueService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Rescue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Rescue::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Rescue::getName, keyword);
        }

        return Result.success(rescueService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }



    @GetMapping("/my/page")
    public Result findMyPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {

        LambdaQueryWrapper<Rescue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Rescue::getId);
        queryWrapper.eq(Rescue::getUserId,TokenUtils.getCurrentUser().getId());
        return Result.success(rescueService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

