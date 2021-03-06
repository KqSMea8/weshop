package tech.wetech.weshop.wechat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.wetech.weshop.wechat.service.CategoryService;
import tech.wetech.weshop.utils.Result;
import tech.wetech.weshop.wechat.dto.CategoryIndexDTO;
import tech.wetech.weshop.wechat.dto.CategoryDTO;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/wechat/catalog")
@Validated
public class WechatCatalogController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/index")
    public Result<CategoryIndexDTO> index(Integer id) {
        return Result.success(categoryService.index(id));
    }

    @GetMapping("/current")
    public Result<CategoryDTO> current(@NotNull @RequestParam("id") Integer id) {
        return Result.success(categoryService.current(id));
    }

}
