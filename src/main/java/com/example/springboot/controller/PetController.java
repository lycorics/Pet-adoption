package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.Pet;
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
@RequestMapping("/pet")
public class PetController {

    @Resource
    private IPetService petService;

    @PostMapping
    public Result save(@RequestBody Pet pet) {
        if (pet.getId() == null) {
            Account account = TokenUtils.getCurrentUser();
            if (!StrUtil.equals(account.getRole(),"ROLE_ADMIN")){
                pet.setUserId(account.getId());
                pet.setCategory("个人");
            }else {
                pet.setCategory("官方");
            }
            pet.setTime(DateUtil.now());
            pet.setStatus("待领养");
        }
        return Result.success(petService.saveOrUpdate(pet));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(petService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(petService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(petService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(petService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Pet::getId);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Pet::getName, keyword);
        }

        return Result.success(petService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/front/page")
    public Result findFrontPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "所有") String sex,
                           @RequestParam(defaultValue = "所有") String category,
                           @RequestParam(defaultValue = "") String address ) {

        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Pet::getId);
        queryWrapper.eq(Pet::getStatus,"待领养");
        if (!StrUtil.equals("所有",sex)) {
            queryWrapper.eq(Pet::getSex, sex);
        }
        if (!StrUtil.equals("所有",category)) {
            queryWrapper.eq(Pet::getCategory, category);
        }
        if (StrUtil.isNotBlank(address)) {
            queryWrapper.like(Pet::getAddress, address);
        }
        return Result.success(petService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/my/page")
    public Result findMyPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {
        LambdaQueryWrapper<Pet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Pet::getId);
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Pet::getName, keyword);
        }
        queryWrapper.eq(Pet::getUserId, TokenUtils.getCurrentUser().getId());
        queryWrapper.eq(Pet::getCategory,"个人");
        return Result.success(petService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }




}

