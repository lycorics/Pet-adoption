package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Trend;
import com.example.springboot.mapper.TrendMapper;
import com.example.springboot.service.ITrendService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class TrendServiceImpl extends ServiceImpl<TrendMapper, Trend> implements ITrendService {
    
}
