package cn.soboys.simplest.jpa.jpalazy;

import cn.soboys.simplest.core.domain.BaseVo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: siyulong
 * @date: 2021/4/9 16:22
 * @see JsonBody
 **/
@Getter
@Setter
public class JsonBodyVo extends BaseVo {

    private String root = "result[]";

    private String expression = "";

    private Integer maxLevel = 5;

    private Boolean useCustom = false;

    private Boolean ignoreLazy = false;

    private JsonBody.Strategy strategy = JsonBody.Strategy.DEFAULT_NONE;

}
