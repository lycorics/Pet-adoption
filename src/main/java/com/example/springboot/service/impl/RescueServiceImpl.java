package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Rescue;
import com.example.springboot.mapper.RescueMapper;
import com.example.springboot.service.IRescueService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class RescueServiceImpl extends ServiceImpl<RescueMapper, Rescue> implements IRescueService {
    
}
