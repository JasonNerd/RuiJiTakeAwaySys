package com.rain.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.reggie.common.R;
import com.rain.reggie.dto.DishDto;
import com.rain.reggie.entity.Category;
import com.rain.reggie.entity.Dish;
import com.rain.reggie.service.CategoryService;
import com.rain.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService service;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String name){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        Page<Dish> info = new Page<>(page, pageSize);
        wrapper.like(name!=null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        service.page(info);

        Page<DishDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(info, dtoPage, "records");
        List<Dish> dishes = info.getRecords();
        List<DishDto> records = new ArrayList<>();
        for (Dish d: dishes){
            Long cat_id = d.getCategoryId();
            Category category = categoryService.getById(cat_id);
            DishDto rec = new DishDto();
            BeanUtils.copyProperties(d, rec);
            rec.setCategoryName(category.getName());
            records.add(rec);
        }
        dtoPage.setRecords(records);
        return R.success(dtoPage);
    }

    @PostMapping
    public R<String> insert(@RequestBody DishDto dishDto){
        log.info("接收到菜品信息: {}", dishDto);
        service.addWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("{dishId}")
    public R<DishDto> getDishById(@PathVariable Long dishId){
        log.info("获取菜品信息(包括口味){}", dishId);
        DishDto dishDto = service.getDishWithFlavor(dishId);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("更新菜品: {}", dishDto);
        service.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }
}
