package cn.soboys.simplest.jpa.jpalazy;

import java.io.Serializable;

/**
 * @Author <a href="mailto:ksmwly@gmail.com">lengyu</a>
 * @Creation date: 2008年01月14日 下午5:33:43
 * @Intro 通过反射机制把Entity转为JSON数据格式，默认只要是公共(public)的get方法都转换
 */

//    xxx实体类继承BaseEntigy后自动继承该接口，需要个性化json重写父类接口，如下是示例
//    public Object toJSonObject() {
//        Map<String, Object> map = CommUtil
//        .obj2mapExcept(this, new String[] {"manager", "belongTo", "region", "creator", "checker", "trade", "parent", "roles"});
//
//        if (region !=   null) {
//        map.put("region", CommUtil.obj2map(region, new String[] {"id", "sn", "title"}));
//        }
//        if (trade != null) {
//        map.put("trade", trade.toJSonObject());
//        }
//        if (parent != null) {
//        map.put("parent", CommUtil.obj2map(parent, new String[] {"id", "code", "title"}));
//        }
//        return map;
//    }


public interface IJsonObject extends Serializable {
    /**
     * 实现此方法调用CommUtil.obj2mapExcept或CommUtil.obj2map两个方法来过滤或允许数据对象
     *
     * @return
     */
    Object toJSonObject();
}