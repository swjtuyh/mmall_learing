package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by yinhuan on 2018/4/20.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
         if (StringUtils.isBlank(categoryName) || parentId == null){
             return ServerResponse.createByErrorMessgae("参数从错误");
         }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessgae("添加品类成功");
        }
        return ServerResponse.createByErrorMessgae("添加产品失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if (categoryId == null){
            return ServerResponse.createByErrorMessgae("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessgae("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessgae("更新品类名称失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前品类的子品类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<Integer>> getChildrenDeepCategory(Integer categoryId){
        if (categoryId != null){
            Set<Category> categorySet = Sets.newHashSet();
            findChildCategory(categorySet,categoryId);
            List<Integer> categoryIdList = Lists.newArrayList();
            for (Category categoryItem  :
                    categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
            return ServerResponse.createBySuccess(categoryIdList);
        }
        return ServerResponse.createByErrorMessgae("品类ID为空");
    }

    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem :
                categoryList) {
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}
