package tech.wetech.weshop.wechat.service;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.wetech.weshop.wechat.dto.HomeCategoryDTO;
import tech.wetech.weshop.wechat.dto.HomeIndexDTO;
import tech.wetech.weshop.mapper.*;
import tech.wetech.weshop.po.*;
import tech.wetech.weshop.utils.Reflections;
import tk.mybatis.mapper.weekend.Weekend;
import tk.mybatis.mapper.weekend.WeekendCriteria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public HomeIndexDTO index() {

        List<Ad> bannerList = adMapper.select(new Ad().setAdPositionId((short) 1));

        PageHelper.orderBy("sort_order asc");
        List<Channel> channelList = channelMapper.selectAll();

        PageHelper.startPage(1, 4);
        List<Goods> newGoodsList = goodsMapper.select(new Goods().setNewly(true));

        PageHelper.startPage(1, 4);
        List<Goods> hotGoodsList = goodsMapper.select(new Goods().setHot(true));

        PageHelper.orderBy("new_sort_order asc");
        List<Brand> brandList = brandMapper.select(new Brand().setNewly(true));

        PageHelper.startPage(1, 3);
        List<Topic> topicList = topicMapper.selectAll();

        List<HomeCategoryDTO> categoryList = new LinkedList<>();

        categoryMapper.select(
                new Category().setParentId(0)
        ).forEach(c -> {

            List<Integer> categoryIdList = categoryMapper.select(new Category().setParentId(c.getId())).stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());

            PageHelper.startPage(1, 7);
            Weekend<Goods> goodsWeekend = Weekend.of(Goods.class);
            goodsWeekend.selectProperties(Reflections.fnToFieldName(
                    Goods::getId,
                    Goods::getName,
                    Goods::getListPicUrl,
                    Goods::getRetailPrice));
            WeekendCriteria<Goods, Object> criteria = goodsWeekend.weekendCriteria();
            criteria.andIn(Goods::getCategoryId, categoryIdList);
            List<Goods> goodsList = goodsMapper.selectByExample(goodsWeekend);
            categoryList.add(new HomeCategoryDTO(c.getId(), c.getName(), goodsList));
        });

        return new HomeIndexDTO().setBannerList(bannerList)
                .setChannelList(channelList)
                .setNewGoodsList(newGoodsList)
                .setHotGoodsList(hotGoodsList)
                .setBrandList(brandList)
                .setTopicList(topicList)
                .setCategoryList(categoryList);
    }

}
